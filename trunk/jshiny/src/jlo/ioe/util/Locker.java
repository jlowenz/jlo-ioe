package jlo.ioe.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 9:31:15 AM<br>
 */
public class Locker {
	private Lock lock = new ReentrantLock();
	public <R> R lock(F.lambda<R> f) {
		lock.lock();
		try {
			return f.call();
		} finally { lock.unlock(); }
	}
}
