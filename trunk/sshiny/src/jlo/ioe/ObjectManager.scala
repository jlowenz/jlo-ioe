package jlo.ioe;

// import com.db4o._
import java.io.{ObjectInputStream,ObjectOutputStream,File,FileInputStream,ObjectInput,ObjectOutput}
import scala.collection.mutable.HashMap

class SystemStateWrapper {
  var state : SystemState = null

  def load = {
    val sss = SystemStateStorage.loadAll
    state = if (sss.length > 0) sss.head
		else new SystemState
    Console.println("AFTER LOAD SYSTEM: " + state)
  }

  def update(c:Class,d:data.DOStorage[data.DataObject]) = {
    if (state != null) state.update(c,d)
  }
  def get(oid:data.ObjectID) : Option[data.DOStorage[data.DataObject]] = {
    if (state != null) state.get(oid)
    else None
  }
  
}

@serializable
@SerialVersionUID(1000L)
class SystemState extends data.DataObject {
  private var storageMap = new HashMap[Class,data.DOStorage[data.DataObject]]

  override def storage = SystemStateStorage
  override def defaultView = null.asInstanceOf[View]
  override def kind = "SystemState"

  def update(c:Class,d:data.DOStorage[data.DataObject]) = {
    Console.println("updateSS: " + c + ", " + d)
    storageMap.update(c,d)
    save
  }
  def get(oid:data.ObjectID) = {
    storageMap.get(oid.clazz)
  }

  override def writeExternal(out:ObjectOutput) : Unit = {
    super.writeExternal(out)
    out.writeInt(storageMap.size)
    storageMap.foreach { e => out.writeObject(e._1); out.writeObject(e._2.getClass) }
  }
  override def readExternal(in:ObjectInput) : Unit = {
    super.readExternal(in)
    val count = in.readInt
    for (val i <- Iterator.range(0,count)) {
      val k = in.readObject.asInstanceOf[Class]
      val v = in.readObject.asInstanceOf[Class]
      val f = v.getField("MODULE$") // implementation detail!
      storageMap.update(k,f.get(null).asInstanceOf[data.DOStorage[data.DataObject]])
    }
  }

  override def toString : String = {
    storageMap.foreach { e => Console.println(e._1 + ": " + e._2) }
    super.toString
  }
}

object SystemStateStorage extends data.DOStorage[SystemState] {
  import com.sleepycat.je.Database;
  import com.sleepycat.je.DatabaseConfig;
  import com.sleepycat.je.DatabaseException;

  override def createDB(n:String, c:Class) : Database = {
    try {
      val dbConfig = new DatabaseConfig
      dbConfig.setAllowCreate(true)
      dbConfig.setTransactional(true)
      return ObjectManager.dbEnv.openDatabase(null,n,dbConfig)
    } catch  {
      case e:DatabaseException => e.printStackTrace
    }
    null.asInstanceOf[Database]
  }

  // create the system state database
  val _db = createDB("SystemState", classOf[SystemState])
  override def db = _db
}


object ObjectManager {
  import com.sleepycat.je.DatabaseException
  import com.sleepycat.je.Environment
  import com.sleepycat.je.EnvironmentConfig
  import java.io.File

  // Open the environment. Allow it to be created if it does not already exist.
  var dbEnv : Environment = openEnvironment
  
  // load the system state!
  private var screens : List[Screen] = Nil
  private var storage = new SystemStateWrapper
  storage.load
  loadSystem

  def addScreen(s:Screen) = screens = s :: screens
  def numScreens = screens.length
  def getFirstScreen = screens.head

  def setStorageFor(c:Class,d:data.DOStorage[data.DataObject]) = {
    storage.update(c,d)
  }

  def getStorageFor(oid:data.ObjectID) : Option[data.DOStorage[data.DataObject]] = { 
    storage.get(oid)
  }

  def objectCreated(a : data.DataObject) {
    // put the object in the lobby? store to disk? create file?
    a.printMeta
  }

  private def loadSystem = {
    //stostorage.getClass, SystemStateStorage)
    // try to read the system state
    val s = ScreenStateStorage.loadAll
    Console.println("count: " + s.length)
    s.foreach { e => addScreen(e.screen) }
  }
  
  private def openEnvironment : Environment = {
    var dbEnv : Environment = null
    try {
      val envDir = new File("./db")
      if (!envDir.exists()) envDir.mkdir
      val envConfig = new EnvironmentConfig()
      envConfig.setTransactional(true)
      envConfig.setAllowCreate(true)
      envConfig.setTxnSerializableIsolation(true)
      dbEnv = new Environment(new File("./db"), envConfig)
    } catch {
      case dbe : DatabaseException => { dbe.printStackTrace }// Exception handling goes here 
    } 
    dbEnv
  }
}
