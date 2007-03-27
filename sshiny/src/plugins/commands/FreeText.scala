package jlo.ioe.command

// not added to the vocabulary

class FreeText(term:String) extends VocabularyTerm {
  type T = FreeText
  def this() = this("?")

  val name = term
  
  override def part = FreeTextPart(term,this)
  
  override def synonyms = List[String]()

  override def suggestions = List(new FreeText)

  override def execute = None

  def getTerm = term
}
