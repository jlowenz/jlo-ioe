package jlo.ioe.ui;

import scala.collection.jcl._
import scala.collection.immutable._
import scala.actors.Actor._
import java.io.{ObjectInputStream,ObjectOutputStream}

abstract class ObservableEvent

trait Observer {
  var handler = makeActor
  private def makeActor = actor {
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
	case RemoveEvents(o) => {
	  Console.println("remove events")
	  handlers = handlers - o
	}
      }
    } 
  }
 
//   @throws(classOf[java.io.IOException])
//   @throws(classOf[java.lang.ClassNotFoundException])
//   def readObject(in:ObjectInputStream) :Unit = {
//     handler = makeActor
//     handlers = Map[Observable,List[Function1[ObservableEvent,Unit]]]()
//   }
//   @throws(classOf[java.io.IOException])
//   def writeObject(out:ObjectOutputStream) :Unit = {}

  class ListenTarget(o:Observable) {
    def event(f:Function1[ObservableEvent,Unit]) = handler ! Event(o,f)
  }

  var handlers = Map[Observable,List[Function1[ObservableEvent,Unit]]]()
//  handler.start()

  def listenTo(o:Observable)  = { o.addObserver(this); new ListenTarget(o) }
  // maybe don't want to remove events? not sure
  def ignore(o:Observable) : this.type = { o.removeObserver(this); handler ! RemoveEvents(o); this }
  def handle(o:Observable, e:ObservableEvent) = handler ! Handle(o,e)

  
  abstract class Msg
  case class Handle(o:Observable, e:ObservableEvent) extends Msg
  case class Event(o:Observable, f:Function1[ObservableEvent,Unit]) extends Msg
  case class RemoveEvents(o:Observable) extends Msg
}

trait Observable {
  var observers = makeActor

  private def makeActor = actor {
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
//  observers.start()

//   @throws(classOf[java.io.IOException])
//   @throws(classOf[java.lang.ClassNotFoundException])
//   def readObject(in:ObjectInputStream) : Unit = {
//     observers = makeActor
//     listeners = List[Observer]()
//   }
//   @throws(classOf[java.io.IOException])
//   def writeObject(out:ObjectOutputStream) : Unit = {
//     Console.println("called?? **********************")
//   }

  
  def addObserver(o:Observer) = observers ! Add(o)
  def removeObserver(o:Observer) = observers ! Remove(o)
  def fire(e:ObservableEvent) = { observers ! Fire(e) }

  abstract class Msg
  case class Add(o : Observer) extends Msg
  case class Remove(o : Observer) extends Msg
  case class Fire(e: ObservableEvent) extends Msg
}

  
