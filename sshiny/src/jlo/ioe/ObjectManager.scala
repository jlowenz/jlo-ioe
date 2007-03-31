package jlo.ioe;


object ObjectManager {
  var objects = List[data.DataObject]()

  def objectCreated(a : data.DataObject) {
    // put the object in the lobby? store to disk? create file?
    objects = a :: objects
  }
}
