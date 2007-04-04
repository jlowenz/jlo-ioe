package jlo.ioe.data

import jlo.ioe.command._

package command {  
  class Email extends VocabularyTerm {
    type T = this.type
    val name = "email"

    override def part = DataTypePart("email",this)
    def synonyms = List[VocabularyTerm](makeSynonym("mail"))
    def suggestions = List[VocabularyTerm]()
    def execute(next:Option[VocabularyTerm]) = Some(()=>{new jlo.ioe.data.Email})
  }
}

object Email extends DOStorage[Email] {
  val db = createDB("Email")
  def defaultView(e:Email) = new EmailView(e)
}

class Email extends DataObject {
  val to = Text(this, "to", "") 
  val body = Text(this, "body", "")
  var defView : Option[View] = None

  listenTo(to) event {
    case FieldChange(n,v) => {
      Console.println("blah")
      meta(DataObjects.kTitle, "Email to " + to.text)
    }
  }

  def storage = Email
  def kind = "Email"
  def defaultView = defView match {
    case Some(v) => v
    case None => { defView = Some(Email.defaultView(this)); defView.get }
  }
}

class EmailView(email:Email) extends jlo.ioe.View {
  val emailPanel = new EmailPanel
  emailPanel.toField.initialFocus
  add(emailPanel)
  emailPanel.toField.bindTo(email.to).trackingText
  preferredSize(500,500)
}
