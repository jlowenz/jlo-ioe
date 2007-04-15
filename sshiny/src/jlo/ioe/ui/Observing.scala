package jlo.ioe.ui;

import scala.collection.immutable._
import scala.actors.Actor._
import java.io.{ObjectInputStream,ObjectOutputStream,ObjectInput,ObjectOutput}
import java.util.concurrent._
import jlo.ioe.data.Storable;

abstract class ObservableEvent

@serializable
@SerialVersionUID(1000)
class EventHandler(f:Function1[ObservableEvent,Unit]) extends Function1[ObservableEvent,Unit] with java.io.Serializable {
  def apply(e:ObservableEvent) = f(e)
}

object Threads {
  //var pool = Executors.newCachedThreadPool
  var pool = Executors.newFixedThreadPool(1)
}

trait Observer {
  def handlers : Map[Observable,List[EventHandler]]
  def handlers_=(h:Map[Observable,List[EventHandler]]) : Unit

  class ListenTarget(o:Observable) {
    def event(f:Function1[ObservableEvent,Unit]) = {
      Console.println("event " + Observer.this)
      synchronized {
	var l = handlers.get(o).getOrElse(List[EventHandler]())
	l = new EventHandler(f) :: l
	handlers = handlers.update(o, l)
      }
      handlers.foreach { e => Console.println("\t" + e._1 + " --> " + e._2) }
    }
  }

  private def spawn(f:EventHandler, e:ObservableEvent) = Threads.pool.execute(new Runnable { def run = f(e) })

  def listenTo(o:Observable)  = { o.addObserver(this); new ListenTarget(o) }
  // maybe don't want to remove events? not sure
  def ignore(o:Observable) : this.type = { 
    o.removeObserver(this)
    Console.println("remove events")
    synchronized {
      handlers = handlers - o
    }
    this 
  }
  def handle(o:Observable, e:ObservableEvent) = { 
    try { handlers.get(o).getOrElse(List()).foreach { f => spawn(f,e) } }
    catch { case e:Throwable => e.printStackTrace() }
  }

  def readHandlers(in:ObjectInput) : Unit = {
    val count = in.readInt
    for (val i <- 1.to(count)) {
      handlers = handlers.update(in.readObject.asInstanceOf[Observable], in.readObject.asInstanceOf[List[EventHandler]])
    }
 }
  
  def writeHandlers(out:ObjectOutput) : Unit = {
    val h = handlers.filter { e => e._1.isInstanceOf[Storable] }
    out.writeInt(h.size)
    h.foreach { e => {
      out.writeObject(e._1)
      out.writeObject(e._2)
    }}
  }
}

@serializable
trait Observable {
  // BEGIN: state
  // todo: observers may need to be wrapped in lazy references
  def listeners : Set[Observer]
  def listeners_=(s:Set[Observer]) : Unit
  // END: state

  // todo: fix this
  def addObserver(o:Observer) =  {
    Console.println("listenTo " + this)
    synchronized {
      listeners = listeners.+(o)
    }
  }
  def removeObserver(o:Observer) =  synchronized { listeners = listeners filter { l => l == o } }
  def fire(e:ObservableEvent) = { 
    for (val l <- listeners.elements) {
      l.handle(this,e) 
    }
  }

  def readObservers(in:ObjectInput) : Unit = {
    val count = in.readInt
    for (val i <- 1.to(count)) {
      listeners = listeners + in.readObject.asInstanceOf[Observer]
    }
  }

  def writeObservers(out:ObjectOutput) : Unit = {
    val l = listeners.filter { e => e.isInstanceOf[Storable] }
    out.writeInt(l.size)
    l.foreach { e => Console.println("wO: " + e); out.writeObject(e) }
  }
}

