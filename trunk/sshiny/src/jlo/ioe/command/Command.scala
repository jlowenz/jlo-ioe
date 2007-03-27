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
  var command : Option[VocabularyTerm] = None

  def updateFragment(f:String) = fragment = f
  def completed(f:String,term:Option[VocabularyTerm]) = term match {
    case Some(t) => command match { 
      case Some(c) => reorder(c,t)
      case None => command = Some(t)
    }
    case None => append(command.get, new FreeText(f))
  }
  def termCompleted(matches:List[VocabularyTerm]) = {
    // do something with the fragment?
  }

  def reorder(cmd:VocabularyTerm,next:VocabularyTerm) = {
    Tuple(cmd.part,next.part) match {
      case Tuple2(VerbPart(t,v),DataTypePart(t1,c,v1)) => v.attach(v1)
      case _ => append(cmd,next)
    }
  }

  private def append(t:VocabularyTerm,v:VocabularyTerm) : Unit = {
    if (t.next.isDefined) append(t.next.get,v)
    else t.attach(v)
  }

  override def toString = command.getOrElse(fragment).toString
}
