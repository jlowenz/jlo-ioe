package jlo.ioe;

import jlo.ioe.ui._
import javax.swing.JPanel

class Sheet extends JPanel with Observable {
  var titleString : String = "?"

  def title = titleString
  def select = Console.println("Selected!")
}
