package jlo.ioe.ui

import javax.swing.JEditorPane

class TextPane extends JEditorPane with TextComponent {
  override def update(v:Any) = setText(v.asInstanceOf[String])
}
