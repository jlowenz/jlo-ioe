package jlo.ioe.command

class Also extends VocabularyTerm {
  type T = Also
  val name = "also"
  
  override def part = VerbPart(name,this)
  override def synonyms = List(makeSynonym("split"))
  override def suggestions = Vocabulary.allTerms
  override def execute(next:Option[VocabularyTerm]) = None
}
