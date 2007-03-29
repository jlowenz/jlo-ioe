package jlo.ioe

import jlo.ioe.command._
import jlo.ioe.ui._
import java.awt.Container
import javax.swing.SwingUtilities

object Suggestions {
  private val uilist = new SList[VocabularyTerm] {
    orientation(SList.HORIZONTAL_WRAP)
  }
  private val ui = new Panel {
    import javax.swing.border.EmptyBorder
    import java.awt.BorderLayout
    import java.awt.AlphaComposite
    import java.awt.RenderingHints
    import java.awt.Graphics
    import java.awt.Graphics2D
    import java.awt.Color

    setOpaque(false)
    setSize(400,200)
    setBorder(new EmptyBorder(10,10,10,10))
    setLayout(new BorderLayout())
    add(uilist)

    override def paintChildren(g : Graphics) = {
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f))
      super.paintChildren(g2)
    }
    
    override def paintComponent(g : Graphics) = {
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setColor(Color.yellow.brighter.brighter)
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f))
      g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
    }
  }  
  class Delegate extends SListDelegate[VocabularyTerm] {
    var data = List[VocabularyTerm]()
    def numElements = data.length
    def elementAt(i:int) = data(i)
    def update(l:List[VocabularyTerm]) = { data = l; fireUpdate(data) }
  }
  val model = new Delegate
  uilist.delegateTo(model)

  implicit def doubleToInt(d:double):int = d.asInstanceOf[int]
  implicit def toRunnable[A](f:Function0[A]) = new Runnable { def run() { f() } }

  var shown = false;
  def showAt(x:double,y:double,c:Container) = {
    if (!shown) {
      shown = true;
      addTo(c)
      SwingUtilities.invokeLater(toRunnable({
	ui.setLocation(x,y)
	ui.setVisible(true)
	ui.validate
      }))
    }
  }

  def hide(c:Container) = { 
    if (shown) {
      SwingUtilities.invokeLater(toRunnable({
	ui.setVisible(false)
	shown = false 
	removeFrom(c)
	ui.validate
      }))
    }			       
  }

  def showSuggestions(s:List[VocabularyTerm]) = {
    Console.println("showSuggestions")
    model.update(s)
  }

  private def removeFrom(c:Container) = c.remove(ui)
  private def addTo(c:Container) = { c.add(ui) }
}
