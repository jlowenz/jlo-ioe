package jlo.ioe.command

abstract class CommandPart(text:String)
case class VerbPart(text:String,v:VocabularyTerm) extends CommandPart(text)
case class UserDataPart(text:String,v:VocabularyTerm) extends CommandPart(text)
case class DataTypePart(text:String,c:Class,v:VocabularyTerm) extends CommandPart(text)
case class ModifierPart(text:String,v:VocabularyTerm) extends CommandPart(text)
case class FreeTextPart(text:String,v:VocabularyTerm) extends CommandPart(text)

class Command {
  var fragment = ""
  var text = ""
  var verb : Option[VocabularyTerm] = None
  var noun : Option[VocabularyTerm] = None
  var freeText = List[VocabularyTerm]()

  def updateFragment(f:String) = fragment = f
  def completed(f:String,term:Option[VocabularyTerm]) = term match {
    case Some(t) => t.part match {
      case VerbPart(t1,v) => verb = Some(v) // what if there's already a verb?
      case DataTypePart(t1,c,v) => noun = Some(v)
      case FreeTextPart(t1,v) => freeText ::: List(v)
      case _ => {} // XXX handle the others
    }
    case None => freeText ::: List(new FreeText(f))
  }
  def termCompleted(matches:List[VocabularyTerm]) = {
    // do something with the fragment?
    Console.println("termCompleted: " + matches)
    if (!matches.isEmpty) {
      completed("", Some(matches.head))
    }
  }

  def suggestions(frag:String) = {
    Vocabulary.possibleTerms(frag)
  }

  def execute = {
    Console.println("executing command: " + this.toString)
    verb.get.execute(noun)
  }

  override def toString = {
    val s = verb.get.name + noun.get.name
    if (!freeText.isEmpty) 
      s + freeText.map({e=>e.name}).reduceLeft({(l:String,r:String) => l + " " + r}) 
    else s
  }
}
