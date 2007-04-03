package jlo.ioe;

// import com.db4o._

object ObjectManager {
  // configuration here?
//   var db = Db4o.openFile("ioe.db")
  var activeObjects = List[data.DataObject]()

  def objectCreated(a : data.DataObject) {
    // put the object in the lobby? store to disk? create file?
    a.printMeta
    activeObjects = a :: activeObjects
  }

  
}
