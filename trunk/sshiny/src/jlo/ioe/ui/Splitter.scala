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

class Split[T](var obj:Option[T], var aspect:Aspect.Aspect, comp : (T)=>Component) {
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
    first = Some(new Split(Some(a),Aspect.Vertical,comp))
    second = Some(new Split(Some(b),Aspect.Vertical,comp))
    kind = SplitType.Vertical()
  }
  def horizontalDivider(a:T,b:T) = {
    obj = None
    first = Some(new Split(Some(a),Aspect.Horizontal,comp))
    second = Some(new Split(Some(b),Aspect.Horizontal,comp))
    kind = SplitType.Horizontal()
  }

  override def toString = "Split(" + obj + "," + aspect + "," + kind + "(" + first + ")(" + second + "))"
}

class Splitter extends JXMultiSplitPane with Component {
  import scala.collection.mutable.HashMap
  import scala.compat.StringBuilder

  def resplit[T](root:Split[T]) = {
    removeAll()
//     String layoutDef =
//       "(COLUMN (ROW weight=1.0 left (COLUMN middle.top middle middle.bottom) right) bottom)";
    val compMap = new HashMap[String,Split[T]]
    val layoutDef = buildLayout(root,compMap,0,new StringBuilder)
    Console.println("Layout: " + layoutDef)
    val modelRoot = MultiSplitLayout.parseModel(layoutDef);

    getMultiSplitLayout().setModel(modelRoot);
    
    for (val p : Tuple2[String,Split[T]] <- compMap) {
      add(p._1, p._2.component)
    }
    validate()
    repaint()
  }

  private def buildLayout[T](split : Split[T], map : HashMap[String,Split[T]], i : int, sb : StringBuilder) : String = {
    sb.append("(")
    split.kind match {
      case SplitType.Horizontal() => { 
	sb.append("COLUMN ")
	buildLayout(split.first.get,map,i+1,sb)
	buildLayout(split.second.get,map,i+100,sb) 
      }
      case SplitType.Vertical() => { 
	sb.append("ROW ")
	buildLayout(split.first.get,map,i+1,sb)
	buildLayout(split.second.get,map,i+100,sb) 
      }
      case _ => {
	val name = "comp" + i
	map.update(name, split)
	sb.append("LEAF weight=0.5 name=").append(name).append(" ")
      }
    }
    sb.append(")")
    sb.toString
  }
}
