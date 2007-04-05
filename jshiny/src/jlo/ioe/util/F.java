package jlo.ioe.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Some functions (functional) utilities like anonymous functions and some
 * collection manipulation routines.
 *
 * <p><table> <tr><td>User:</td><td>jlowens</td></tr> <tr><td>Date:</td><td>Apr 14,
 * 2006</td></tr> <tr><td>Time:</td><td>10:04:52 PM</td></tr> </table>
 */
@SuppressWarnings("unchecked")
public class F {

	public interface Callable<r>
	{
		r call(Object ... args);
	}

	public static abstract class lambda<r> implements Callable<r>
	{
		protected static final String ARGUMENT_SIZE_MISMATCH = "Argument size mismatch: you either have too many or too little arguments";
		protected static final String ARGUMENT_APPLICATION_OVERFLOW = "Can't apply more arguments than parameter count!";

		protected List _args = new ArrayList(5);
		protected int getNumArgs() { return 0; }
		public <l extends lambda<r>> l apply(Object[] o) {
			assert o.length > getNumArgs() : ARGUMENT_APPLICATION_OVERFLOW;
			for (Object arg : o) { _args.add(arg); }
			return (l)this;
		}
		public r call(Object ... args) {
			assert args.length == 0 : ARGUMENT_SIZE_MISMATCH;
			return code();
		}
		protected abstract r code();
	}

	public static abstract class lambda1<r,a> extends lambda<r>
	{
		protected abstract r code(a A);
		public r call(Object ... args) {
			assert (args.length + _args.size()) == 1 : ARGUMENT_SIZE_MISMATCH;
			if (_args.size() > 0) { return code((a)_args.get(0)); }
			else { return code((a)args[0]); }
		}
	}
	public static abstract class lambda2<r,a,b> extends lambda<r>
	{
		protected abstract r code(a A, b B);
		public r call(Object ... args) {
			assert (args.length + _args.size()) == 2 : ARGUMENT_SIZE_MISMATCH;
			switch (_args.size()) {
				case 1: return code((a)_args.get(0), (b)args[0]);
				case 2: return code((a)_args.get(0), (b)_args.get(1));
				case 0:
				default: return code((a)args[0], (b)args[1]);
			}
		}
	}
	public static abstract class lambda3<r,a,b,c> extends lambda<r>
	{
		protected abstract r code(a A, b B, c C);
		public r call(Object ... args)
		{
			assert (args.length + _args.size()) == 3 : ARGUMENT_SIZE_MISMATCH;
			switch (_args.size()) {
				case 1: return code((a)_args.get(0), (b)args[0], (c)args[1]);
				case 2: return code((a)_args.get(0), (b)_args.get(1), (c)args[0]);
				case 3: return code((a)_args.get(0), (b)_args.get(1), (c)_args.get(2));
				case 0:
				default: return code((a)args[0], (b)args[1], (c)args[2]);
			}
		}
	}


	/**
	 * Add to collection and return added value.
	 * @param c
	 * @param elt
	 * @return the first element value added to the collection
	 */
	public static <r> r addr(Collection<r> c, r ... elt) {
		for (r e : elt) c.add(e);
		return elt[0];
	}

	public static <k,v> v putr(Map<k,v> m, k key, v val) {
		m.put(key,val);
		return val;
	}

	public static <t> void addarray(Collection<t> c, t[] arr)
	{
		for (t el : arr) c.add(el);
	}

	public static <r,c extends Collection<r>> c map(c c, lambda1<r,r> l) {
		c newc = null;
		try {
			newc = (c)c.getClass().newInstance();
			for (r el : c) { newc.add(l.call(el)); }
			return newc;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return newc;
	}

	public static <a, c extends Collection<a>> void foreach(c c, lambda1<?,a> lambda1) {
		for (a el : c) { lambda1.call(el); }
	}
}

