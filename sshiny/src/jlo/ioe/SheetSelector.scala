package jlo.ioe;

import scala.Iterator._
import javax.swing.JPanel
import java.awt.GridLayout
import java.awt.Dimension
import jlo.ioe.ui._
import scala.actors._


@serializable
@SerialVersionUID(1000L)
class SheetSelector(width : int) extends Panel with Observable with Observer {
  import java.awt.Color
  var sheets : Option[SheetButton] = None
  var current : Option[SheetButton] = None

  setBackground(Color.white)
  setMinimumSize(new Dimension(width,27))
  setLayout(new GridLayout(1,0,0,0))

  def currentSheet = current.get.sheet

//   def makeCurrent(sheet:Sheet) : Unit = {
//     current match {
//       case Some(s) => if (s != sheet) { nextSheet; makeCurrent(sheet) }
//       case None => {}
//     }
//   }

  def newSheet(aSheet:Sheet) = sheets match {
    case Some(s) => {
      val btn = new SheetButton(aSheet)
      btn.prev = s.prev
      btn.next = s
      s.prev.next = btn
      s.prev = btn
      current = Some(s.prev)
      
      hideOthers(current.get)
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
  
  def nextSheet : Sheet = current match {
    case Some(c) => { 
      current = Some(c.next); 
      current.get.select(true)
      hideOthers(current.get)
      current.get.sheet.display
    }
    case None => null
  }
  def prevSheet : Sheet = current match {
    case Some(c) => { 
      current = Some(c.prev); 
      current.get.select(true)
      hideOthers(current.get)
      current.get.sheet.display
    }
    case None => null
  }

  private def hideOthers(shown : SheetButton) = {
    var curr = shown.next
    while (curr != shown) {
      curr.select(false)
      curr = curr.next
    }
  }

  @serializable
  @SerialVersionUID(1000L)
  class SheetButton(theSheet:Sheet) extends Button(theSheet.title) with Observer {
    import java.awt.Color
    import java.awt.Graphics
    import javax.swing.border.EmptyBorder
    import javax.swing.UIManager
    
    var next : SheetButton = this
    var prev : SheetButton = this
    var selected : boolean = true
    setBorder(new EmptyBorder(1,1,1,1))
    setBorderPainted(false)
    //setContentAreaFilled(false)
    preferredWidth(10000) 
    listenTo(this) event {
      case Pressed() => {
	hideOthers(this)
	select(true)
	theSheet.display
      }
    }
    listenTo(theSheet) event {
      case SheetTitleChanged(t) => setText(t)
    }

    def select(b:boolean) = { 
      selected = b
      onSwingThread(() => {validate; repaint})
    }

    override protected def paintComponent(g:Graphics) : Unit  = {
      if (!selected) {
	setBackground(Color.gray.darker.darker)
	setForeground(Color.white)
      } else {
	setBackground(UIManager.getDefaults.getColor("Panel.background"))
	setForeground(Color.gray.darker.darker)
      }
      super.paintComponent(g)
    }

    def sheet = theSheet
  }  
}
