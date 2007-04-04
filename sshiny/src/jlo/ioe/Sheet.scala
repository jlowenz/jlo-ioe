package jlo.ioe;

import jlo.ioe.ui._
import javax.swing.border.{CompoundBorder,LineBorder,EmptyBorder}
import java.awt.Color

case class SheetTitleChanged(newTitle:String) extends ObservableEvent

@serializable
class SheetState(root : Split[data.Ref[data.DataObject]]) { 
  def splits = root
}

class Sheet(screen:Screen, obj:data.DataObject) extends Panel with Observer {
  type SDO = data.Ref[data.DataObject]
  var titleString : String = obj.toString // todo: how to track this?
  var sstate = new SheetState(new Split(Some(new data.Ref[data.DataObject](null.asInstanceOf[data.DataObject], "obj", obj)), 
					Aspect.Horizontal, 
					1.0, 
					{(a:data.Ref[data.DataObject])=>a.ref.defaultView}))
  var splitPane = new Splitter
  setBorder(new EmptyBorder(2,2,2,2))
  add(splitPane)
  splitPane.resplit(sstate.splits)
  listenTo(obj) event {
    case data.DataObjectModified(data.MetadataChanged(data.DataObjects.kTitle)) => title = obj.meta(data.DataObjects.kTitle).get("").asInstanceOf[String]
    case _ => {}
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
      case Aspect.Horizontal() => max.verticalDivider(max().get,new data.Ref[data.DataObject](null,"obj",a))
      case Aspect.Vertical() => max.horizontalDivider(max().get,new data.Ref[data.DataObject](null,"obj",a))
    }
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
