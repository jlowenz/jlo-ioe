package jlo.ioe.data

import java.util.Date
import java.rmi.server.ObjID
import scala.collection.immutable.TreeMap
import scala.Predef.{boolean,int}
import scala.Predef.identity
import scala.Predef.boolean2Boolean
import java.io.{ObjectOutputStream,ObjectInputStream}
import jlo.ioe.ui.Observable // XXX refactor
import jlo.ioe.ui.Observer // XXX refactor
import jlo.ioe.ui.ObservableEvent
import jlo.ioe.View
import jlo.ioe.command._

object DataObject {
  val kOwner = 'owner
  val kCreated = 'created
  val kModified = 'modified
  val kAccessed = 'accessed
  val kModifier = 'modifier
  val kShared = 'shared
  val kKind = 'kind
  val kTitle = 'title
  val kDescription = 'description
  val kBaseMetadata = List(kOwner,kCreated,kModified,kAccessed,kModifier,kShared,kKind,kTitle,kDescription)

  class ComparableSymbol(sym:Symbol) extends Ordered[Symbol] {
    def compare(that:Symbol) = sym.name.compareTo(that.name)
  }
  class ComparableString(s:String) extends Ordered[String] {
    def compare(that:String) = s.compareTo(that)
  }
  
  implicit def toComparable(s:Symbol) : Ordered[Symbol] = new ComparableSymbol(s)
  implicit def toComparable(s:String) : Ordered[String] = new ComparableString(s)

  def load(oid : ObjectID) : DataObject = ObjectManager.getStorageFor(oid).load(oid)
  def store(o : DataObject) : DataObject = { o.save; o }
}

abstract class DataObjectModification
case class MetadataChanged(s:Symbol) extends DataObjectModification
case class FieldChanged(fieldName:String) extends DataObjectModification

case class DataObjectModified(dom:DataObjectModification) extends ObservableEvent

trait DOStorage {
  def createDB(n:String) : Database = {
    try {
      val dbConfig = new DatabaseConfig
      dbConfig.setAllowCreate(true)
      val db = ObjectManager.dbEnv.openDatabase(null,n,dbConfig)
      true
    } catch  {
      case e:DatabaseException => e.printStackTrace
    }
  }

  def store[T](o : T) : T
  def load[T](oid : ObjectID) : T
}

class ObjectID(val clazz : Class) {
  val className = clazz.getName()
  val uid = new UID()
}


@serializable
trait DataObject extends Observable with Observer {
  import DataObject._
  

  val oid = new ObjectID(Predef.classOf[this.type])
  var tags = List[String]()
  var metadata = TreeMap[Symbol,AnyRef]()
  var instanceFields = TreeMap[String,Any]()
//   val _actor = actor {
//     loop {
//       react {
// 	case 'save => { val out = configuration.getObjectOutputStream(oid); out.writeObject(this); out.close() }
//       }
//     }
//   }

  meta(kOwner,"a user") // XXX
  meta(kCreated,new Date)
  meta(kModified,meta(kCreated).get) 
  meta(kAccessed,meta(kCreated).get)
  meta(kModifier,"a user") // XXX
  meta(kShared,false)
  meta(kKind,kind)
  save // save on initial creation!

  // ********************************************************************************
  // TO BE DEFINED, i.e. subclass responsibility
  def kind : AnyRef
  def defaultView : View
  protected def configuration : DOStorage
  // ********************************************************************************

  // todo - how to make the object multithreaded - I think actors are a good fit here! need to actorify
  // how to make actor's threadsafe?
  def save : Unit = { val out = configuration.getObjectOutputStream(oid); out.writeObject(this); out.close() }
  def objectID = oid
  def meta(k:Symbol,v:AnyRef) = {
    metadata = metadata.update(k,v)
    save
    fire(DataObjectModified(MetadataChanged(k)))
  }
  def meta(k:Symbol) = metadata.get(k)  
  def printMeta = kBaseMetadata.foreach { k => Console.println(k.toString + ": " + meta(k).get("")) }

  override def toString = kind.toString + oid.hashCode

//   private override def readObject(in:ObjectInputStream):Unit = {
//     in.defaultReadObject()
//   }
//   private override def writeObject(out:ObjectOutputStream):Unit = {
//     out.defaultWriteObject()
//   }

  def addField(n:String,get:Any) = {
    instanceFields = instanceFields.update(n,get)
    listenTo(get.asInstanceOf[Observable]) event {
      case FieldChange(n,v) => {
	meta(kModified, new Date)
	save
	fire(DataObjectModified(FieldChanged(n)))
      }
    }
  }
  case class FieldChange[T](name:String,value:T) extends ObservableEvent
  // todo: think about this - maybe not the best idea? 
  // too nice for a programmer, but PITA for performance/space?
  abstract class Field[T](name:String, init:T) extends Observable {
    addField(name,this)
    
    var data : T = init
    var getter : T => T = identity[T]
    var setter : T => T = identity[T]
    def get(newGetter:T=>T) = { getter = newGetter; this }
    def set(newSetter:T=>T) = { setter = newSetter; this }
    def apply() : T = getter(data)
    def update(v:T) = { 
      Console.println("Field update: " + v)
      fire(FieldChange(name,v)); data = setter(v) 
    }
  }
  //********************************************************************************
  // Field kinds! these need to be pulled out, somehow? how to extend? things should be
  // available to all dataobjects
  @serializable
  case class Text(name_ : String, init_ :String) extends Field(name_,init_) {
    val name = name_
    def text : String = apply()
    def text(v:String) : Text = { update(v); this }
  }
  implicit def view(a:Any) : Text = a.asInstanceOf[Text]

  @serializable
  @SerialVersionUID(1000L)
  case class Ref[T <: DataObject](name : String, init : T) extends Field(name,init) {
    var loaded = true
    var oid : ObjectID = init.objectID
    var maybeData : Option[T] = Some(init)
    get( (d) => maybeData match {
      case Some(o) => o
      case None => load(oid)
    } )
    set( d => { oid = d.objectID; maybeData = Some(d); d } )

    def this(name_ :String, oid_ :ObjectID) = { this(name_ , null.asInstanceOf[T]); oid = oid_; maybeData = None; loaded = false; this }
    
    def ref(v:T) : Ref[T] = { update(v); this }
    def ref : T = apply()
    private def load(o:ObjectID) : T = { DataObject.load(o).asInstanceOf[T] }
    private def writeReplace() : Object = new RefProxy[T](name,oid)
  }
  @serializable
  @SerialVersionUID(1000L)
  class RefProxy[T](name:String, oid: ObjectID) {
    private def readReplace() : Object = {
      new Ref[T](name,oid)
    }
  }
  implicit def view[T](a:Any) : Ref[T] = a.asInstanceOf[Ref[T]]

  abstract class DOCollection[E] extends Observable with Seq[E] {
    var buf : Seq[E]

    def add(e:E) : boolean
    def addAll(c:DOCollection[E]) : boolean = { for (val e <- c) if (!add(e)) return false; true }
    def clear : Unit 
    def contains(o:Any) : boolean = buf.contains(o)
    def containsAll(c:DOCollection[E]) : boolean = { for (val e <- c) if (!contains(e)) return false; true }
    def isEmpty : boolean = buf.isEmpty
    def remove(o:Any):boolean 
    def removeAll(c:DOCollection[E]) : boolean = { for (val e <- c) if(!remove(e)) return false; true }
    def size : int = buf.length
    def toArray : Array[E] = buf.toArray
    def asIterable : Iterable[E] = buf
  }

  case class DOList[E] extends DOCollection[E] {
    import scala.collection.jcl.LinkedList
    var buf = new LinkedList[E]
    
    override def add(e:E) = buf.add(e)
    def push(e:E) = buf.underlying.addLast(e)
    def offer(e:E) = buf.underlying.addFirst(e)
    def pop : E = buf.underlying.removeFirst.asInstanceOf[E]
    def poll : E = buf.underlying.removeLast.asInstanceOf[E]
    override def clear = buf.underlying.clear()
    override def remove(o:Any) : boolean = buf.remove(o.asInstanceOf[E])
    def peekFirst : E = buf.underlying.getFirst.asInstanceOf[E]
    def peekLast : E = buf.underlying.getLast.asInstanceOf[E]
    def removeFirst : E = buf.underlying.removeFirst.asInstanceOf[E]
    def removeLast : E = buf.underlying.removeLast.asInstanceOf[E]
  }
}

@serializable
@SerialVersionUID(1000)
class Tag extends DataObject {
  val value = Text("value","")

  def kind = "Tag"
  def defaultView = null
}
