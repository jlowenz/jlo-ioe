package jlo.ioe;

import jlo.ioe.ui._
import javax.swing.border.{CompoundBorder,LineBorder,EmptyBorder}
import java.awt.Color

class Split(obj:data.DataObject) extends Panel {
  
}

class Sheet(screen:Screen, obj:data.DataObject) extends Panel {
  var titleString : String = obj.toString // todo: how to track this?
  var splits = List[Split]()

  setBorder(new CompoundBorder(new LineBorder(Color.gray,1), new EmptyBorder(2,2,2,2)))
  add(obj.defaultView)

  def title = titleString
  def display = {
    Console.println("Shown!")
    screen.display(this)
  }

  def split(a:data.DataObject) = {}
}
