package jlo.ioe.ui

import org.jdesktop.swingx.JXMultiSplitPane
import org.jdesktop.swingx.MultiSplitLayout

object SplitType {
  abstract class SplitType
  case class NoSplit extends SplitType
  case class Horizontal extends SplitType
  case class Vertical extends SplitType
}

object Aspect {
  abstract class Aspect
  case class Vertical extends Aspect
  case class Horizontal extends Aspect
} 

class Split[T](var obj:Option[T], var aspect:Aspect.Aspect, var weight: double, comp : (T)=>Component) {
  var kind : SplitType.SplitType = SplitType.NoSplit
  var first : Option[Split[T]] = None
  var second : Option[Split[T]] = None

  def apply() = obj
  def update(o:T) = obj = Some(o)
  def component = comp(obj.get)
  def area = obj match {
    case Some(o) => comp(o).getWidth() * comp(o).getHeight()
    case None => 0
  }

  def verticalDivider(a:T,b:T) = {
    obj = None
    first = Some(new Split(Some(a),Aspect.Vertical,0.5,comp))
    second = Some(new Split(Some(b),Aspect.Vertical,0.5,comp))
    kind = SplitType.Vertical()
  }
  def horizontalDivider(a:T,b:T) = {
    obj = None
    first = Some(new Split(Some(a),Aspect.Horizontal,0.5,comp))
    second = Some(new Split(Some(b),Aspect.Horizontal,0.5,comp))
    kind = SplitType.Horizontal()
  }

  override def toString = "Split(" + obj + "," + aspect + "," + kind + "(" + first + ")(" + second + "))"
}

// TODO: this is broken. the splitpane seems not to handle more than 8 components!
class Splitter extends jlo.ioe.ui.MSP with Component {
  import scala.collection.mutable.HashMap
  import scala.compat.StringBuilder
  import javax.swing.JOptionPane


  def resplit[T](root:Split[T]) = {
    getMSPLayout().setFloatingDividers(true)    
    val compMap = new HashMap[String,Split[T]]
    val layoutDef = buildLayout(root,compMap)
    Console.println("Layout: " + layoutDef)
    //val modelRoot = MSPLayout.parseModel(layoutDef);
    val modelRoot = layoutDef
    getMSPLayout().setModel(modelRoot);

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      def run = {
	removeAll()	
	for (val p : Tuple2[String,Split[T]] <- compMap) {
	  Console.println("size: " + p._1 + ", " + p._2.component.getSize())
	  Console.println("pref: " + p._1 + ", " + p._2.component.getPreferredSize())
	  add(p._1, p._2.component)
	  revalidate()
	}
	repaint()
      }
    });
  }

  private def buildLayout[T](split : Split[T], map : HashMap[String,Split[T]]) : MSPLayout.Node = {
    split.kind match {
      case SplitType.Horizontal() => {
	val n = new MSPLayout.Split(List(buildLayout(split.first.get,map),
						new MSPLayout.Divider,
						buildLayout(split.second.get,map)).toArray)
	n.setRowLayout(false)
	n
      }
      case SplitType.Vertical() => {
	val n = new MSPLayout.Split(List(buildLayout(split.first.get,map),
						new MSPLayout.Divider,
						buildLayout(split.second.get,map)).toArray)
	n.setRowLayout(true)
	n
      }
      case _ => {
	val name = "comp" + split().get
	map.update(name, split)
	val leaf = new MSPLayout.Leaf(name)	
	leaf
      }
    }
  }

  private def buildLayout[T](split : Split[T], map : HashMap[String,Split[T]], sb : StringBuilder) : String = {
    sb.append("(")
    split.kind match {
      case SplitType.Horizontal() => { 
	sb.append("COLUMN ")
	sb.append("weight=").append(split.weight).append(" ")
	buildLayout(split.first.get,map,sb)
	buildLayout(split.second.get,map,sb) 
      }
      case SplitType.Vertical() => { 
	sb.append("ROW ")
	sb.append("weight=").append(split.weight).append(" ")
	buildLayout(split.first.get,map,sb)
	buildLayout(split.second.get,map,sb)
      }
      case _ => {
	val name = "comp" + split().get
	map.update(name, split)
	sb.append("LEAF weight=0.0").append(" name=").append(name).append(" ")
      }
    }
    sb.append(")")
    sb.toString
  }
}
