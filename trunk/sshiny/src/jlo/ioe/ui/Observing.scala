package jlo.ioe.ui;

import scala.collection.immutable._

abstract class ObservableEvent() extends Object with Ordered[ObservableEvent] {
  var sender : Observable
  def this(o : Observable) = { this(); sender = o }
  def compare(that : ObservableEvent) : int = toString().compare(that.toString())
}

trait Observer {
  var observed = List[Observable]() 
  def observe(o : Observable) = { o.addObserver(this); observed = o :: observed }
  def onEvent(e : ObservableEvent, o : Observable, f : (ObservableEvent) => Unit) = o.addHandler(e,f)
}

trait Observable {
  var observers = List[Observer]()
  var events : Map[ObservableEvent,List[(ObservableEvent)=>Unit]] = new TreeMap[ObservableEvent,List[(ObservableEvent)=>Unit]]()
  
  def addObserver(o : Observer) = { observers = o :: observers; this }
  def addHandler(e : ObservableEvent, f : (ObservableEvent) => Unit) = events.get(e) match {
    case Some(l) => events = events.update(e, f :: l) 
  }        
}

  
