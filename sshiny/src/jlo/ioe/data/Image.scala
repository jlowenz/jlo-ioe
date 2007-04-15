package jlo.ioe.data

import jlo.ioe.ui.behavior.AncestorTracker
import jlo.ioe.ui.behavior.Ancestor
import jlo.ioe.ui.Observer
import jlo.ioe.ui.Component
import jlo.ioe.command._
import java.awt.image.BufferedImage
import edu.umd.cs.piccolo.PCanvas
import edu.umd.cs.piccolo.nodes.PImage
import edu.umd.cs.piccolo.nodes.PText
import edu.umd.cs.piccolo.util.PPaintContext
import edu.umd.cs.piccolox.util.PFixedWidthStroke
import java.awt.Color
import java.awt.BorderLayout
import java.awt.geom._
import javax.swing.border.LineBorder
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.net.URL

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

object ImageStorage extends DOStorage[Image] {
  val db = createDB("Image", classOf[Image])
  def defaultView(i:Image) = new ImageView(i)
}

class Image extends DataObject {
  val image = new field.Image(this, "image", new BufferedImage(512,512,BufferedImage.TYPE_INT_ARGB))

  var defView : Option[View] = None
  def storage = ImageStorage
  def kind = "Image"
  def defaultView = defView match {
    case Some(v) => v
    case None => { defView = Some(storage.defaultView(this)); defView.get }
  }
}

class ImageView(image:Image) extends jlo.ioe.View {
  val icomp = new ImageComponent
  
  onSwingThread(() => 
    {
      setLayout(new BorderLayout())
      setBorder(new LineBorder(Color.gray,1))
      add(icomp)
      preferredSize(1000,1000)
      
      validate()
      icomp.requestFocus()
      icomp.setImage(image.image())
    })
}

class ImageComponent extends PCanvas with Component with Observer with AncestorTracker {
  var theImage : java.awt.Image = null
  var imageNode : PImage = null
  
  setFocusable(true)
  listenTo(this) event {
    case Ancestor("added",e) => requestFocus
    case _ => {}
  }
  addComponentListener(new ComponentAdapter() {
    override def componentResized(e:ComponentEvent) : Unit = {
      Console.println("canvas resized: " + getRoot().getGlobalFullBounds())
      Console.println(getLayer().getGlobalFullBounds())
      Console.println(getCamera().getGlobalFullBounds())
      getCamera.animateViewToCenterBounds(imageNode.getGlobalFullBounds, false, 0)
    }
  })
			 
  def setImage(im:java.awt.Image) = {
    theImage = im
    onSwingThread(() =>
      {
	Console.println("shown")
	//imageNode = new PImage(new URL("http://ec.imaginaryday.com/jlo/files/chameleon_logo.png")) {
	imageNode = new PImage(theImage) {
	  override def paintAfterChildren(ctx:PPaintContext) : Unit = {
	    super.paintAfterChildren(ctx)
	    val g = ctx.getGraphics
	    g.setStroke(new PFixedWidthStroke(1f))
	    g.setColor(Color.black)
	    g.draw(getFullBounds())
	  }
	}
	getLayer().addChild(imageNode)
	getCamera.animateViewToCenterBounds(imageNode.getGlobalFullBounds,false,0)
      })
  }
}
