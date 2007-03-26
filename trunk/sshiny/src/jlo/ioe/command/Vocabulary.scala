package jlo.ioe.command

import scala.actors.Actor._
import scala.collection.jcl._

trait VocabularyTerm {
  val name : String
  var next : Option[VocabularyTerm] = None
  def part : CommandPart
  def accepts : List[VocabularyTerm]
  def attach(v : VocabularyTerm) = next = Some(v)
  def execute
}

object Vocabulary {
  var dataTypes = List[VocabularyTerm]()
  var dataTypeTrie = new Trie[VocabularyTerm]()
  var verbs = List[VocabularyTerm]()
  var verbTrie = new Trie[VocabularyTerm]()
  
  var vocabulary = actor {
    loop {
      react {
	case 'allDataTypes => reply(dataTypes)
	case 'allVerbs => reply(verbs)
	case Tuple2('possibleDataType,partial:String) => reply(possibleDataType(partial))
	case Tuple2('possibleVerb,partial:String) => reply(possibleVerb(partial))
      }
    }
  }

  def possibleDataType(partial : String) = {
    
  }

  def possibleVerb(partial : String) = {
    
  }
}


class Trie[A] {
  private val level = Array.make[Option[SubTrie[A]]](26,None)
  
  def insert(p:String,a:String,obj:A) : Unit = {
    val c = charToIndex(p.charAt(0))
    level(c) match {
      case Some(t) => t insert(p,a,obj)
      case None => {
	level(c) = Some(new SubTrie[A])
	level(c).get.insert(p,a,obj)
      }
    }
  }

  def retrieve(p:String) : List[Tuple2[String,A]] = {
    level(charToIndex(p.charAt(0))) match {
      case Some(t) => t.retrieve(p)
      case None => List[Tuple2[String,A]]()
    }
  }

  implicit def charToIndex(c : char) : int = {
    c.toLowerCase match {
      case 'a' => 0;  case 'b' => 1;
      case 'c' => 2;  case 'd' => 3;
      case 'e' => 4;  case 'f' => 5;
      case 'g' => 6;  case 'h' => 7;
      case 'i' => 8;  case 'j' => 9;
      case 'k' => 10; case 'l' => 11;
      case 'm' => 12; case 'n' => 13;
      case 'o' => 14; case 'p' => 15;
      case 'q' => 16; case 'r' => 17;
      case 's' => 18; case 't' => 19;
      case 'u' => 20; case 'v' => 21;
      case 'w' => 22; case 'x' => 23;
      case 'y' => 24; case 'z' => 25;
      case _ => -1
    }
  }
}

class SubTrie[A] extends Trie[A] {
  private val level = Array.make[Option[SubTrie[A]]](26,None)
  private var completes = List[Tuple2[String,A]]()
  private var partials = List[Tuple2[String,A]]()
  
  override def insert(partial:String, all:String, obj:A) : Unit = {
    Console.println(partial)
    Console.println("" + this + ".partials " + partials)
    Console.println("" + this + ".completes " + completes)
    if (partial.length == 1) completes = Tuple(all,obj) :: completes
    else { 
      val p = partial.substring(1)
      val c = charToIndex(p.charAt(0))
      partials = Tuple(all,obj) :: partials 
      level(c) match {
	case Some(t) => {
	  t.insert(p,all,obj)
	}
	case None => {
	  level(c) = Some(new SubTrie[A])
	  level(c).get.insert(p,all,obj)
	}
      }
    }
  }
  
  override def retrieve(partial:String) : List[Tuple2[String,A]] = {
    Console.println(partial)
    if (partial.length == 1) partials
    else {
      val p = partial.substring(1)
      level(charToIndex(p.charAt(0))) match {
	case Some(t) => t.retrieve(p)
	case None => List[Tuple2[String,A]]()
      }
    }
  }
}
  
