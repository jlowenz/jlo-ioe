package jlo.ioe;

import scala.Iterator._
import javax.swing.JPanel
import javax.swing.BoxLayout
import java.awt.Dimension
import jlo.ioe.ui._
import scala.actors._

class SheetSelector(width : int) extends Panel with Observable with Observer {
  var sheets = List[SheetButton]()
  var current : SheetButton = _

  setMinimumSize(new Dimension(width,27))
  setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS))

  def currentSheet = current.sheet

  def newSheet(aSheet:Sheet) = {
    sheets = new SheetButton(aSheet) :: sheets
    add(sheets.head)
    validate
  }
  def removeSheet = { remove(currentSheet); validate }    

  class SheetButton(theSheet:Sheet) extends Button(theSheet.title) with Observer {
    def sheet = theSheet
    preferredWidth(10000) 
    listenTo(this) event {
      case Pressed() => theSheet.show
    }
  }  
}
