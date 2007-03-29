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

}

class TextField extends JTextField with TextComponent with ActionCapable {
  
}
