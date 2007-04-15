package jlo.ioe.ui

import javax.swing._
import javax.swing.event._
import scala.collection.immutable.{HashSet,Set,HashMap,Map}

case class Update[A](l:List[A]) extends ObservableEvent

trait SListDelegate[A] extends Observable {
  def numElements : int
  def elementAt(i:int) : A
  protected def fireUpdate(changed:List[A]) = {
    fire(Update[A](changed))
  }
}

object SList {
  val VERTICAL = JList.VERTICAL
  val VERTICAL_WRAP = JList.VERTICAL_WRAP
  val HORIZONTAL_WRAP = JList.HORIZONTAL_WRAP
}

class SList[A] extends JList with Component {  
  var delegate : SListDelegate[A] = null   
  delegateTo(makeDelegate(List[A]())) 

  def this(l:List[A]) = { this(); delegateTo(makeDelegate(l)) }
  
  private def makeDelegate(l:List[A]) = new SListDelegate[A] {
    private var data = l
    private var obsListeners : Set[Observer] = new HashSet[Observer]()
    def listeners = obsListeners
    def listeners_=(o:Set[Observer]) = obsListeners = o

    def numElements : int = data.length
    def elementAt(i:int) : A = data(i)
  }

  def orientation(v:int) = setLayoutOrientation(v)
  def delegateTo(d:SListDelegate[A]) = { 
    delegate = d
    setModel(new DelegateListModel(delegate))
  }

  class DelegateListModel(delegate:SListDelegate[A]) extends ListModel with Observer {
    private var listeners = List[ListDataListener]()
    private var obsHandlers : Map[Observable,List[EventHandler]] = new HashMap[Observable,List[EventHandler]]()
    def handlers = obsHandlers
    def handlers_=(h:Map[Observable,List[EventHandler]]) = obsHandlers = h

    def addListDataListener(l:ListDataListener) : Unit = {
      listeners = l :: listeners
      listenTo(delegate) event {
	case Update(c) => listeners.foreach {
	  l => l.contentsChanged(
	    new ListDataEvent(SList.this, 
			      ListDataEvent.CONTENTS_CHANGED, 
			      0, delegate.numElements + 1))
	}
      }
    }
    def removeListDataListener(l:ListDataListener) : Unit = {
      listeners = listeners filter {el => el == l}
    }
    def getElementAt(index:int) : Object = delegate.elementAt(index).asInstanceOf[Object]
    def getSize() : int = delegate.numElements
  }
}
