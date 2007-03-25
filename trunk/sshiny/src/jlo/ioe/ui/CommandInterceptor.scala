package jlo.ioe.ui

import javax.swing._
import java.awt.event._

trait CommandInterceptor extends java.awt.Component {
  val DIFF = 500L
  var lastTime = System.currentTimeMillis
  
  def _parent = {
    val p = getParent()
    if (p != null) Some(p) else None
  }

  override def processKeyEvent(e:KeyEvent) {
    if (KeyEvent.getKeyText(e.getKeyCode()) == "Command" && e.getID() == KeyEvent.KEY_PRESSED) {
      val currTime = System.currentTimeMillis
      if ((currTime - lastTime) < DIFF) {
	_parent match {
	  case Some(p) => p.asInstanceOf[CommandInterceptor].commandRequested
	  case None => {}
	}
	lastTime = 0
      }
      else lastTime = currTime
    }
    super.processKeyEvent(e)
  }
    
  def commandRequested : Unit = _parent match { case Some(p) => p.asInstanceOf[CommandInterceptor].commandRequested
					       case None => {} }
}
