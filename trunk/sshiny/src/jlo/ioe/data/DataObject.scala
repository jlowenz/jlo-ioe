package jlo.ioe.data

import java.util.Date
import java.rmi.server.ObjID
import scala.collection.immutable.TreeMap
import scala.Predef.identity
import scala.Predef.boolean2Boolean
import jlo.ioe.ui.Observable // XXX refactor
import jlo.ioe.ui.Observer // XXX refactor
import jlo.ioe.ui.ObservableEvent
import jlo.ioe.View
import jlo.ioe.command._

object DataObject {
  val kOwner = 'owner
  val kCreated = 'createdg
  val kModified = 'modified
  val kAccessed = 'accessed
  val kModifier = 'modifier
  val kShared = 'shared
  val kKind = 'kind
  val kTitle = 'title
  val kDescription = 'description
  Console.println("ooga booga")

  class ComparableSymbol(sym:Symbol) extends Ordered[Symbol] {
    def compare(that:Symbol) = sym.name.compareTo(that.name)
  }
  class ComparableString(s:String) extends Ordered[String] {
    def compare(that:String) = s.compareTo(that)
  }
  
  implicit def toComparable(s:Symbol) : Ordered[Symbol] = new ComparableSymbol(s)
  implicit def toComparable(s:String) : Ordered[String] = new ComparableString(s)
}

abstract class DataObjectModification
case class MetadataChanged(s:Symbol) extends DataObjectModification
case class FieldChanged(fieldName:String) extends DataObjectModification

case class DataObjectModified(dom:DataObjectModification) extends ObservableEvent

@serializable
trait DataObject extends Observable with Observer {
  import DataObject._
  
  val oid = new ObjID()
  var tags = List[String]()
  var metadata = TreeMap[Symbol,AnyRef]()
  var instanceFields = TreeMap[String,Any]()

  meta(kOwner,"a user") // XXX
  meta(kCreated,new Date)
  meta(kModified,meta(kCreated)) 
  meta(kAccessed,meta(kCreated))
  meta(kModifier,"a user") // XXX
  meta(kShared,false)
  meta(kKind,kind)

  // TO BE DEFINED, i.e. subclass responsibility
  def kind : AnyRef
  def defaultView : View 

  def objectID = oid
  def meta(k:Symbol,v:AnyRef) = {
    metadata = metadata.update(k,v)
    fire(DataObjectModified(MetadataChanged(k)))
  }
  def meta(k:Symbol) = metadata.get(k)  

  // todo: are the below two methods useful?
  def addField(n:String,get:Any) = {
    instanceFields = instanceFields.update(n,get)
    listenTo(get.asInstanceOf[Observable]) event {
      case FieldChange(n,v) => fire(DataObjectModified(FieldChanged(n)))
    }
  }
//   def field(n:String) : Any = instanceFields.get(n) match { 
//     case Some(f) => f() 
//     case None => null
//   }

  case class FieldChange[T](name:String,value:T) extends ObservableEvent
  @serializable
  abstract class Field[T](name:String, init:T) extends Observable {
    addField(name,this)
    
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
  case class Text(name_ :String, init_ :String) extends Field(name_,init_) {
    val name = name_
    def text : String = apply()
    def text(v:String) : Text = { update(v); this }
  }
  implicit def view(a:Any) : Text = a.asInstanceOf[Text]

  override def toString = kind.toString + oid.hashCode
}

@serializable
@SerialVersionUID(1000)
class Tag extends DataObject {
  val value = Text("value","")

  def kind = "Tag"
  def defaultView = null
}
