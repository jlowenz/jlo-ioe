package jlo.ioe.data

import java.util.Date
import java.rmi.server.UID
import scala.collection.immutable.TreeMap
import scala.Predef.{boolean,int,byte}
import scala.Predef.identity
import scala.Predef.boolean2Boolean
import java.io.{ObjectOutputStream,ObjectInputStream,
		ByteArrayOutputStream,ByteArrayInputStream,
		DataOutputStream,DataInputStream,
		Serializable}
import jlo.ioe.ui.Observable // XXX refactor
import jlo.ioe.ui.Observer // XXX refactor
import jlo.ioe.ui.ObservableEvent
import jlo.ioe.View
import jlo.ioe.command._

abstract class DataObjectModification
case class MetadataChanged(s:Symbol) extends DataObjectModification
case class FieldChanged(fieldName:String) extends DataObjectModification

case class DataObjectModified(dom:DataObjectModification) extends ObservableEvent

trait DOStorage[+T <: DataObject] {
  import com.sleepycat.je.Database;
  import com.sleepycat.je.DatabaseConfig;
  import com.sleepycat.je.DatabaseException;
  import com.sleepycat.je.DatabaseEntry;
  import com.sleepycat.je.LockMode;
  import com.sleepycat.je.OperationStatus;
  import com.sleepycat.je.Cursor;

  def db : Database

  def createDB(n:String) : Database = {
    try {
      val dbConfig = new DatabaseConfig
      dbConfig.setAllowCreate(true)
      return ObjectManager.dbEnv.openDatabase(null,n,dbConfig)
    } catch  {
      case e:DatabaseException => e.printStackTrace
    }
    null.asInstanceOf[Database]
  }
  def newTx = ObjectManager.dbEnv.beginTransaction(null,null)
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

  def store(o : DataObject) : T = {
    try {
      val key = getKey(o.objectID)
      val value = getValue(o)
      val tx = newTx
      db.put(tx, key, value)
      tx.commit
    } catch {
      case e:Exception => e.printStackTrace
    }
    o.asInstanceOf[T]
  }
  def load(oid : ObjectID) : Option[T] = {
    try {
      val key = getKey(oid)
      val obj = new DatabaseEntry()
      if (db.get(null, key, obj, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
	return Some(getObject(obj.getData()))
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

class ObjectID(val clazz : Class) {
  val className = clazz.getName()
  val uid = new UID()
  val bytes = toBytes

  private def toBytes : Array[byte] = {
    val bytes = new ByteArrayOutputStream
    val out = new DataOutputStream(bytes)
    uid.write(out)
    out.writeUTF(className)
    bytes.toByteArray
  }
}

case class FieldChange[T](name:String,value:T) extends ObservableEvent

@serializable
trait DataObject extends Observable with Observer with Serializable {
  import DataObjects._
  import Fields._
  
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
  //save // save on initial creation!

  // ********************************************************************************
  // TO BE DEFINED, i.e. subclass responsibility
  def kind : AnyRef
  def defaultView : View
  protected def storage : DOStorage[DataObject]
  // ********************************************************************************

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
	fire(DataObjectModified(FieldChanged(n)))
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

  def load[T <: DataObject](oid : ObjectID) : Option[T] = ObjectManager.getStorageFor(oid).load(oid).asInstanceOf[Option[T]]
  def store[T <: DataObject](o : T) : T = { o.save; o }
}

object TagStorage extends DOStorage[Tag] {
  val db = createDB("Tag")
}

@serializable
@SerialVersionUID(1000)
class Tag extends DataObject {
  val value = Text(this,"value","")

  def kind = "Tag"
  def defaultView = null
  def storage = TagStorage
}
