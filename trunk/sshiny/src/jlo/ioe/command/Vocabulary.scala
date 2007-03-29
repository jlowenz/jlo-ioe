package jlo.ioe.command

import scala.actors.SuspendActorException
import scala.actors.Actor._
import scala.collection.jcl._

trait VocabularyTerm {
  type T <: VocabularyTerm
  val name : String
  var next : Option[VocabularyTerm] = None
  
//  def this(name:String) = this()

  def part : CommandPart
  def synonyms : List[String]
  def suggestions : List[VocabularyTerm]
  def attach(v : VocabularyTerm) = next = Some(v)
  def execute : Option[Any]
  def copy : T = getClass().newInstance().asInstanceOf[T]

  override def toString = name
}

object Vocabulary {
  import java.io.File
  val allTerms = new Trie[VocabularyTerm]()
  val dataTypeTrie = new Trie[VocabularyTerm]()
  val verbTrie = new Trie[VocabularyTerm]()   
  // load the commands
  val commands = List("New")
  val dataTypes = List()
  var vocab = actor {
    loop {
      react {
	case Tuple2('matchTerm,p:String) => reply(allTerms.retrieveMatches(p))
	case Tuple2('possibleTerms,partial:String) => {Console.println("here"); reply(_possibleTerms(partial))}
	case Tuple2('addTerm,term:VocabularyTerm) => reply(_addTerm(term))
	case 'allDataTypes => reply(dataTypeTrie.getAll)
	case 'allVerbs => reply(verbTrie.getAll)
	case Tuple2('possibleDataType,partial:String) => reply(_possibleDataType(partial))
	case Tuple2('possibleVerb,partial:String) => reply(_possibleVerb(partial))
      }
    }
  }
  commands.foreach { 
    c => {
      val name = "jlo.ioe.command."+c
      addTerm(Class.forName(name).newInstance.asInstanceOf[VocabularyTerm])
    }
  }

  def load() = Console.println("Loading vocabulary")
  
  def matchingTerms(p:String) : List[VocabularyTerm] = {
    vocab !? Tuple('matchTerm,p) match {
      case l:List[VocabularyTerm] => l
      case _ => List[VocabularyTerm]()
    }
  }

  def possibleTerms(p:String) : List[VocabularyTerm] = {
    Console.println("before send");
    vocab !? Tuple('possibleTerms,p) match {
      case l:List[VocabularyTerm] => {Console.println("ack"); l }
      case _ => List[VocabularyTerm]()
    }
  }
  private def _possibleTerms(p:String) : List[VocabularyTerm] = {
    Console.println("_possibleTerms")
    val l = allTerms.retrieve(p)
    Console.println("_after")
    if (l.length > 0) 
      (List.unzip(l))._2 
    else 
      List[VocabularyTerm]() 
  }
  
  def allDataTypes = vocab !? 'allDataTypes match {
    case l:List[VocabularyTerm] => l
    case _ => List[VocabularyTerm]()
  }
  def allVerbs = vocab !? 'allVerbs match {
    case l:List[VocabularyTerm] => l
    case _ => List[VocabularyTerm]()
  }

  def addTerm(t:VocabularyTerm) = vocab ! Tuple('addTerm,t)  
  private def _addTerm(t : VocabularyTerm) = {
    allTerms.insert(t.name,t)
    t.synonyms.foreach { s => allTerms.insert(s,t) }
    t.part match {
      case VerbPart(n,v) => v.synonyms.foreach { s => verbTrie.insert(s,v) }
      case DataTypePart(n,c,v) => v.synonyms.foreach { s => dataTypeTrie.insert(s,v) }
      case _ => { Console.println("*** unhandled part: " + t) }
    }
  }

  def possibleDataType(p:String) = vocab !? Tuple('possibleDataType,p) match {
    case l:List[VocabularyTerm] => l
    case _ => List[VocabularyTerm]()
  }
  private def _possibleDataType(p:String) = dataTypeTrie.retrieve(p)

  def possibleVerb(p:String) = vocab !? Tuple('possibleVerb,p) match {
    case l:List[VocabularyTerm] => l
    case _ => List[VocabularyTerm]()
  }
  private def _possibleVerb(p : String) = verbTrie.retrieve(p)
}

trait TrieUtil {
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

class Trie[A] extends TrieUtil {
  private val level = Array.make[Option[SubTrie[A]]](26,None)
  private var all = List[A]()
  
  def getAll = all

  def insert(key:String,obj:A) : Unit = insert(key,key,obj)

  private def insert(p:String,a:String,obj:A) : Unit = {
    val c = charToIndex(p.charAt(0))
    level(c) match {
      case Some(t) => t insert(a,a,obj)
      case None => {
	level(c) = Some(new SubTrie[A])
	level(c).get.insert(a,a,obj)
      }
    }
  }

  def retrieve(p:String) : List[Tuple2[String,A]] = {
    Console.println("Trie.retrieve")
    if (p.length < 1) return List[Tuple2[String,A]]()
    try {
      level(charToIndex(p.charAt(0))) match {
	case Some(t:SubTrie[A]) => t._retrieve(p)
	case None => {Console.println(" nothing"); List[Tuple2[String,A]]()}
      }
    } catch {
      case e:Throwable => { e.printStackTrace; throw e }
    }
  }

  def retrieveMatches(c:String) : List[Tuple2[String,A]] = {
    if (c.length < 1) return List[Tuple2[String,A]]()
    else {
      level(charToIndex(c.charAt(0))) match {
	case Some(t) => t.retrieveMatches(c)
	case None => List[Tuple2[String,A]]()
      }
    }
  }
}

class SubTrie[A] extends TrieUtil {
  private val level = Array.make[Option[SubTrie[A]]](26,None)
  private var completes = List[Tuple2[String,A]]()
  private var partials = List[Tuple2[String,A]]()
  
  def retrieveMatches(complete:String) : List[Tuple2[String,A]] = {
    if (complete.length == 1) completes
    else { 
      val c = complete.substring(1) 
      level(charToIndex(c.charAt(0))) match {
	case Some(t) => t.retrieveMatches(c)
	case None => List[Tuple2[String,A]]()
      }
    }
  }

  def insert(partial:String, all:String, obj:A) : Unit = {
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
  
  def _retrieve(partial:String) : List[Tuple2[String,A]] = {
    Console.println(partial)
    if (partial.length == 1) (partials ++ completes).toList
    else {
      val p = partial.substring(1)
      level(charToIndex(p.charAt(0))) match {
	case Some(t) => t._retrieve(p)
	case None => List[Tuple2[String,A]]()
      }
    }
  }
}
  
