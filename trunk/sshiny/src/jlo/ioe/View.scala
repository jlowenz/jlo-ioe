package jlo.ioe

import jlo.ioe.ui.Observer
import jlo.ioe.ui.Panel
import jlo.ioe.ui.Component
import jlo.ioe.ui.Button
import jlo.ioe.ui.TextComponent
import jlo.ioe.data.DataObject

trait Binder[B] extends DataObject {
  var target : AnyRef = _
  def bindTo[T](f : DataObject#Field[T]) : B = { 
    Console.println("bindTo " + f)
    target = f
    Console.println("\ttarget: " + target)
    this.asInstanceOf[B] 
  }
  override def defaultView = null
}
trait FunctionBinder[B] extends DataObject {
  var func : ()=>Unit = _
  def bindTo[T](f : ()=>Unit) : B = { func = f; this.asInstanceOf[B] }
  override def defaultView = null
}

case class TextBinder(comp:TextComponent) extends Binder[TextBinder] with Observer {
  import jlo.ioe.ui.behavior.DocumentChanged
  Console.println("TextBinder(" + comp + ")")
  def trackingText = {
    listenTo(target) event {
      case FieldChange(n,v) => comp.update(v)
    }
    listenTo(comp) event {
      case DocumentChanged(e) => target() = comp.getText()
    }
    this
  }
  def kind = "*TextBinder*"
} 
case class ButtonBinder(comp:Button) extends Binder[ButtonBinder] with FunctionBinder[ButtonBinder] with Observer {
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
  implicit def view(c:TextComponent) : TextBinder = new TextBinder(c)
  implicit def view(c:Button) : ButtonBinder = new ButtonBinder(c)
}