package jlo.ioe.util;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 6:50:05 PM<br>
 */
public abstract class LazyRef<T extends Identifiable> implements Serializable {
	transient private Opt<T> object = Opt.none();
	private ObjectID oid;
	transient private Lock lock = new ReentrantLock();

	public LazyRef(T object) {
		this.object = Opt.some(object);
		this.oid = object.getObjectID();
	}

	public LazyRef(ObjectID oid) {
		this.oid = oid;
		object = Opt.none();
	}

	// eagerly created, but lazily executed
	private F.lambda<Opt<T>> loader = new F.lambda0<Opt<T>>(){protected Opt<T> code() {
		return loadObject(oid);
	}};

	public T get() {
		lock.lock();
		try {
			return (object = object.orElse(loader)).get((T)null);
		} finally { lock.unlock(); }
	}

	public boolean isLoaded() {
		return isLoaded(oid);
	}

	public ObjectID getOID() {
		return oid;	
	}

	abstract protected boolean isLoaded(ObjectID oid);
	abstract protected Opt<T> loadObject(ObjectID oid);
}
