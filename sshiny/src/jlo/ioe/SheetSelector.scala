package jlo.ioe;

import scala.Iterator._
import javax.swing.JPanel
import javax.swing.BoxLayout
import java.awt.Dimension
import jlo.ioe.ui._
import scala.actors._

class SheetSelector(width : int) extends Panel with Observable with Observer {
  var sheets : Option[SheetButton] = None
  var current : Option[SheetButton] = None

  setMinimumSize(new Dimension(width,27))
  setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS))

  def currentSheet = current.get.sheet

  def newSheet(aSheet:Sheet) = sheets match {
    case Some(s) => {
      val btn = new SheetButton(aSheet)
      btn.prev = s.prev
      btn.next = s
      s.prev.next = btn
      s.prev = btn
      current = Some(s.prev)
      
      add(current.get)
      validate
      repaint()
    }
    case None => {
      sheets = Some(new SheetButton(aSheet))
      current = sheets
      add(sheets.get)
      validate()
      repaint()
    }
  }
  def removeSheet = { 
    current match {
      case Some(c) => {
	c.prev.next = c.next
	c.next.prev = c.prev
	validate; 
	repaint() 
      }
      case None => {}
    }
  }    
  
  def nextSheet = current match {
    case Some(c) => { current = Some(c.next); current.get.sheet.display }
    case None => {}
  }
  def prevSheet = current match {
    case Some(c) => { current = Some(c.prev); current.get.sheet.display }
    case None => {}
  }

  class SheetButton(theSheet:Sheet) extends Button(theSheet.title) with Observer {
    var next : SheetButton = this
    var prev : SheetButton = this
    preferredWidth(10000) 
    listenTo(this) event {
      case Pressed() => theSheet.display
    }

    def sheet = theSheet
  }  
}
