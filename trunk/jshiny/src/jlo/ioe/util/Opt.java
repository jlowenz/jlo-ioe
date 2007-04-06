package jlo.ioe.util;

import static jlo.ioe.util.F.lambda;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 8:15:05 AM<br>
 */
public abstract class Opt<T> {
	public static <T> Opt<T> some(T obj) {
		return new Some<T>(obj);
	}

	public static <T> Opt<T> none() {
		return new None<T>();
	}

	public abstract <R> R match(lambda<R> some, lambda<R> none) throws RuntimeException;
	public abstract T get(T defaultValue);
	public abstract T getOrElse(lambda<T> getter);
	public abstract Opt<T> orElse(lambda<Opt<T>> getter);

	static class None<T> extends Opt<T> {
		public <R> R match(lambda<R> some, lambda<R> none) {
			try {
				return none.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		public T get(T defaultValue) {
			return defaultValue;
		}

		public Opt<T> orElse(lambda<Opt<T>> getter) {
			return getter.call();
		}

		public T getOrElse(lambda<T> getter) {
			return getter.call();
		}
	}
	static class Some<T> extends Opt<T> {
		private T obj;
		public Some(@NotNull T obj) {
			this.obj = obj;
		}
		public <R> R match(lambda<R> some, lambda<R> none) throws RuntimeException {
			try {
				return some.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		public T get(T defaultValue) {
			return obj;
		}
		public T getOrElse(lambda<T> getter) {
			return obj;
		}
		public Opt<T> orElse(lambda<Opt<T>> getter) {
			return this;
		}
	}
}
