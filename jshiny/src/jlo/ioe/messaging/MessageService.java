package jlo.ioe.messaging;

import jlo.ioe.util.F;
import jlo.ioe.util.Tuple;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Apr 4, 2007
 * Time: 6:24:43 PM
 */
public class MessageService implements Runnable {
	public static final class MsgMap extends HashMap<Class, List<F.lambda>> {

	}
	public static final class MsgFilterMap extends HashMap<Class, Map<Tuple, List<F.lambda>>> {

	}
	public static final class SenderMap extends HashMap<Object, Map<Class, List<F.lambda>>> {

	}
	public static final class SenderFilterMap extends HashMap<Object, Map<Class, Map<Tuple, List<F.lambda>>>> {

	}

	public static final MessageService _instance = new MessageService();
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	private Executor threads = Executors.newCachedThreadPool();
	private MsgMap msgMap = new MsgMap();
	private MsgFilterMap msgFilterMap = new MsgFilterMap();
	private SenderMap senderMap = new SenderMap();
	private SenderFilterMap senderFilterMap = new SenderFilterMap();
	private boolean done = false;

	private MessageService() {
		new Thread(this).start();
	}

	public void run() {
		List<? extends F.lambda> funs;
		while (!done) {
			try {
				Message m = queue.poll(2, TimeUnit.SECONDS);
				if (m == null) continue;
				funs = m.match(msgMap, msgFilterMap, senderMap, senderFilterMap);

				for (F.lambda f : funs) {
					m.execute(threads, f);
				}
			} catch (Throwable e) {
				e.printStackTrace(System.err);
			}
		}
	}

	// add messages to a thread safe priority queue
	// use a thread pool to distribute messages to listeners
	// drop messages that do not have listeners

	public MessageService publish(Message m) {
		queue.offer(m);
		return this;
	}

	public <T> void subscribe(T sender, Class msgClass, F.lambda action) {
		put(senderMap, sender, msgClass, action);
	}

	private <T> void put(SenderMap senderMap, T sender, Class msgClass, F.lambda action) {
		Map<Class, List<F.lambda>> m = senderMap.get(sender);
		if (m == null) {
			m = new HashMap<Class, List<F.lambda>>();
			senderMap.put(sender, m);
		}
		List<F.lambda> f = m.get(msgClass);
		if (f == null) {
			f = new LinkedList<F.lambda>();
			m.put(msgClass, f);
		}
		f.add(action);
	}

	public void subscribe(Class msgClass, F.lambda action) {
		List<F.lambda> f = msgMap.get(msgClass);
		if (f == null) {
			f = new LinkedList<F.lambda>();
			msgMap.put(msgClass, f);
		}
		f.add(action);
	}

	public void subscribe(Class msgClass, Tuple params, F.lambda action) {
		Map<Tuple,List<F.lambda>> m = msgFilterMap.get(msgClass);
		if (m == null) {
			m = new HashMap<Tuple, List<F.lambda>>();
			msgFilterMap.put(msgClass, m);
		}
		List<F.lambda> f = m.get(params);
		if (f == null) {
			f = new LinkedList<F.lambda>();
			m.put(params, f);
		}
		f.add(action);
	}

	public <T> void subscribe(T t, Class msgClass, Tuple filter, F.lambda lambda) {
		Map<Class,Map<Tuple,List<F.lambda>>> m = senderFilterMap.get(t);
		if (m == null) {
			m = new HashMap<Class, Map<Tuple, List<F.lambda>>>();
			senderFilterMap.put(t, m);
		}
		Map<Tuple,List<F.lambda>> m1 = m.get(msgClass);
		if (m1 == null) {
			m1 = new HashMap<Tuple, List<F.lambda>>();
			m.put(msgClass, m1);
		}
		List<F.lambda> f = m1.get(filter);
		if (f == null) {
			f = new LinkedList<F.lambda>();
			m1.put(filter, f);
		}
		f.add(lambda);
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
