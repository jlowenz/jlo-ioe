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

object Environment {
  UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
  //UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel")
  Scheduler.impl = new ThreadPoolScheduler()

  // todo: eventually need to support multiple screens
  Vocabulary.load

  def loadScreen : Screen = {
    if (ObjectManager.numScreens < 1) {
      Console.println("no screens")
      screen = new Screen(System.getProperty("user.name"))
      ObjectManager.addScreen(screen)
      screen
    } else {
      Console.println("found screen")
      screen = ObjectManager.getFirstScreen
      screen
    }
  }
  var screen : Screen = loadScreen
  screen.preInit
  screen.setVisible(true)

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

