package jlo.ioe.util;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * One of Java's "missing features", the Tuple provides 1-5 tuple constructs
 * for use when returning multiple values.
 *
 * <p><table> <tr><td>User:</td><td>jlowens</td></tr> <tr><td>Date:</td><td>Apr 14,
 * 2006</td></tr> <tr><td>Time:</td><td>5:58:12 PM</td></tr> </table>
 */
public abstract class Tuple implements Serializable {

	public abstract <a> a get(int i);
	public abstract int count();
	public abstract boolean match1(One t);
	public abstract boolean match2(Two t);
	public abstract boolean match3(Three t);
	public abstract boolean match4(Four t);
	public abstract boolean match5(Five t);
	public abstract boolean match(Tuple t);
	private static boolean check(Object in, Object match) {
		return match == null || in != null && in.equals(match);
	}

	public static class One<a> extends Tuple
	{
		private a A;
		public One(a obj) { this.A = obj; }
		public a first() { return A; }
		public int count() { return 1; }

		@SuppressWarnings("unchecked")
		public <z> z get(int i)
		{
			try {
				Class c = getClass();
				Field f = c.getField(nameFromInt(i));
				f.setAccessible(true);
				return (z)f.get(this);
			} catch (NoSuchFieldException e)
			{
				throw new RuntimeException("Couldn't access index " + i + " of tuple " + this);
			} catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
		private String nameFromInt(int i) { return Character.valueOf((char)('A' + (char)i)).toString(); }

		public boolean match1(One t) {
			return check(first(),t.first());
		}

		public boolean match2(Two t) {
			return false;
		}

		public boolean match3(Three t) {
			return false;
		}

		public boolean match4(Four t) {
			return false;
		}

		public boolean match5(Five t) {
			return false;
		}

		public boolean match(Tuple t) {
			return t.match1(this);
		}


		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			One one = (One) o;

			if (A != null ? !A.equals(one.A) : one.A != null) return false;

			return true;
		}

		public int hashCode() {
			return (A != null ? A.hashCode() : 0);
		}
	}

	public static class Two<a,b> extends One<a>
	{
		private b B;
		public Two(a a, b b) { super(a); B = b; }
		public b second() { return B; }

		@Override
		public int count() {
			return 2;
		}

		@Override
		public boolean match1(One t) {
			return false;
		}

		@Override
		public boolean match2(Two t) {
			return match1((One)t) && check(second(),t.second());
		}

		@Override
		public boolean match(Tuple t) {
			return t.match2(this);
		}


		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;

			Two two = (Two) o;

			if (B != null ? !B.equals(two.B) : two.B != null) return false;

			return true;
		}

		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + (B != null ? B.hashCode() : 0);
			return result;
		}
	}

	public static class Three<a,b,c> extends Two<a,b>
	{
		private c C;
		public Three(a a, b b, c c) { super(a,b); C = c; }
		public c third() { return C; }

		@Override
		public int count() {
			return 3;
		}


		@Override
		public boolean match1(One t) {
			return false;
		}

		@Override
		public boolean match2(Two t) {
			return false;
		}

		@Override
		public boolean match3(Three t) {
			return super.match2((Two)t) && check(third(),t.third());
		}

		@Override
		public boolean match(Tuple t) {
			return t.match3(this);
		}


		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;

			Three three = (Three) o;

			if (C != null ? !C.equals(three.C) : three.C != null) return false;

			return true;
		}

		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + (C != null ? C.hashCode() : 0);
			return result;
		}
	}

	public static class Four<a,b,c,d> extends Three<a,b,c>
	{
		private d D;
		public Four(a a, b b, c c, d d) { super(a,b,c); D = d; }
		public d fourth() { return D; }

		@Override
		public int count() {
			return 4;
		}


		@Override
		public boolean match(Tuple t) {
			return t.match4(this);
		}

		@Override
		public boolean match1(One t) {
			return false;
		}

		@Override
		public boolean match2(Two t) {
			return false;
		}

		@Override
		public boolean match3(Three t) {
			return false;
		}

		@Override
		public boolean match4(Four t) {
			return super.match3((Three)t) && check(fourth(), t.fourth());
		}


		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;

			Four four = (Four) o;

			return !(D != null ? !D.equals(four.D) : four.D != null);

		}

		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + (D != null ? D.hashCode() : 0);
			return result;
		}
	}

	public static class Five<a,b,c,d,e> extends Four<a,b,c,d>
	{
		private e E;
		public Five(a a, b b, c c, d d, e e) { super(a,b,c,d); E = e; }
		public e fifth() { return E; }

		@Override
		public int count() {
			return 5;
		}


		@Override
		public boolean match(Tuple t) {
			return t.match5(this);
		}

		@Override
		public boolean match1(One t) {
			return false;
		}

		@Override
		public boolean match2(Two t) {
			return false;
		}

		@Override
		public boolean match3(Three t) {
			return false;
		}

		@Override
		public boolean match4(Four t) {
			return false;
		}


		@Override
		public boolean match5(Five t) {
			return super.match4((Four)t) && check(fifth(),t.fifth());
		}


		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;

			Five five = (Five) o;

			if (E != null ? !E.equals(five.E) : five.E != null) return false;

			return true;
		}

		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + (E != null ? E.hashCode() : 0);
			return result;
		}
	}

	public static <a> One<a> one(a A) { return new One<a>(A); }
	public static <a> One<a> tuple(a A) { return new One<a>(A); }

	public static <a,b> Two<a,b> two(a A, b B) { return new Two<a,b>(A,B); }
	public static <a,b> Two<a,b> tuple(a A, b B) { return new Two<a,b>(A,B); }

	public static <a,b,c> Three<a,b,c> three(a A, b B, c C) { return new Three<a,b,c>(A,B,C); }
	public static <a,b,c> Three<a,b,c> tuple(a A, b B, c C) { return new Three<a,b,c>(A,B,C); }

	public static <a,b,c,d> Four<a,b,c,d> four(a A, b B, c C, d D) { return new Four<a,b,c,d>(A,B,C,D); }
	public static <a,b,c,d> Four<a,b,c,d> tuple(a A, b B, c C, d D) { return new Four<a,b,c,d>(A,B,C,D); }

	public static <a,b,c,d,e> Five<a,b,c,d,e> five(a A, b B, c C, d D, e E) { return new Five<a,b,c,d,e>(A,B,C,D,E); }
	public static <a,b,c,d,e> Five<a,b,c,d,e> tuple(a A, b B, c C, d D, e E) { return new Five<a,b,c,d,e>(A,B,C,D,E); }
}
