package jlo.ioe.command

object New {
  Vocabulary.addTerm(new New)
}

class New extends VocabularyTerm {
  type T = New
  val name = "new"
  
  override def part = VerbPart(name,this)
  
  //override def synonyms = List(makeSynonym("create"), makeSynonym("make"))
  override def synonyms = List("create","make")

  override def suggestions = Vocabulary.allDataTypes

  override def execute = {
    next match {
      case Some(v) => v.part match {
	case DataTypePart(t,c,v1) => ObjectManager.objectCreated(c.newInstance())
	case _ => {}
      }
      case None => {}
    }
    None
  }
}
