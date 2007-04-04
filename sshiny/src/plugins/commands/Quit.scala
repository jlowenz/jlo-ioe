package jlo.ioe.command

class Quit extends VocabularyTerm {
  type T = Quit
  val name = "quit"
  
  override def part = VerbPart(name,this)
  override def synonyms = List(makeSynonym("exit"))
  override def suggestions = List[VocabularyTerm]()
  override def execute(next:Option[VocabularyTerm]) = {
    Console.println("Exiting the system")
    System.exit(0);
    None
  }
}
