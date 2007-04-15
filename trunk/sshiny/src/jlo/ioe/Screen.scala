package jlo.ioe

import java.awt.GraphicsEnvironment
import java.awt.DisplayMode
import java.awt.Dimension
import java.awt.Color
import javax.swing.JFrame
import javax.swing.JComponent
import java.awt.BorderLayout
import javax.swing.UIManager
import javax.swing._
import java.awt.AWTEvent
import java.awt.event._
import scala.actors._
import jlo.ioe.ui._
import jlo.ioe.ui.Panel
import jlo.ioe.command._
import scala.List
import javax.swing.border.LineBorder
import java.io.{ObjectOutput,ObjectInput}

object ScreenStateStorage extends data.DOStorage[ScreenState] {
  val ssdb = createDB("ScreenState", classOf[ScreenState])
  def db = ssdb
}

@SerialVersionUID(1000) class ScreenState(var name : String) extends data.DataObject {
  var sheets = List[Sheet]()
  var currentSheet : Option[Sheet] = None

  def this() = this("")

  val screen = new Screen(this)

  def storage = ScreenStateStorage
  def kind = "ScreenState"
  def defaultView = null

  def getName = name
  def addSheet(s:Sheet) = { sheets = sheets ::: List(s); currentSheet = Some(s); save }
  def removeSheet(s:Sheet) = { sheets = sheets.remove {e => s==e}; save }
  def getSheets = sheets
  def makeCurrent(s:Sheet) = { currentSheet = if (s == null) None else Some(s); }
  def getCurrent = currentSheet

  override def writeExternal(out:ObjectOutput) : Unit = {
    super.writeExternal(out)
    out.writeUTF(name)
    out.writeInt(sheets.length)
    sheets.foreach { e => out.writeObject(e.state) }
    if (currentSheet.isDefined) { out.writeBoolean(true); out.writeObject(currentSheet.get.state) }
    else { out.writeBoolean(false) }
  }
  override def readExternal(in:ObjectInput) : Unit = {
    super.readExternal(in)
    name = in.readUTF

    val count = in.readInt
    for (val i <- Iterator.range(0,count)) {
      Console.println("^^^^^^^^^^^^^^^^^^^^^^^^ reading sheet")
      sheets = sheets ::: List(new Sheet(screen,in.readObject.asInstanceOf[SheetState]))
      //screen.display(sheets.head)
    }
    val cur_? = in.readBoolean
    if (cur_?) {
      val cur = new Sheet(screen,in.readObject.asInstanceOf[SheetState])
      for (val s <- sheets) {
	if (s == cur) { currentSheet = Some(s) }
      }
    }
  }
}

// todo: split this out? probably should be in separate file
class Screen(sstate : ScreenState) extends JFrame() with CommandInterceptor {
  val name = sstate.name
  val screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
  val top = new Panel() { setBorder(new LineBorder(Color.gray,1)) } // todo: make INFOPANEL!
  val center = new Panel() { setBorder(null) }
  val sheetSelector = new SheetSelector(width())
  var commandOn = false
  Console.println("using screen state???" + sstate)
  addWindowListener(new WindowAdapter {
    override def windowOpened(e:WindowEvent) : Unit = {
      Console.println("SCREEN (*&^(*&^(*&^ windowActivated")
      init
    }
  })
 
  def preInit = _setup
  def init = {
    sstate.getSheets.foreach { s => {
      Console.println("**** loading sheet")
      newSheet(s)
    }}
    //for (val c <- sstate.getCurrent) { sheetSelector.makeCurrent(c) }
  }
    
  def this(n : String) = { 
    this(new ScreenState(n))
    Console.println("not using screen state????")
  }

  private def _setup = {
    setRootPane(new RootPane)
    setLayeredPane(new LayeredPane)
    setContentPane(new Panel)
    _layout
    setFullscreen(true)
    enableEvents(AWTEvent.KEY_EVENT_MASK)
    setFocusable(true)
    getContentPane().asInstanceOf[JComponent].grabFocus()
  }

  private def _layout = {
    setLayout(new BorderLayout())
    top.setBackground(Color.black)
    center.setBackground(Color.white)
    //sheetSelector.setBackground(Color.gray)
    add(top, BorderLayout.NORTH)
    add(center, BorderLayout.CENTER)
    add(sheetSelector, BorderLayout.SOUTH)
    getLayeredPane().setLayout(null)
    validate()
    repaint()
  }

  def nextSheet = sstate.getCurrent match {
    case Some(s) => sstate.makeCurrent(sheetSelector.nextSheet)
    case None => {}
  }

  def prevSheet = sstate.getCurrent match {
    case Some(s) => sstate.makeCurrent(sheetSelector.prevSheet)
    case None => {}
  }

  def display(aSheet:Sheet) = {
    val displayer = new Runnable {
      def run : Unit = {
	sstate.getCurrent match {
	  case Some(s) => { center.remove(s); s.setVisible(false); }
	  case None => {}
	}
	aSheet.setVisible(true)
	center.add(aSheet)
	center.invalidate()
	validate()
	repaint()
	sstate.makeCurrent(aSheet)
      }
    }
    if (Thread.currentThread.getName().startsWith("AWT")) {
      displayer.run
    } else {
      SwingUtilities.invokeLater(displayer)
    }
  }

  def newSheet(a:data.DataObject) = {
    a.defaultView.setPreferredSize(getSize())
    // todo: keep track of the sheets?
    val s = new Sheet(this,a)
    sheetSelector.newSheet(s)
    sstate.addSheet(s)
    display(s)
  }

  def newSheet(s:Sheet) = {
    s.obj.defaultView.setPreferredSize(getSize())
    sheetSelector.newSheet(s)
    display(s)
  }

  def splitSheet(a:data.DataObject) = {
    sheetSelector.currentSheet.split(a)
  }

  def showCommand = {
    SwingUtilities.invokeLater(new Runnable {
      def run : Unit = {
	CommandInterface.component.setVisible(true)
	center(CommandInterface.component)
	getLayeredPane().add(CommandInterface.component,CommandInterface.level)
	validate()
	repaint()
      }
    })
  }

  def removeCommand = {
    SwingUtilities.invokeLater(new Runnable {
      def run : Unit = {
	CommandInterface.component.setVisible(false)
	getLayeredPane().remove(CommandInterface.component)
	validate()
	repaint()
	getContentPane().requestFocusInWindow
      }
    })
  }
  
  def center(c:JComponent) = {
    val w = (width()-c.getWidth())/2
    val h = (height()-c.getHeight())/2
    c.setLocation(w,h)
  }
  def width() = screenDevice.getDisplayMode().getWidth()
  def height() = screenDevice.getDisplayMode().getHeight()
  
  private def setFullscreen(b : boolean) = {
    val dm = screenDevice.getDisplayMode()
    if (screenDevice.isFullScreenSupported()) {
      setUndecorated(true)
      setResizable(false)
      screenDevice.setFullScreenWindow(this)
      validate();
    } else
      Console.println("full-screen mode unsupported")
  }
   
  override def commandRequested : Unit = if (!commandOn) { commandOn = true; showCommand }
				  else { commandOn = false; removeCommand }
}

