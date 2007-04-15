package jlo.ioe.util

// todo: refactor to util
import jlo.ioe.data.ObjectID
import java.io.{ObjectInput,ObjectOutput}

@SerialVersionUID(1000)
abstract class LazyRef[T <: Identifiable](var obj : Option[T]) extends java.io.Externalizable {
  var oid : ObjectID = if (obj.isDefined) obj.get.objectID else null

  def this() = this(None)
  def this(id : ObjectID) = { this(None); oid = id }

  def objectID = oid
  def isLoaded : boolean = isLoaded(oid)
  def apply() : T = synchronized { obj = obj.orElse({loadObject(oid)}); obj.get }
  protected def isLoaded(id:ObjectID) : boolean
  protected def loadObject(id:ObjectID) : Option[T]
  
  def writeExternal(out:ObjectOutput) : Unit = {
    out.writeObject(oid)
  }
  def readExternal(in:ObjectInput) : Unit = {
    oid = in.readObject.asInstanceOf[ObjectID]
  }
}
