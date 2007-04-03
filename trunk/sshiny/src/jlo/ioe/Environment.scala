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
// import com.db4o._

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
  Vocabulary.load

  def loadScreen : Screen = {
    if (ObjectManager.numScreens < 1) {
      screen = new Screen(System.getProperty("user.name"))
      ObjectManager.addScreen(screen)
      screen
    } else {
      screen = ObjectManager.getFirstScreen
      screen
    }
  }
  var screen : Screen = loadScreen

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

