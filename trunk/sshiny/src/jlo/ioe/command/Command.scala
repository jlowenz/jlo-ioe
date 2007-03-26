package jlo.ioe.command

abstract class CommandPart(text:String)
case class VerbPart(text:String,v:VocabularyTerm) extends CommandPart(text)
case class UserDataPart(text:String,v:VocabularyTerm) extends CommandPart(text)
case class DataTypePart(text:String,v:VocabularyTerm) extends CommandPart(text)
case class ModifierPart(text:String,v:VocabularyTerm) extends CommandPart(text)

class Command {
  var parts = List[CommandPart]()  
}
