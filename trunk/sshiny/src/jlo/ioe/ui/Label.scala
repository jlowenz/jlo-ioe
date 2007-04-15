package jlo.ioe.ui;

import javax.swing._

class Label extends JLabel with Component {
  def this(t:String) = { this(); setText(t) }
  override def toString = "Label(" + getText + ")"
}
