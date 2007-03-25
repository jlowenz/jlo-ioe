package jlo.ioe.ui;

import scala.collection.jcl._
import scala.collection.immutable._
import scala.actors.Actor._

abstract class ObservableEvent

trait Observer {
  val handler = actor {
    loop {
      react {
	case Handle(o, e) => { 
	  try { handlers.get(o).getOrElse(List()).foreach { f => f(e) } }
	  catch { case e:Throwable => e.printStackTrace() }
	}
	case Event(o, f) => {
	  Console.println("event " + this)
	  var l = handlers.get(o).getOrElse(List[Function1[ObservableEvent,Unit]]())
	  l = f :: l
	  handlers = handlers.update(o, l)
	}
      }
    } 
  }
 
  class ListenTarget(o:Observable) {
    def event(f:Function1[ObservableEvent,Unit]) = handler ! Event(o,f)
  }

  var handlers = Map[Observable,List[Function1[ObservableEvent,Unit]]]()
  handler.start()

  def listenTo(o:Observable)  = { o.addObserver(this); new ListenTarget(o) }
  def ignore(o:Observable) : this.type = { o.removeObserver(this); this }
  def handle(o:Observable, e:ObservableEvent) = handler ! Handle(o,e)

  
  abstract class Msg
  case class Handle(o:Observable, e:ObservableEvent) extends Msg
  case class Event(o:Observable, f:Function1[ObservableEvent,Unit]) extends Msg
}

trait Observable {
  val observers = actor {
    loop {
      react {
	case Add(o) => {
	  Console.println("listenTo " + this)
	  listeners = o :: listeners
	}
	case Remove(o) => listeners = listeners filter { l => l == o }
	case Fire(e) => { listeners foreach { l => l.handle(this,e) } }
      }
    }
  }
  var listeners = List[Observer]()
  observers.start()
  
  def addObserver(o:Observer) = observers ! Add(o)
  def removeObserver(o:Observer) = observers ! Remove(o)
  def fire(e:ObservableEvent) = { observers ! Fire(e) }

  abstract class Msg
  case class Add(o : Observer) extends Msg
  case class Remove(o : Observer) extends Msg
  case class Fire(e: ObservableEvent) extends Msg
}

  
