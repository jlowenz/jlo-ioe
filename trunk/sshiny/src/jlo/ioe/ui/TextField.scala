package jlo.ioe.ui;

import javax.swing._
import javax.swing.text.JTextComponent

package behavior {
  import javax.swing.event.DocumentEvent
  import javax.swing.event.DocumentListener

  case class DocumentChanged(e:DocumentEvent) extends ObservableEvent

  trait DocumentTracker requires TextComponent {
    getDocument.addDocumentListener(new DocumentListener {
      def changedUpdate(e:DocumentEvent) {
	fire(DocumentChanged(e))
      }
      def insertUpdate(e:DocumentEvent) { fire(DocumentChanged(e)) }
      def removeUpdate(e:DocumentEvent) { fire(DocumentChanged(e)) }
    })
  }
}

trait TextComponent extends JTextComponent with Component {
  import java.awt.event.ComponentListener
  import java.awt.event.ComponentEvent
  import javax.swing.event.AncestorListener
  import javax.swing.event.AncestorEvent

  def initialFocus = {
    addComponentListener(new ComponentListener {
      override def componentShown(e:ComponentEvent) : Unit = TextComponent.this.requestFocusInWindow() 
      override def componentResized(e:ComponentEvent) : Unit = TextComponent.this.requestFocusInWindow() 
      override def componentHidden(e:ComponentEvent) : Unit = {}
      override def componentMoved(e:ComponentEvent) : Unit = {}
    })
    addAncestorListener(new AncestorListener {
      override def ancestorAdded(e:AncestorEvent) : Unit = TextComponent.this.requestFocusInWindow
      override def ancestorMoved(e:AncestorEvent) : Unit = TextComponent.this.requestFocusInWindow
      override def ancestorRemoved(e:AncestorEvent) : Unit = {}
    })
    this
  }
} 

class TextField extends JTextField with TextComponent with ActionCapable {
  
}
