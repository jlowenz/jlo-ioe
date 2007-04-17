package jlo.ioe.data

import jlo.ioe.util.LazyRef

@SerialVersionUID(1000)
class Ref[T <: DataObject](_obj : Option[T]) extends LazyRef[T](_obj) {
  
  def this() = this(None)

  protected def isLoaded(id:ObjectID) : boolean = {
//     for (val s <- ObjectManager.getStorageFor(id)) {
//       return s.isLoaded(id)
//     }
//     return false
    return true
  }
  protected def loadObject(id:ObjectID) : Option[T] = {
//     for (val s <- ObjectManager.getStorageFor(id)) {
//       return s.load(id).asInstanceOf[Option[T]]
//     }
//     return None
    return obj
  }
} 
