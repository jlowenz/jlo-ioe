package jlo.ioe.data

import jlo.ioe.ui.{Observable,Observer}

object Fields {
  implicit def view(a:Any) : Text = a.asInstanceOf[Text]
  implicit def view[R <: DataObject](a:Any) : Ref[R] = a.asInstanceOf[Ref[R]]
}

// todo: think about this - maybe not the best idea? 
// too nice for a programmer, but PITA for performance/space?
abstract class Field[T](owner:DataObject, name:String, init:T) extends Observable {
  if (owner != null) owner.addField(name,this)
  
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
@serializable
case class Text(owner_ : DataObject, name_ : String, init_ :String) extends Field(owner_, name_,init_) {
  val name = name_
  def text : String = apply()
  def text(v:String) : Text = { update(v); this }
}

// todo: this needs to be fixed - should have model?? instead of visual element?
class ImageCanvas extends jlo.ioe.ui.Panel {
  import javax.swing.border.LineBorder
  import java.awt.Color

  setBackground(Color.white)
  setBorder(new LineBorder(Color.black,1))
}

object Graphics {
  def BLANK = new ImageCanvas
}

case class Graphic(owner_ : DataObject, name_ : String, init_ : ImageCanvas) extends Field(owner_,name_,init_) {
  
}

@serializable
@SerialVersionUID(1000L)
case class Ref[R <: DataObject](owner : DataObject, name : String, init : R) extends Field(owner, name,init) {
  var loaded = true
  var oid : ObjectID = init.objectID
  var maybeData : Option[R] = Some(init)
  get( (d) => maybeData match {
    case Some(o) => o
    case None => load(oid)
  } )
  set( d => { oid = d.objectID; maybeData = Some(d); d } )

  def this(owner : DataObject, name_ :String, oid_ :ObjectID) = { this(owner, name_ , null.asInstanceOf[R]); oid = oid_; maybeData = None; loaded = false; this }
  
  def ref(v:R) : Ref[R] = { update(v); this }
  def ref : R = apply()
  private def load(o:ObjectID) : R = { DataObjects.load(o).asInstanceOf[R] }
  private def writeReplace() : Object = new RefProxy[R](owner, name,oid)
}
@serializable
@SerialVersionUID(1000L)
class RefProxy[R <: DataObject](owner: DataObject, name:String, oid: ObjectID) {
  private def readReplace() : Object = {
    new Ref[R](owner, name,oid)
  }
}

abstract class DOCollection[E](owner : DataObject, name : String) extends Observable with Seq[E] {
  owner.addField(name,this)

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
