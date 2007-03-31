package jlo.ioe.command
import jlo.ioe.data.DataObject

object New {

}

class New extends VocabularyTerm {
  type T = New
  val name = "new"
  
  override def part = VerbPart(name,this)
  override def synonyms = List(makeSynonym("create"), makeSynonym("make"))
  override def suggestions = Vocabulary.allDataTypes
  override def execute(next:Option[VocabularyTerm]) = {
    next match {
      case Some(v) => v.part match {
	case DataTypePart(t,c,v1) => {
	  val obj = v1.execute(None).get.asInstanceOf[()=>DataObject]()
	  ObjectManager.objectCreated(obj)
	  Environment.newSheet(obj)
	}
	case _ => {}
      }
      case None => {}
    }
    None
  }
}
