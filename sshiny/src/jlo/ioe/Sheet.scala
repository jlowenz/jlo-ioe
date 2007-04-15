package jlo.ioe;

import jlo.ioe.ui._
import jlo.ioe.ui.Splitter._
import javax.swing.border.{CompoundBorder,LineBorder,EmptyBorder}
import java.awt.Color
import java.awt.event._
import java.io.{ObjectOutput,ObjectInput}

case class SheetTitleChanged(newTitle:String) extends ObservableEvent

object SheetStateStorage extends data.DOStorage[SheetState] {
  val _db = createDB("SheetState", classOf[SheetState])
  def db = _db
}

@SerialVersionUID(1000)
class SheetState(var root : Split[data.Ref[data.DataObject]]) extends data.DataObject { 
  def splits = root

  def this() = this(null)

  def storage = SheetStateStorage
  def kind = "SheetState"
  def defaultView = null

  override def writeExternal(out:ObjectOutput) : Unit = {
    super.writeExternal(out)
    out.writeObject(root)
  }
  override def readExternal(in:ObjectInput) : Unit = {
    super.readExternal(in)
    root = in.readObject.asInstanceOf[Split[data.Ref[data.DataObject]]]
  }
}

class ViewAccessor extends ComponentAccessor[data.Ref[data.DataObject]]({(a)=>null}) {
  override def apply(p : data.Ref[data.DataObject]) : Component = p().defaultView
}

class Sheet(screen:Screen, sstate : SheetState) extends Panel with Observer {
  type SDO = data.Ref[data.DataObject]
  var obj = sstate.splits().get.apply()
  var titleString : String = obj.toString // todo: how to track this?
  var splitPane = new Splitter
  setBorder(new EmptyBorder(2,2,2,2))
  add(splitPane)
  addComponentListener(new ComponentAdapter {
    var firstTime = true
    override def componentShown(e:ComponentEvent) : Unit = {
      if (firstTime) {
	splitPane.resplit(sstate.splits)
	listenTo(obj) event {
	  case data.DataObjectModified(data.MetadataChanged(data.DataObjects.kTitle)) => title = obj.meta(data.DataObjects.kTitle).get("").asInstanceOf[String]
	  case _ => {} 
	}
	firstTime = false
      }
    }
  })

  def this(_screen : Screen, obj : data.DataObject) = { this(_screen, new SheetState(new Split(Some(new data.Ref[data.DataObject](Some(obj))), 
 											       Aspect.Horizontal, 
 											       1.0, 
 											       new ViewAccessor))); this.obj = obj }

  def state = sstate
  override def equals(o:Any) : boolean = {
    if (o.isInstanceOf[Sheet]) {
      val s = o.asInstanceOf[Sheet]
      s.state.objectID == sstate.objectID
    } else false
  }

  def title = titleString
  def title_=(s:String) = {
    titleString = s
    fire(SheetTitleChanged(titleString))
  }
  def display = {
    Console.println("Shown!")
    screen.display(this)
    this
  }

  def split(a:data.DataObject) = {    
    val max = findSplit(sstate.splits,sstate.splits)
    Console.println(max)
    max.aspect match {
      case Aspect.Horizontal() => max.verticalDivider(max().get,new data.Ref[data.DataObject](Some(a)))
      case Aspect.Vertical() => max.horizontalDivider(max().get,new data.Ref[data.DataObject](Some(a)))
    }
    sstate.save
    splitPane.resplit(sstate.splits)
  }
  
  private def findSplit(max:Split[SDO], s:Split[SDO]) : Split[SDO] = {
    Console.println("MAX: " + max)
    Console.println("s  : " + s)
    // add the object at the no split with the largest area
    s.kind match {
      case SplitType.NoSplit() => if (s.area >= max.area) s else max
      case _ => Util.argmax({(a:Split[data.Ref[data.DataObject]],b:Split[data.Ref[data.DataObject]]) => if (a.area > b.area) a else b}, List(findSplit(max,s.first.get),findSplit(max,s.second.get)))
    }
  }
}
