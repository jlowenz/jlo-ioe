package jlo.ioe.util;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 3:36:20 PM<br>
 */
public class StringUtil {
	public static CustomString str(String s) {
		return null;
	}

	public static class CustomString {
		public String str;
		public CustomString(String s) {
			str = s;
		}

		public <f extends F.lambda<Object>> Object match(Tuple.Two<String, f> ... cases) {
			for (Tuple.Two<String,f> m : cases) {
				if (str.equals(m.first())) {
					return m.second().call();
				}
			}
			return null;
		}

	}
}
