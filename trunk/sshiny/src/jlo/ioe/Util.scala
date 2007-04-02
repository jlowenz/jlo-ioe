package jlo.ioe

trait STBoolean {
  def ifTrue(f:()=>Any) : Any
  def ifFalse(f:()=>Any) : Any
}

object STTrue extends STBoolean {
  override def ifTrue(f:()=>Any) = f()
  override def ifFalse(f:()=>Any) = {}
}

object STFalse extends STBoolean {
  override def ifTrue(f:()=>Any) = {}
  override def ifFalse(f:()=>Any) = f()
}

// kinda stupid, cause it's not built-in
object Util {
  implicit def stBool(b:boolean) : STBoolean = if (b) STTrue else STFalse

  def argmax[A](f:(A,A)=>A,o:List[A]) : A = {
    if (o.length == 1) o(0)
    else argmax(f,f(o(0),o(1)) :: o.tail.tail)
  }
}
