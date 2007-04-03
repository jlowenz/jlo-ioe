package jlo.ioe;

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

object Test {
  def main(args : Array[String]) : Unit = {
    Scheduler.impl = new SingleThreadedScheduler()
    val t = new Trie[String]
    var l = List[String]("Jason", "Josh", "Jean", "Jeannie", "Jeff", "Nettie")
    l.foreach { name => t.insert(name,name) }
    Console.println("j  : " + t.retrieve("j"))
    Console.println("je : " + t.retrieve("je"))
    Console.println("jea: " + t.retrieve("jea"))
    Console.println("n  : " + t.retrieve("n"))

    Vocabulary.addTerm(new New)
    Console.println(Vocabulary.possibleTerms("o"))
    Console.println(Vocabulary.possibleTerms("n"))
    
  }
}

object Environment {
  //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
  //UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel")
  Scheduler.impl = new ThreadPoolScheduler()

  // todo: eventually need to support multiple screens
  val screen = new Screen(System.getProperty("user.name"))
  Vocabulary.load
  
  // todo: this indirection is here to handle multiple screens
  def newSheet(a:data.DataObject) = screen.newSheet(a)
  def splitSheet(a:data.DataObject) = screen.splitSheet(a)
  def nextSheet = screen.nextSheet
  def prevSheet = screen.prevSheet
  def commandRequested = screen.commandRequested

  def main(args : Array[String]) : unit = {
    Console.println("Hello World!")
  }
}

// todo: split this out? probably should be in separate file
class Screen(name : String) extends JFrame("Environment: " + name) with CommandInterceptor {
  val screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
  val top = new Panel() { setBorder(new LineBorder(Color.gray,1)) } // todo: make INFOPANEL!
  val center = new Panel() { setBorder(null) }
  val sheetSelector = new SheetSelector(width())
  var commandOn = false
  var currentSheet : Option[Sheet] = None

  setRootPane(new RootPane)
  setLayeredPane(new LayeredPane)
  setContentPane(new Panel)
  _layout
  setFullscreen(true)
  setVisible(true) 
  enableEvents(AWTEvent.KEY_EVENT_MASK)
  setFocusable(true)
  getContentPane().asInstanceOf[JComponent].grabFocus()

  def _layout = {
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

  def nextSheet = currentSheet match {
    case Some(s) => currentSheet = Some(sheetSelector.nextSheet)
    case None => {}
  }

  def prevSheet = currentSheet match {
    case Some(s) => currentSheet = Some(sheetSelector.prevSheet)
    case None => {}
  }

  def display(aSheet:Sheet) = {
    val displayer = new Runnable {
      def run : Unit = {
	currentSheet match {
	  case Some(s) => { center.remove(s); s.setVisible(false); }
	  case None => {}
	}
	aSheet.setVisible(true)
	center.add(aSheet)
	center.invalidate()
	validate()
	repaint()
	currentSheet = Some(aSheet)
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

