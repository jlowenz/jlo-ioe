package jlo.ioe.data

import java.util.Date
import java.rmi.server.UID
import scala.collection.immutable.{TreeMap,Map,HashMap,Set,HashSet}
import scala.Predef.{boolean,int,byte}
import scala.Predef.identity
import scala.Predef.boolean2Boolean
import java.io.{ObjectOutputStream,ObjectInputStream,
		ByteArrayOutputStream,ByteArrayInputStream,
		DataOutputStream,DataInputStream,
		ObjectInput,ObjectOutput,
		Serializable}
import jlo.ioe.ui.Observable // XXX refactor
import jlo.ioe.ui.Observer // XXX refactor
import jlo.ioe.ui.EventHandler
import jlo.ioe.ui.ObservableEvent
import jlo.ioe.View
import jlo.ioe.command._

abstract class DataObjectModification
case class MetadataChanged(s:Symbol) extends DataObjectModification
case class FieldChanged(fieldName:String) extends DataObjectModification

case class DataObjectModified(dom:DataObjectModification) extends ObservableEvent

object Storage {
  val objects = new scala.collection.mutable.HashMap[ObjectID,DataObject]()
}

trait DOStorage[+T <: DataObject] {
  import com.sleepycat.je.Database;
  import com.sleepycat.je.DatabaseConfig;
  import com.sleepycat.je.DatabaseException;
  import com.sleepycat.je.DeadlockException;
  import com.sleepycat.je.DatabaseEntry;
  import com.sleepycat.je.LockMode;
  import com.sleepycat.je.OperationStatus;
  import com.sleepycat.je.Cursor;
  import Storage._

  def db : Database

  def createDB(n:String, c:Class) : Database = {
    try {
      ObjectManager.setStorageFor(c,this)
      val dbConfig = new DatabaseConfig
      dbConfig.setAllowCreate(true)
      dbConfig.setTransactional(true)
      return ObjectManager.dbEnv.openDatabase(null,n,dbConfig)
    } catch  {
      case e:DatabaseException => e.printStackTrace
    }
    null.asInstanceOf[Database]
  }
  def newTx = ObjectManager.dbEnv.beginTransaction(ObjectManager.dbEnv.getThreadTransaction,null)
  def getKey(o:ObjectID) : DatabaseEntry = new DatabaseEntry(o.bytes)
  def getValue(o:Serializable) : DatabaseEntry = {
    val bytes = new ByteArrayOutputStream()
    val out = new ObjectOutputStream(bytes)
    out.writeObject(o)
    new DatabaseEntry(bytes.toByteArray)
  }
  def getObject(b:Array[byte]) : T = {
    val in = new ObjectInputStream(new ByteArrayInputStream(b))
    in.readObject().asInstanceOf[T]
  }

  def isLoaded(oid : ObjectID) = {
    objects.get(oid).isDefined
  }

  def store(o : DataObject) : T = {
    Console.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=- storing: " + o)
    val tx = newTx
    try {
      val key = getKey(o.objectID)
      val value = getValue(o)
      db.put(tx, key, value)
      tx.commit
      objects.update(o.objectID, o)
    } catch {
      case d:DeadlockException => { tx.abort; d.printStackTrace }
      case e:Exception => { tx.abort; e.printStackTrace }
    } 
    o.asInstanceOf[T]
  }
  def load(oid : ObjectID) : Option[T] = {
    try {
      val key = getKey(oid)
      val obj = new DatabaseEntry()
      if (db.get(null, key, obj, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	val theObject = getObject(obj.getData())
	objects.update(theObject.objectID, theObject)
	return Some(theObject)
      } 
    } catch {
      case e:Exception => e.printStackTrace
    }
    return None
  }

  def loadAll : List[T] = {
    var all = List[T]()
    var cursor : Cursor = null
    try {
      // Open the cursor. 
      cursor = db.openCursor(null, null)
      val foundKey = new DatabaseEntry();
      val foundData = new DatabaseEntry();
      
      while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	val obj : T = getObject(foundData.getData())
	all = all ::: List(obj)
      }
    } catch {
      case de:DatabaseException => System.err.println("Error accessing database." + de);
    } 
    all
  }
}

class ObjectID(val clazz : Class) extends java.io.Serializable {
  val className = clazz.getName()
  val uid = new UID()
  val bytes = toBytes
  Console.println("ObjectID(" + className + ") + " + uid)

  private def toBytes : Array[byte] = {
    val bytes = new ByteArrayOutputStream
    val out = new DataOutputStream(bytes)
    uid.write(out)
    out.writeUTF(className)
    bytes.toByteArray
  }
}

case class FieldChange[T](name:String,value:T) extends ObservableEvent

// todo: refactor to util?
trait Storable extends java.io.Serializable {}

@SerialVersionUID(1000)
abstract class DataObject extends Observable with Observer with java.io.Externalizable with Storable with jlo.ioe.util.Identifiable {
  import DataObjects._
  import Fields._
  
  // BEGIN: state
  private var oid = new ObjectID(getClass())
  var tags = List[String]()
  var metadata = TreeMap[Symbol,AnyRef]()
  var instanceFields = TreeMap[String,Any]()
  var obsHandlers : Map[Observable,List[EventHandler]] = new HashMap[Observable,List[EventHandler]]()
  var obsListeners : Set[Observer] = new HashSet[Observer]()
  // END: state

  def handlers = obsHandlers
  def handlers_=(h:Map[Observable,List[EventHandler]]) = obsHandlers = h
  def listeners = obsListeners
  def listeners_=(o:Set[Observer]) = obsListeners = o

  meta(kOwner,"a user") // XXX
  meta(kCreated,new Date)
  meta(kModified,meta(kCreated).get) 
  meta(kAccessed,meta(kCreated).get)
  meta(kModifier,"a user") // XXX
  meta(kShared,false)
  meta(kKind,kind)
  //save // save on initial creation!

  // ********************************************************************************
  // TO BE DEFINED, i.e. subclass responsibility
  def kind : AnyRef
  def defaultView : View
  protected def storage : DOStorage[DataObject]
  // ********************************************************************************

  def writeExternal(oout:ObjectOutput) :Unit = {
    Console.println("DataObject writeExternal")

    // remember mixins
    writeHandlers(oout)
    writeObservers(oout)

    // write self
    oout.writeObject(oid)
    oout.writeObject(tags)
    oout.writeInt(metadata.size)
    metadata.foreach { e => { oout.writeUTF(e._1.toString); oout.writeObject(e._2) } }
    oout.writeInt(instanceFields.size)
    instanceFields.foreach { e => {
      oout.writeUTF(e._1); Console.println("writing field: " + e._1)
      oout.writeObject(e._2)
    }}
    
  }

  def readExternal(in:ObjectInput):Unit = {
    Console.println("DataObject readExternal")
    readHandlers(in)
    readObservers(in)

    // read self
    oid = in.readObject.asInstanceOf[ObjectID]
    tags = in.readObject.asInstanceOf[List[String]]
    
    val mdCount = in.readInt
    for (val i <- Iterator.range(0,mdCount)) {
      val k = new Symbol(in.readUTF)
      val v = in.readObject
      meta(k,v)
    }
    val fieldCount = in.readInt
    for (val i <- Iterator.range(0,fieldCount)) {
      val n = in.readUTF
      val f = in.readObject
      addField(n,f)
    }
  }

  // todo - how to make the object multithreaded - I think actors are a good fit here! need to actorify
  // how to make actor's threadsafe?
  def objectID : ObjectID = oid
  def save : DataObject = storage.store(this) 
  def meta(k:Symbol,v:AnyRef) = {
    metadata = metadata.update(k,v)
    fire(DataObjectModified(MetadataChanged(k)))
  }
  def meta(k:Symbol) = metadata.get(k)  
  def printMeta = kBaseMetadata.foreach { k => Console.println(k.toString + ": " + meta(k).get("")) }

  override def toString = kind.toString + oid.hashCode

  def addField(n:String,f:Any) = {
    Console.println(toString + ".addField(" + n + "," + f + ")")
    instanceFields = instanceFields.update(n,f)
    listenTo(f.asInstanceOf[Observable]) event {
      case FieldChange(n,v) => {
	meta(kModified, new Date)
	fire(DataObjectModified(FieldChanged(n)))
	save
      }
    }
  }
}

object DataObjects {
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

  def load[T <: DataObject](oid : ObjectID) : Option[T] = ObjectManager.getStorageFor(oid).get(null).load(oid).asInstanceOf[Option[T]]
  def store[T <: DataObject](o : T) : T = { o.save; o }
}

object TagStorage extends DOStorage[Tag] {
  val db = createDB("Tag", Predef.classOf[Tag])
}

@serializable
@SerialVersionUID(1000)
class Tag extends DataObject {
  val value = new Text(this,"value","")

  def kind = "Tag"
  def defaultView = null
  def storage = TagStorage
}
