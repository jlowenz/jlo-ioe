package jlo.ioe;

import scala.Iterator._
import javax.swing.JPanel
import javax.swing.JButton
import jlo.ioe.ui._

object SheetSelector extends JPanel with Observable {
  for (val i <- range(1,5)) {
    add(new Button("Press me " + i))
  }
}
