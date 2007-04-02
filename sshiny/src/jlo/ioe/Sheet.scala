package jlo.ioe;

import jlo.ioe.ui._
import javax.swing.border.{CompoundBorder,LineBorder,EmptyBorder}
import java.awt.Color


class Sheet(screen:Screen, obj:data.DataObject) extends Panel {
  var titleString : String = obj.toString // todo: how to track this?
  var splits = new Split(Some(obj), Aspect.Horizontal, {(a:data.DataObject)=>a.defaultView})
  var splitPane = new Splitter
  setBorder(new CompoundBorder(new LineBorder(Color.gray,1), new EmptyBorder(2,2,2,2)))
  add(splitPane)
  splitPane.resplit(splits)
  
  def title = titleString
  def display = {
    Console.println("Shown!")
    screen.display(this)
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
      case SplitType.NoSplit() => if (s.area > max.area) s else max
      case _ => Util.argmax({(a:Split[data.DataObject],b:Split[data.DataObject]) => if (a.area > b.area) a else b}, List(findSplit(max,s.first.get),findSplit(max,s.second.get)))
    }
  }
}
