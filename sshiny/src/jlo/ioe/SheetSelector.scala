package jlo.ioe;

import scala.Iterator._
import javax.swing.JPanel
import javax.swing.BoxLayout
import java.awt.Dimension
import jlo.ioe.ui._
import scala.actors._

class SheetSelector(width : int) extends Panel with Observable with Observer {
  setMinimumSize(new Dimension(width,10))
  setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS))
  // for testing!
  for (val i <- range(1,5)) {
    addSelector(new Sheet)
  }

  def addSelector(aSheet:Sheet) = {
    add(new SelectButton(aSheet))
  }
  
  def removeSelector(sheet:Sheet) = {
      
  }
  
  class SelectButton(theSheet:Sheet) extends Button(theSheet.title) with Observer {
    preferredWidth(10000) 
    listenTo(this) event {
      case Pressed() => Console.println("SelectButton pressed!")
    }
  }  
}
