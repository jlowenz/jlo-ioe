package jlo.ioe.data

import jlo.ioe.command._

package command {
  import jlo.ioe.command.Command
  class Note extends VocabularyTerm {
    type T = this.type
    val name = "note"
    
    def part = DataTypePart("note",classOf[Note],this)
    def synonyms = List[VocabularyTerm]()
    def suggestions = List[VocabularyTerm]()
    // by convention, execute on a DataType command returns the data type singleton
    def execute(next:Option[VocabularyTerm]) = Some(()=>{new jlo.ioe.data.Note})
  }
}

// define note-related functions and loading routines
object Note {
  def defaultView(n:Note) = new NoteView(n)
}

// define the note data object
@serializable
@SerialVersionUID(1000)
class Note extends DataObject {
  val note = Text("note","")

  def kind = "Note"
  def defaultView = Note.defaultView(this)

  override def toString = "Note:" + note() + "..."
}

import jlo.ioe.ui._
class NoteView(note:Note) extends jlo.ioe.View {
  import java.awt.Color
  import javax.swing.border.EmptyBorder
  val textPane = new TextPane {
    setBackground(new Color(240,240,220))
  }

  textPane.bindTo(note.note).trackingText;

  setBorder(new EmptyBorder(10,10,10,10))
  add(textPane)

}
