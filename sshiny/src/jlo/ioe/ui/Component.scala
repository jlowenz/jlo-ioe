package jlo.ioe.ui;

import javax.swing.JComponent
import javax.swing.border.EmptyBorder

trait Component[C] extends Observable {
  type C <: JComponent
  var theComponent : JComponent = null

  def margin(t:int,l:int,b:int,r:int) : JComponent = { theComponent.setBorder(new EmptyBorder(t,l,b,r)); theComponent }
}
