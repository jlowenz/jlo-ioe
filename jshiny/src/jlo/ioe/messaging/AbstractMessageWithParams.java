package jlo.ioe.messaging;

import jlo.ioe.util.F;
import jlo.ioe.util.Tuple;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 8, 2007<br>
 * Time: 12:06:56 AM<br>
 */
public class AbstractMessageWithParams extends AbstractMessage implements MessageWithParams {
	public Tuple params;
	public AbstractMessageWithParams(Object sender, Tuple t) {
		super(sender);
		params = t;
	}

	@Override
	public List<F.lambda> match(MessageService.MsgMap msgMap, MessageService.MsgFilterMap msgFilterMap, MessageService.SenderMap senderMap, MessageService.SenderFilterMap senderFilterMap) {
		List<F.lambda> funs = super.match(msgMap, msgFilterMap, senderMap, senderFilterMap);
		funs.addAll(get(msgFilterMap, getClass(), params));
		funs.addAll(get(senderFilterMap, sender, getClass(), params));
		return funs;
	}

	private List<F.lambda> get(MessageService.SenderFilterMap senderFilterMap, Object sender, Class<? extends AbstractMessageWithParams> aClass, Tuple params) {
		Map<Class, Map<Tuple,List<F.lambda>>> m = senderFilterMap.get(sender);
		if (m == null) return EMPTY_LIST;
		else {
			Map<Tuple,List<F.lambda>> m1 = m.get(aClass);
			if (m1 == null) return EMPTY_LIST;
			else {
				List<F.lambda> f = m1.get(params);
				if (f == null) return EMPTY_LIST;
				else return f;
			}
		}
	}

	private List<F.lambda> get(MessageService.MsgFilterMap msgFilterMap, Class<? extends AbstractMessageWithParams> aClass, Tuple params) {
		Map<Tuple,List<F.lambda>> m = msgFilterMap.get(aClass);
		if (m == null) return EMPTY_LIST;
		else {
			List<F.lambda> l = m.get(params);
			if (l == null) return EMPTY_LIST;
			else return l;
		}
	}


	@Override
	public void execute(Executor threads, final F.lambda f) {
		threads.execute(new Runnable() {public void run() {
			try {
				f.call(params);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}});
	}
}
