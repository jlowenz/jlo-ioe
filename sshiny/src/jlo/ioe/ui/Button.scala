package jlo.ioe.ui;

import javax.swing.JButton
import javax.swing.JComponent

class Button(text:String) extends JButton(text) with Component[Button] {
  type C = Button
  theComponent = this
  
}
