package jlo.ioe.ui.behavior

import java.awt.event._
import javax.swing.event._
import jlo.ioe.ui._

case class Key(e:KeyEvent) extends ObservableEvent

object KeyTracker {
  implicit def keyToString(e:KeyEvent) : String = {
    val m = KeyEvent.getKeyModifiersText(e.getModifiers)
    val k = KeyEvent.getKeyText(e.getKeyCode)
    return m + k
  }
}

trait KeyTracker requires Component {
  import KeyTracker._
  addKeyListener(new KeyListener {
    override def keyPressed(e:KeyEvent) : Unit = fire(Key(e))
    override def keyReleased(e:KeyEvent) : Unit = fire(Key(e))
    override def keyTyped(e:KeyEvent) : Unit = fire(Key(e))
  })

}
