package jlo.ioe.command

class New extends VocabularyTerm {
  val name = "new"
  
  override def part = VerbPart(name,this)

  override def accepts = {
    List[this.type]()
  }

  override def execute = {}
}
