package jlo.ioe.ui

import javax.swing._
import javax.swing.event._

package model {
  // finish me!
  trait SListDelegate {
    def asListModel : ListModel
    def numElements : int
    def elementAt(i:int) : Any
  }
}

object SList {
  val VERTICAL = JList.VERTICAL
  val VERTICAL_WRAP = JList.VERTICAL_WRAP
  val HORIZONTAL_WRAP = JList.HORIZONTAL_WRAP
}

class SList extends JList with Component {
  def orientation(v:int) = setLayoutOrientation(v)
}
