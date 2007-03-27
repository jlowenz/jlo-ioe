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
import java.awt._
import java.awt.event._
import scala.actors._
import jlo.ioe.ui._
import jlo.ioe.ui.Panel
import jlo.ioe.command._
import scala.List

object Test {
  def main(args : Array[String]) : Unit = {
    val t = new Trie[String]
    var l = List[String]("Jason", "Josh", "Jean", "Jeannie", "Jeff", "Nettie")
    l.foreach { name => t.insert(name,name) }
    Console.println("j  : " + t.retrieve("j"))
    Console.println("je : " + t.retrieve("je"))
    Console.println("jea: " + t.retrieve("jea"))
    Console.println("n  : " + t.retrieve("n"))
  }
}

object Environment {
  UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
  Scheduler.impl = new ThreadPoolScheduler()
  val screen = new Screen(System.getProperty("user.name"))

  def main(args : Array[String]) : unit = {
    Console.println("Hello World!")
    Console.println("" + (Character.digit('a',26)-10))
    Console.println("" + new Trie[Object]().charToIndex('Z'))
  }
}

class Screen(name : String) extends JFrame("Environment: " + name) with CommandInterceptor {
  val screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
  val top = new Panel()
  val center = new Panel()
  val bottom = new SheetSelector(width())
  var commandOn = false
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
    Console.println("ogasdasd")
    setLayout(new BorderLayout())
    top.setBackground(Color.black)
    center.setBackground(Color.white)
    bottom.setBackground(Color.gray)
    add(top, BorderLayout.NORTH)
    add(center, BorderLayout.CENTER)
    add(bottom, BorderLayout.SOUTH)
    getLayeredPane().setLayout(null)
    validate()
    repaint()
  }

  def showCommand = {
    CommandInterface.component.setVisible(true)
    center(CommandInterface.component)
    getLayeredPane().add(CommandInterface.component,CommandInterface.level)
    validate()
    repaint()
  }

  def removeCommand = {
    CommandInterface.component.setVisible(false)
    getLayeredPane().remove(CommandInterface.component)
    validate()
    repaint()
    getContentPane().requestFocusInWindow
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

