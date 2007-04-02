package jlo.ioe.ui;

import java.awt.event._
import javax.swing.JComponent
import javax.swing.border.EmptyBorder
import java.awt.Dimension

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
//   addComponentListener(new ComponentAdapter() {
//     override def componentResized(e:ComponentEvent) = {
//       val dim = getSize();
//       Console.println("resized(" + this + ") : " + dim)
//       setPreferredSize(new Dimension(getWidth(),getHeight()))
//     }
//   })

  override def setSize(dim:Dimension) : Unit = {
    if (dim.getWidth() <= 0 || dim.getHeight() <= 0) {
      new Throwable().printStackTrace()
    }
    super.setSize(dim)
  }
  override def setSize(w:int,h:int) : Unit = {
    if (w <= 0 || h <= 0) {
      new Throwable().printStackTrace()
    }
    super.setSize(w,h)
  }
  override def resize(dim:Dimension) : Unit = {
    if (dim.getWidth() <= 0 || dim.getHeight() <= 0) {
      new Throwable().printStackTrace()
    }
    super.resize(dim)
  }
  override def resize(w:int,h:int) : Unit = {
    if (w <= 0 || h <= 0) {
      new Throwable().printStackTrace()
    }
    super.resize(w,h)
  }

  override def setBounds(x:int,y:int,w:int,h:int) : Unit = {
    if (w <= 0 || h <= 0) {
      new Throwable().printStackTrace()
    }    
    super.setBounds(x,y,w,h);
  }
  override def setBounds(r:java.awt.Rectangle) : Unit = {
    if (r.width <= 0 || r.height <= 0) {
      new Throwable().printStackTrace()
    }    
    super.setBounds(r);
  }

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
