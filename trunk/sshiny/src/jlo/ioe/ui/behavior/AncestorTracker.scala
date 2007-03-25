package jlo.ioe.ui.behavior

import javax.swing.event._
import jlo.ioe.ui._

case class Ancestor(what:String,e:AncestorEvent) extends ObservableEvent { def evt = e }

object AncestorTracker {
  val added = 'added
  val removed = 'removed
  val moved = 'moved
}

trait AncestorTracker requires Component {
  import AncestorTracker._
  addAncestorListener(new AncestorListener {
    override def ancestorAdded(e:AncestorEvent) : Unit = fire(Ancestor("added",e)) 
    override def ancestorRemoved(e:AncestorEvent) : Unit = fire(Ancestor("removed",e))
    override def ancestorMoved(e:AncestorEvent) : Unit = fire(Ancestor("moved",e))
  })
}
