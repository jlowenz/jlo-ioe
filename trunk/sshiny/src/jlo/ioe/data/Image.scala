package jlo.ioe.data

import jlo.ioe.command._

package command {
  class Image extends VocabularyTerm {
    type T = this.type
    val name = "image"
    
    override def part = DataTypePart("image",this)
    override def synonyms = List(makeSynonym("picture"),makeSynonym("photo"),makeSynonym("graphic"))
    override def suggestions = List[VocabularyTerm]()
    override def execute(next:Option[VocabularyTerm]) = Some(()=>{new jlo.ioe.data.Image})
  }
}

object Image extends DOStorage[Email] {
  val db = createDB("Image")
  def defaultView(i:Image) = new ImageView(i)
}

class Image extends DataObject {
  val image = Graphic(this, "image", Graphics.BLANK)  

  var defView : Option[View] = None
  def storage = Image
  def kind = "Image"
  def defaultView = defView match {
    case Some(v) => v
    case None => { defView = Some(Image.defaultView(this)); defView.get }
  }
}

class ImageView(image:Image) extends jlo.ioe.View {
  
  add(image.image())
  preferredSize(500,500)

}
