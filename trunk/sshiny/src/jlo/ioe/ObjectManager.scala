package jlo.ioe;

// import com.db4o._
import java.io.{ObjectInputStream,ObjectOutputStream,File,FileInputStream}
import scala.collection.mutable.HashMap

@serializable
@SerialVersionUID(1000L)
class SystemState extends data.DataObject {
  var screens = List[ScreenState]()
  var activeObjects = List[data.Ref[data.DataObject]]()

  override def storage = SystemStateStorage
  override def defaultView = null.asInstanceOf[View]
  override def kind = "SystemState"
  def addScreen(s:ScreenState) = screens ::: List(s)
  def numScreens = screens.length
  def activateObject(o:data.DataObject) = new data.Ref[data.DataObject](this,"active", o) :: activeObjects
  def deactivateObject(o:data.DataObject) = activeObjects = activeObjects.remove(e => e.ref==o)
}

object SystemStateStorage extends data.DOStorage[SystemState] {
  // create the system state database
  override def db = createDB("SystemState")    
}


object ObjectManager {
  import com.sleepycat.je.DatabaseException
  import com.sleepycat.je.Environment
  import com.sleepycat.je.EnvironmentConfig
  import java.io.File

  // Open the environment. Allow it to be created if it does not already exist.
  var dbEnv : Environment = openEnvironment

  // load the system state!
  private var state : SystemState = loadSystem
  private var storageMap = new HashMap[Class,data.DOStorage[data.DataObject]]

  def addScreen(s:Screen) = state.addScreen(s.sstate)
  def numScreens = state.numScreens
  def getFirstScreen = state.screens.head

  def getStorageFor(oid:data.ObjectID) : data.DOStorage[data.DataObject] = { 
    storageMap.get(oid.clazz).get(null)
  }

  def objectCreated(a : data.DataObject) {
    // put the object in the lobby? store to disk? create file?
    a.printMeta
    state.activateObject(a)
  }

  private def loadSystem : SystemState = {
    // try to read the system state
    val allState = SystemStateStorage.loadAll
    if (allState.isEmpty) new SystemState
    else allState.head
  }
  
  private def openEnvironment : Environment = {
    var dbEnv : Environment = null
    try {
      val envConfig = new EnvironmentConfig()
      envConfig.setTransactional(true)
      envConfig.setAllowCreate(true)
      dbEnv = new Environment(new File("./db"), envConfig)
    } catch {
      case dbe : DatabaseException => { dbe.printStackTrace }// Exception handling goes here 
    } 
    dbEnv
  }
}
