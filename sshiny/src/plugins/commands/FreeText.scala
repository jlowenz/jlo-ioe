package jlo.ioe.command

// not added to the vocabulary

class FreeText(term:String) extends VocabularyTerm {
  type T = FreeText
  val name = term
  
  override def part = FreeTextPart(term,this)  
  override def synonyms = List[VocabularyTerm]()

  override def suggestions = List(new FreeText("?"))

  override def execute(p:Option[VocabularyTerm]) = None

  def getTerm = term
}
