package jlo.ioe.messaging;

import jlo.ioe.util.F;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 6:30:55 PM<br>
 */
public class AbstractMessage implements Message {
	public Object sender;
	static final List<F.lambda> EMPTY_LIST = new LinkedList<F.lambda>();

	public AbstractMessage(Object sender) {
		this.sender = sender;
	}

	public Object getSender() {
		return sender;
	}

	public List<F.lambda> match(MessageService.MsgMap msgMap,
	                            MessageService.MsgFilterMap msgFilterMap,
	                            MessageService.SenderMap senderMap,
	                            MessageService.SenderFilterMap senderFilterMap) {
		List<F.lambda> funs = new LinkedList<F.lambda>();
		for (F.lambda f : get(msgMap, getClass())) {
			funs.add(f);
		}
		for (F.lambda f : get(senderMap, sender, getClass())) {
			funs.add(f);
		}
		return funs;
	}

	private List<F.lambda> get(MessageService.SenderMap senderMap, Object sender, Class<? extends AbstractMessage> aClass) {
		Map<Class, List<F.lambda>> m = senderMap.get(sender);
		if (m == null) return EMPTY_LIST;
		else {
			List<F.lambda> l = m.get(aClass);
			if (l == null) return EMPTY_LIST;
			else return l;
		}
	}

	private List<F.lambda> get(MessageService.MsgMap msgMap, Class<? extends AbstractMessage> aClass) {
		List<F.lambda> l = msgMap.get(aClass);
		if (l == null) return EMPTY_LIST;
		else return l;
	}


	public void execute(Executor threads, final F.lambda f) {
		threads.execute(new Runnable() {
			public void run() {
				try {
					f.call();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}


}
