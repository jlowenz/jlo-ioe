package jlo.ioe.ui;

import javax.swing.JButton
import javax.swing.JComponent
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import scala.actors._

case class Pressed extends ObservableEvent

class Button(var text:String) extends JButton(text) with Component {
  addActionListener(new ActionListener {
    def actionPerformed(e:ActionEvent) {
      Console.println("ap")
      fire(Pressed())
    }
  })
}
