package jlo.ioe;

import java.awt.GraphicsEnvironment;
import java.awt.DisplayMode;
import java.awt.Dimension;
import java.awt.Color
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;

object Environment {
  UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())
  val screen = new Screen(System.getProperty("user.name"))

  def main(args : Array[String]) : unit = {
    Console.println("Hello World!")
  }
}

class Screen(name : String) extends JFrame("Environment: " + name) {
  val top = new JPanel()
  val center = new JPanel()
  val bottom = SheetSelector
  var oldDisplayMode : DisplayMode = null
  setFullscreen(true)
  setVisible(true) 
  _layout

  def _layout = {
    setLayout(new BorderLayout())
    top.setBackground(Color.black)
    center.setBackground(Color.white)
    bottom.setBackground(Color.gray)
    add(top, BorderLayout.NORTH)
    add(center, BorderLayout.CENTER)
    add(bottom, BorderLayout.SOUTH)
    validate()
  }
  
  def setFullscreen(b : boolean) = {
    val screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
    val dm = screenDevice.getDisplayMode()
    if (screenDevice.isFullScreenSupported()) {
      setUndecorated(true)
      setResizable(false)
      screenDevice.setFullScreenWindow(this)
      validate();
    } else
      Console.println("full-screen mode unsupported")
  }
}

