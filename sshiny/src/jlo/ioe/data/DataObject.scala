package jlo.ioe.data

import java.util.Date
import java.rmi.server.ObjID
import scala.collection.immutable.TreeMap
import scala.Predef.identity
import scala.Predef.boolean2Boolean
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
  Console.println("ooga booga")

  class ComparableSymbol(sym:Symbol) extends Ordered[Symbol] {
    def compare(that:Symbol) = sym.name.compareTo(that.name)
  }
  class ComparableString(s:String) extends Ordered[String] {
    def compare(that:String) = s.compareTo(that)
  }
  
  implicit def toComparable(s:Symbol) : Ordered[Symbol] = new ComparableSymbol(s)
  implicit def toComparable(s:String) : Ordered[String] = new ComparableString(s)
}

abstract class DataObjectModification
case class MetadataChanged(s:Symbol) extends DataObjectModification
case class FieldChanged(fieldName:String) extends DataObjectModification

case class DataObjectModified(dom:DataObjectModification) extends ObservableEvent

@serializable
trait DataObject extends Observable with Observer {
  import DataObject._
  
  val oid = new ObjID()
  var tags = List[String]()
  var metadata = TreeMap[Symbol,AnyRef]()
  var instanceFields = TreeMap[String,Any]()

  meta(kOwner,"a user") // XXX
  meta(kCreated,new Date)
  meta(kModified,meta(kCreated).get) 
  meta(kAccessed,meta(kCreated).get)
  meta(kModifier,"a user") // XXX
  meta(kShared,false)
  meta(kKind,kind)

  // ********************************************************************************
  // TO BE DEFINED, i.e. subclass responsibility
  def kind : AnyRef
  def defaultView : View 
  // ********************************************************************************

  def objectID = oid
  def meta(k:Symbol,v:AnyRef) = {
    metadata = metadata.update(k,v)
    fire(DataObjectModified(MetadataChanged(k)))
  }
  def meta(k:Symbol) = metadata.get(k)  
  def printMeta = {
    kBaseMetadata.foreach { k => Console.println(k.toString + ": " + meta(k).get("")) }
  }

  override def toString = kind.toString + oid.hashCode

  def addField(n:String,get:Any) = {
    instanceFields = instanceFields.update(n,get)
    listenTo(get.asInstanceOf[Observable]) event {
      case FieldChange(n,v) => {
	meta(kModified, new Date)
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

//   @serializable
//   case class Ref(name_ : String, init_ : DataObject) extends Field(name_,init_) {
//     override var data : Option[DataObject] = None
//     var oid : ObjID = _
//     get( (d) => d match {
//       case Some(o) => o
//       case None => load(oid)
//     } )
//     set( d => { oid = d.objectID; data = Some(d); d } )
    
//     def ref(v:DataObject) : Ref = { update(v); this }
//     def ref : DataObject = apply()

//     private def writeObject(oos : ObjectOutputStream) : Unit = {}
//     private def readObject(ois : ObjectInputStream) : 
//   }
//   implicit def view(a:Any) : Ref = a.asInstanceOf[Ref]
}

@serializable
@SerialVersionUID(1000)
class Tag extends DataObject {
  val value = Text("value","")

  def kind = "Tag"
  def defaultView = null
}
