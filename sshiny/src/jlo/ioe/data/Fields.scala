package jlo.ioe.data

import jlo.ioe.ui.{Observable,Observer}
import scala.collection.immutable.{Set,HashSet}
import java.io.{ObjectInput,ObjectOutput}

object Fields {
  implicit def view(a:Any) : Text = a.asInstanceOf[Text]
  implicit def view[R <: DataObject](a:Any) : Ref[R] = a.asInstanceOf[Ref[R]]
}

// todo: think about this - maybe not the best idea? 
// too nice for a programmer, but PITA for performance/space?
@SerialVersionUID(1000)
abstract class Field[T](var owner:DataObject, var name:String, init:T) extends Observable with java.io.Externalizable with Storable {
  protected var obsListeners : Set[Observer] = new HashSet[Observer]()

  if (owner != null) owner.addField(name,this)
    
  def listeners = { Console.println("getListeners: " + obsListeners); obsListeners }
  def listeners_=(o : Set[Observer]) = {
    if (o == null) { 
      Console.println("listeners is null!")
      new Throwable().printStackTrace
    }
    obsListeners = o
  }

  def writeExternal(out:ObjectOutput) : Unit = {
    writeObservers(out)
    out.writeObject(apply())
  }
  def readExternal(in:ObjectInput) : Unit = {
    readObservers(in)
    update(in.readObject().asInstanceOf[T])
  }

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
@SerialVersionUID(1000)
class Text(owner_ : DataObject, name_ : String, init_ :String) extends Field(owner_, name_,init_) {

  def this() = this(null,"","")

  def text : String = synchronized { apply() }
  def text(v:String) : Text = { synchronized { update(v) }; this }
}

package field {
  @SerialVersionUID(1000)
  class Image(owner_ : DataObject, name_ : String, init_ : java.awt.Image) extends Field(owner_,name_,init_) {
    def this() = this(null,"",null)

  }
}

abstract class DOCollection[E](owner : DataObject, name : String) extends Observable with Seq[E] with java.io.Serializable {
  owner.addField(name,this)

  var obsListeners : Set[Observer] = new HashSet[Observer]()
  def listeners = obsListeners
  def listeners_=(o:Set[Observer]) = obsListeners = o

  def buf : Seq[E]
  def add(e:E) : boolean
  def addAll(c:DOCollection[E]) : boolean = { for (val e <- c) if (!add(e)) return false; true }
  override def apply(i:int) = buf.apply(i)
  def clear : Unit 
  override def contains(o:Any) : boolean = buf.contains(o)
  def containsAll(c:DOCollection[E]) : boolean = { for (val e <- c) if (!contains(e)) return false; true }
  override def elements : Iterator[E] = buf.elements
  override def isEmpty : boolean = buf.isEmpty
  override def length : int = buf.length
  def remove(o:Any):boolean 
  def removeAll(c:DOCollection[E]) : boolean = { for (val e <- c) if(!remove(e)) return false; true }
  def size : int = buf.length
  //     override def toArray : Array[E] = buf.toArray
  def asIterable : Iterable[E] = buf
}

case class DOList[E](owner: DataObject, name:String) extends DOCollection[E](owner,name) {
  import scala.collection.jcl.LinkedList
  def buf = new LinkedList[E]
  
  override def add(e:E) = buf.add(e)
  def push(e:E) = buf.underlying.addLast(e)
  def offer(e:E) = buf.underlying.addFirst(e)
  def pop : E = buf.underlying.removeFirst.asInstanceOf[E]
  def poll : E = buf.underlying.removeLast.asInstanceOf[E]
  override def clear = buf.underlying.clear()
  override def remove(o:Any) : boolean = buf.remove(o.asInstanceOf[E])
  def peekFirst : E = buf.underlying.getFirst.asInstanceOf[E]
  def peekLast : E = buf.underlying.getLast.asInstanceOf[E]
  def removeFirst : E = buf.underlying.removeFirst.asInstanceOf[E]
  def removeLast : E = buf.underlying.removeLast.asInstanceOf[E]
}
