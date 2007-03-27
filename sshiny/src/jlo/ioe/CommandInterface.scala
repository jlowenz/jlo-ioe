package jlo.ioe;

import java.awt.Dimension
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.event._
import javax.swing._
import javax.swing.border._
import javax.swing.event._
import scala.compat._
import jlo.ioe.ui.TextField
import jlo.ioe.ui._
import jlo.ioe.command._

object CommandInterface extends Observer {
  val commandField  = new TextField() with behavior.KeyTracker {
    actions areHandled;
    setBorder(new CompoundBorder(new LineBorder(Color.black,1),new EmptyBorder(2,2,2,2)))
    override def toString : String = "commandField"
  }
  val commandPanel = new Panel with Observer with behavior.AncestorTracker {
    setPreferredSize(new Dimension(400,75))
    setSize(getPreferredSize())
    setOpaque(false)
    setLayout(new BorderLayout())
    setBorder(new EmptyBorder(10,10,10,10))
    initComponents()
    
    override def toString : String = "commandPanel"

    override def paintChildren(g : Graphics) = {
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f))
      super.paintChildren(g2)
    }
    
    override def paintComponent(g : Graphics) = {
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setColor(Color.gray.brighter)
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f))
      g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
    }

    private def initComponents() = {
      val jLabel1 = new Label;
      
      setOpaque(false);
      setPreferredSize(new java.awt.Dimension(400, 100));
      jLabel1.setText("Enter a command:");
      jLabel1.setFont(jLabel1.getFont().deriveFont(12f))
      
      commandField.setText("");
      commandField.setOpaque(false);
      
      val layout = new org.jdesktop.layout.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
	layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
	.add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
             .addContainerGap()
             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                  .add(org.jdesktop.layout.GroupLayout.LEADING, commandField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Math.MAX_SHORT)
                  .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Math.MAX_SHORT))
             .addContainerGap())
      );
      layout.setVerticalGroup(
	layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
	.add(layout.createSequentialGroup()
             .addContainerGap()
             .add(jLabel1)
               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
             .add(commandField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
             .addContainerGap(40, Math.MAX_SHORT))
      );
      commandField.requestFocusInWindow()
    }
  }
  
  listenTo(commandPanel) event { 
    case behavior.Ancestor("added",e) => {
      Console.println("shown")
      commandField.setText("")
      commandField.requestFocusInWindow
    }
    case behavior.Ancestor(_,_) => {} // ignore the other cases
  }

  var currentCommand : Option[Command] = None
  listenTo(commandField) event {
    case behavior.Action(_) => component.commandRequested
    case behavior.Key(e) => e.getKeyCode() match { 
      case KeyEvent.VK_ESCAPE => component.commandRequested
      case KeyEvent.VK_SPACE => currentCommand.foreach {c => c.termCompleted(suggestionMatches)}
      case KeyEvent.VK_TAB => selectSuggestedValue
      case _ => {
	val lastFragment = commandField.getText().split(" ").toList.last
	updateSuggestions(lastFragment)
	currentCommand match {
	  case Some(c) => c.updateFragment(lastFragment)
	  case None => {
	    currentCommand = Some(new Command)
	    currentCommand.get.updateFragment(commandField.getText())
	  }
	}
	Console.println("Command: " + currentCommand)
      }
    }
  }

  private def suggestionMatches = {
    val lastFragment = commandField.getText().split(" ").toList.last
    List[VocabularyTerm]()
  }
  
  private def updateSuggestions(frag:String) = {
    
  }

  private def selectSuggestedValue = {
    
  }

  def level = JLayeredPane.MODAL_LAYER
  def component = commandPanel
}
