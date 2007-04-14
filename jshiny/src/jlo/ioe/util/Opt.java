package jlo.ioe.util;

import static jlo.ioe.util.F.lambda;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 8:15:05 AM<br>
 */
public abstract class Opt<T> implements Serializable {
	public static <T> Opt<T> some(T obj) {
		return new Some<T>(obj);
	}

	public static <T> Opt<T> none() {
		return new None<T>();
	}

	public abstract <R> R match(F.lambda1<R,T> some, F.lambda1<R,T> none) throws RuntimeException;
	public abstract <R extends T> R get(R defaultValue);
	public abstract T getOrElse(lambda<T> getter);
	public abstract Opt<T> orElse(lambda<Opt<T>> getter);

	public abstract void ifSet(F.lambda1<Object, T> f);
	
	static class None<T> extends Opt<T> {
		public <R> R match(F.lambda1<R,T> some, F.lambda1<R,T> none) {
			try {
				return none.call((T)null);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		public <R extends T> R get(R defaultValue) {
			return defaultValue;
		}

		public Opt<T> orElse(lambda<Opt<T>> getter) {
			return getter.call();
		}

		public T getOrElse(lambda<T> getter) {
			return getter.call();
		}

		public void ifSet(F.lambda1<Object, T> f) {}
	}
	static class Some<T> extends Opt<T> {
		private T obj;
		public Some(@NotNull T obj) {
			this.obj = obj;
		}
		public <R> R match(F.lambda1<R,T> some, F.lambda1<R,T> none) throws RuntimeException {
			try {
				return some.call(obj);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		public <R extends T> R get(R defaultValue) {
			return (R)obj;
		}
		public T getOrElse(lambda<T> getter) {
			return obj;
		}
		public Opt<T> orElse(lambda<Opt<T>> getter) {
			return this;
		}
		public void ifSet(F.lambda1<Object, T> f) {
			f.call(obj);
		}
	}
}
