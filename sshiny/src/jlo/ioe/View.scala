package jlo.ioe

import jlo.ioe.ui.{Observer,Observable,EventHandler}
import jlo.ioe.ui.Panel
import jlo.ioe.ui.Component
import jlo.ioe.ui.Button
import jlo.ioe.ui.TextComponent
import jlo.ioe.data.{DataObject,FieldChange}
import scala.collection.immutable.{Map,HashMap}

trait Binder[B] extends Observer {
  var obsHandlers : Map[Observable,List[EventHandler]] = new HashMap[Observable,List[EventHandler]]()
  def handlers = obsHandlers
  def handlers_=(h:Map[Observable,List[EventHandler]]) = obsHandlers = h

  var target : Observable = _
  def bindTo[T](f : data.Field[T]) : B = { 
    Console.println("bindTo " + f)
    target = f
    this.asInstanceOf[B] 
  }
}
trait FunctionBinder[B] {
  var func : ()=>Unit = _
  def bindTo[T](f : ()=>Unit) : B = { func = f; this.asInstanceOf[B] }
}

case class TextBinder(comp:TextComponent) extends Binder[TextBinder] {
  import jlo.ioe.ui.behavior.DocumentChanged
  
  def getComp = comp

  def trackingText = {
    getComp.setText(target.asInstanceOf[data.Text].text)
    
    listenTo(target) event {
      case FieldChange(n,v) => if (v != getComp.getText()) getComp.update(v)
    }
    listenTo(comp) event {
      case DocumentChanged(e) => {
	Console.println("textbinder: doc changed")
	target.asInstanceOf[data.Text].text(getComp.getText())
      }
    }
    this
  }
  def kind = "*TextBinder*"
} 
case class ButtonBinder(comp:Button) extends Binder[ButtonBinder] with FunctionBinder[ButtonBinder] {
  import jlo.ioe.ui.Pressed
  def trackingPresses = {
    listenTo(comp) event {
      case Pressed() => func()
    }
    this
  }
  def kind = "*ButtonBinder*"
}

class View extends Panel with Observer {
  import java.awt.event.ComponentAdapter
  import java.awt.event.ComponentEvent
  import java.awt.Dimension
  setFocusCycleRoot(true)
  setMinimumSize(new java.awt.Dimension(10,10))
  addComponentListener(new ComponentAdapter() {
    override def componentResized(e:ComponentEvent) = {
      val dim = getSize();
      Console.println("resized(" + this + ") : " + dim)
      setPreferredSize(new Dimension(getWidth(),getHeight()))
    }
  })

  implicit def view(c:TextComponent) : TextBinder = {
    Console.println("view conversion!")
//     new Throwable().printStackTrace()
    new TextBinder(c)    
  }
  implicit def view(c:Button) : ButtonBinder = {
    Console.println("view conversion!")
//     new Throwable().printStackTrace()
    new ButtonBinder(c)
  }
}
