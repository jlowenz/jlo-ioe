package jlo.ioe.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 11, 2007<br>
 * Time: 5:18:12 PM<br>
 */
public class Util {
	public static <T> T argmax(F.lambda2<T,T,T> f, List<T> args) {
		if (args.size() == 0) return null;
		if (args.size() == 1) return args.get(0);
		else {
			T a = args.get(0);
			T b = args.get(1);
			return argmax(f, cons(f.call(a,b), args.subList(2,args.size())));
		}
	}

	public static <T> List<T> cons(T a, List<T> b) {
		List<T> l = new LinkedList<T>();
		l.add(a);
		if (b != null) l.addAll(b);
		return l;
	}

	public static <T> LinkedList<T> toList(T[] a) {
		LinkedList<T> l = new LinkedList<T>();
		for (T e : a) l.add(e);
		return l;
	}
}
