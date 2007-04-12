package jlo.ioe.messaging;

import jlo.ioe.util.F;
import jlo.ioe.util.Tuple;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Apr 4, 2007
 * Time: 6:24:43 PM
 */
public class MessageService {
	public static final MessageService _instance = new MessageService();
	private ConcurrentLinkedQueue<Message> queue;
	private Executor threads = Executors.newCachedThreadPool();

	private MessageService() {
		
	}

	// add messages to a thread safe priority queue
	// use a thread pool to distribute messages to listeners
	// drop messages that do not have listeners

	public MessageService publish(Message m) {
		queue.offer(m);
		return this;
	}

	public <T> void subscribe(T sender, Class msgClass, F.lambda action) {

	}

	public void subscribe(Class msgClass, F.lambda action) {

	}

	public void subscribe(Class msgClass, Tuple params, F.lambda action) {
		
	}

	public <T> void subscribe(T t, Class msgClass, Tuple filter, F.lambda lambda) {

	}

	public static MessageService singleton() {
		return _instance;
	}

	private static class Runner implements Runnable {
		private F.lambda fun;

		public void setFun(F.lambda fun) {
			this.fun = fun;
		}

		public void apply(Object ... args) {
			fun.apply(args);
		}

		public void run() {
			fun.call();
		}
	}
}
