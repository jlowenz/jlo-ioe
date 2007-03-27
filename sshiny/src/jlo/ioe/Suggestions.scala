package jlo.ioe

import jlo.ioe.ui._

object Suggestions {
  val list = new SList {
    orientation(SList.VERTICAL_WRAP)    
  }
  val ui = new Panel {
    setOpaque(false)
    preferredWidth(50)
    add(new SList)
  }
    
}
