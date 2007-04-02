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
//     comp(a).preferredWidth(comp(a).preferredWidth/2)
//     comp(b).preferredWidth(comp(b).preferredWidth/2)
//     comp(a).setWidth(comp(a).preferredWidth)
//     comp(b).setWidth(comp(b).preferredWidth)
    first = Some(new Split(Some(a),Aspect.Vertical,0.5,comp))
    second = Some(new Split(Some(b),Aspect.Vertical,0.5,comp))
    kind = SplitType.Vertical()
  }
  def horizontalDivider(a:T,b:T) = {
    obj = None
//     comp(a).preferredHeight(comp(a).preferredHeight/2)
//     comp(b).preferredHeight(comp(b).preferredHeight/2)
//     comp(a).setHeight(comp(a).preferredHeight)
//     comp(b).setHeight(comp(b).preferredHeight)
    first = Some(new Split(Some(a),Aspect.Horizontal,0.5,comp))
    second = Some(new Split(Some(b),Aspect.Horizontal,0.5,comp))
    kind = SplitType.Horizontal()
  }

  override def toString = "Split(" + obj + "," + aspect + "," + kind + "(" + first + ")(" + second + "))"
}

// TODO: this is broken. the splitpane seems not to handle more than 8 components!
class Splitter extends JXMultiSplitPane with Component {
  import scala.collection.mutable.HashMap
  import scala.compat.StringBuilder

  def resplit[T](root:Split[T]) = {
    val compMap = new HashMap[String,Split[T]]
    val layoutDef = buildLayout(root,compMap)
    Console.println("Layout: " + layoutDef)
    //val modelRoot = MultiSplitLayout.parseModel(layoutDef);
    val modelRoot = layoutDef
    getMultiSplitLayout().setModel(modelRoot);

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      def run = {
	removeAll()	
	for (val p : Tuple2[String,Split[T]] <- compMap) {
	  Console.println("size: " + p._1 + ", " + p._2.component.getSize())
	  Console.println("pref: " + p._1 + ", " + p._2.component.getPreferredSize())
	  add(p._1, p._2.component)
	}
	revalidate()
	repaint()
      }
    });
  }

  private def buildLayout[T](split : Split[T], map : HashMap[String,Split[T]]) : MultiSplitLayout.Node = {
    split.kind match {
      case SplitType.Horizontal() => {
	val n = new MultiSplitLayout.Split(List(buildLayout(split.first.get,map),
						new MultiSplitLayout.Divider,
						buildLayout(split.second.get,map)).toArray)
	n.setRowLayout(false)
	n.setWeight(0.5)
	n
      }
      case SplitType.Vertical() => {
	val n = new MultiSplitLayout.Split(List(buildLayout(split.first.get,map),
						new MultiSplitLayout.Divider,
						buildLayout(split.second.get,map)).toArray)
	n.setRowLayout(true)
	n.setWeight(0.5)
	n
      }
      case _ => {
	val name = "comp" + split().get
	map.update(name, split)
	val leaf = new MultiSplitLayout.Leaf(name)	
	leaf.setWeight(0.5)
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
