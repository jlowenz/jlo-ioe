package jlo.ioe.ui;

import java.awt.event._
import javax.swing.JComponent
import javax.swing.border.EmptyBorder
import java.awt.Dimension
import scala.collection.immutable.{HashSet,Set,Map,HashMap}

package behavior {
  case class Action(e:ActionEvent) extends ObservableEvent
}

trait ActionCapable {
  def addActionListener(a:ActionListener)
  def removeActionListener(a:ActionListener)
}

trait ActionsHandler {
  def areHandled
  def areNotHandled
}

trait Component extends JComponent with CommandInterceptor with Observable {
  var actions = if (this.isInstanceOf[ActionCapable]) {
    val o = this.asInstanceOf[ActionCapable]
    new ActionsHandler {
      val _handler = new ActionListener {
	def actionPerformed(e:ActionEvent) = fire(behavior.Action(e))
      }
      def areHandled = o.addActionListener(_handler)
      def areNotHandled = o.removeActionListener(_handler)      
    }
  }
  else
    new ActionsHandler {
      def areHandled = {}
      def areNotHandled = {}
    }
  actions areNotHandled;

  def onSwingThread(f:()=>Unit) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      def run : Unit = {
	f()
      }
    })
  }

  var obsHandlers : Map[Observable,List[EventHandler]] = new HashMap[Observable,List[EventHandler]]()
  def handlers = obsHandlers
  def handlers_=(h:Map[Observable,List[EventHandler]]) = obsHandlers = h

  var obsListeners : Set[Observer] = new HashSet[Observer]()
  def listeners = obsListeners
  def listeners_=(o:Set[Observer]) = obsListeners = o


  // bindings
  def update(v:Any) = {}

  // conveniences
  def margin(t:int,l:int,b:int,r:int) : Component = { setBorder(new EmptyBorder(t,l,b,r)); this }  
  def preferredWidth = getPreferredSize().getWidth()
  def preferredHeight = getPreferredSize().getHeight()
  def preferredWidth(w:double) : Component = { setPreferredSize(new Dimension(w,preferredHeight)); this }
  def preferredHeight(h:double) : Component = { setPreferredSize(new Dimension(preferredWidth,h)); this }  
  def preferredSize(w:double,h:double) : Component = { setPreferredSize(new Dimension(w,h)); this }
  def setWidth(w:double) : Component = { setSize(new Dimension(w,getHeight())); this }
  def setHeight(h:double) : Component = { setSize(new Dimension(getWidth(),h)); this }

  // CONVERSIONS
  implicit def doubleToInt(x:double) : int = x.asInstanceOf[int]

}

class Viewport extends javax.swing.JViewport with Component {}

class Scroller(o:Component) extends javax.swing.JScrollPane(o) with Component {
  //setBorder(null)
  override def createViewport() : javax.swing.JViewport = {
    new Viewport
  }
}

// class Frame(title:String) extends javax.swing.JFrame(title) with Component {}
