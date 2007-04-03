package jlo.ioe;

// import com.db4o._
import java.io.{ObjectInputStream,ObjectOutputStream,File,FileInputStream}
import scala.collection.mutable.HashMap

object SystemState extends data.DOStorage {
  import com.sleepycat.je.Database;
  import com.sleepycat.je.DatabaseConfig;
  import com.sleepycat.je.DatabaseException;
  import com.sleepycat.je.DatabaseEntry;
  import com.sleepycat.je.Environment;
  import com.sleepycat.je.EnvironmentConfig;

  // create the system state database
  val db = createDB("SystemState")
  
  def store[SystemState](o:SystemState) : SystemState = {
    
  }

  def load[SystemState](oid:ObjectID) : SystemState = {
    
  }
}

@serializable
@SerialVersionUID(1000L)
class SystemState extends data.DataObject {
  var screens = List[Screen]()
  var activeObjects = List[data.DataObject]()

  override def configuration = SystemState
  def addScreen(s:Screen) = s :: screens
  def numScreens = screens.length
  def activateObject(o:data.DataObject) = o :: activeObjects
  def deactivateObject(o:data.DataObject) = activeObjects = activeObjects.remove(e => e==o)
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
  private var objectDir : File = new File("./objects")
  private var storageMap = new HashMap[Class,data.DOStorage]

  def addScreen(s:Screen) = state.addScreen(s)
  def numScreens = state.numScreens
  def getFirstScreen = state.screens.head

  def getObjectRoot = objectDir
  def getStorageFor(oid:data.ObjectID) : data.DOStorage = { 
    storageMap.get(oid.clazz).get(null)
  }

  def objectCreated(a : data.DataObject) {
    // put the object in the lobby? store to disk? create file?
    a.printMeta
    state.activateObject(a)
  }

  private def loadSystem : SystemState = {
    // try to read the system state
    val stateFile = new File("./ioe_state")
    if (!objectDir.exists) objectDir.mkdir()
    if (stateFile.exists) {
      val in = new ObjectInputStream(new FileInputStream(stateFile))
      in.readObject.asInstanceOf[SystemState]
    } else {
      new SystemState
    }
  }
  
  private def openEnvironment : Environment = {
    var dbEnv : Environment = null
    try {
      val envConfig = new EnvironmentConfig();
      envConfig.setAllowCreate(true);
      dbEnv = new Environment(new File("./db"), envConfig);
    } catch {
      case dbe : DatabaseException => { dbe.printStackTrace }// Exception handling goes here 
    } 
    dbEnv
  }
}
