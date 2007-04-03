package jlo.ioe;

import jlo.ioe.ui._
import javax.swing.border.{CompoundBorder,LineBorder,EmptyBorder}
import java.awt.Color

case class SheetTitleChanged(newTitle:String) extends ObservableEvent

class Sheet(screen:Screen, obj:data.DataObject) extends Panel with Observer {
  var titleString : String = obj.toString // todo: how to track this?
  var splits = new Split(Some(obj), Aspect.Horizontal, 1.0, {(a:data.DataObject)=>a.defaultView})
  var splitPane = new Splitter
  setBorder(new EmptyBorder(2,2,2,2))
  add(splitPane)
  splitPane.resplit(splits)
  listenTo(obj) event {
    case data.DataObjectModified(data.MetadataChanged(data.DataObject.kTitle)) => title = obj.meta(data.DataObject.kTitle).get("").asInstanceOf[String]
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
    Console.println(splits)
    val max = findSplit(splits,splits)
    Console.println(max)
    max.aspect match {
      case Aspect.Horizontal() => max.verticalDivider(max().get,a)
      case Aspect.Vertical() => max.horizontalDivider(max().get,a)
    }
    splitPane.resplit(splits)
  }

  private def findSplit(max:Split[data.DataObject], s:Split[data.DataObject]) : Split[data.DataObject] = {
    Console.println("MAX: " + max)
    Console.println("s  : " + s)
    // add the object at the no split with the largest area
    s.kind match {
      case SplitType.NoSplit() => if (s.area >= max.area) s else max
      case _ => Util.argmax({(a:Split[data.DataObject],b:Split[data.DataObject]) => if (a.area > b.area) a else b}, List(findSplit(max,s.first.get),findSplit(max,s.second.get)))
    }
  }
}
