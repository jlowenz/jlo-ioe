package jlo.ioe.ui

import javax.swing._
import java.awt.event._

trait CommandInterceptor extends java.awt.Component {
  val DIFF = 500L
  var lastTime = System.currentTimeMillis
  addKeyListener(new KeyListener() {
    override def keyPressed(e:KeyEvent) = { 
      val ks = KeyStroke.getKeyStrokeForEvent(e)
      ks.toString match {
	case "meta pressed TAB" => { Environment.nextSheet; lastTime = 0 }
	case "shift meta pressed TAB" => { Environment.prevSheet; lastTime = 0 }
	case "meta pressed META" => {
	  val currTime = System.currentTimeMillis
	  if ((currTime - lastTime) < DIFF) {
	    _parent match {
	      case Some(p) => Environment.commandRequested
	      case None => {}
	    }
	    lastTime = 0
	  }
	  else lastTime = currTime
	}
	case _ => {}
      }	  
    }
    override def keyReleased(e:KeyEvent) = { }
    override def keyTyped(e:KeyEvent) = { }
  })

  def _parent = {
    val p = getParent()
    if (p != null) Some(p) else None
  }

//   override def processKeyEvent(e:KeyEvent) {
//     if (KeyEvent.getKeyText(e.getKeyCode()) == "Command" && e.getID() == KeyEvent.KEY_PRESSED) {
//       val currTime = System.currentTimeMillis
//       if ((currTime - lastTime) < DIFF) {
// 	_parent match {
// 	  case Some(p) => p.asInstanceOf[CommandInterceptor].commandRequested
// 	  case None => {}
// 	}
// 	lastTime = 0
//       }
//       else lastTime = currTime
//     }
//     super.processKeyEvent(e)
//   }
    
  def commandRequested : Unit = _parent match { case Some(p) => p.asInstanceOf[CommandInterceptor].commandRequested
					       case None => {} }
}
