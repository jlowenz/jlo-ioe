package jlo.ioe.messaging;

import jlo.ioe.ObjectService;
import jlo.ioe.util.F;
import jlo.ioe.util.Identifiable;
import jlo.ioe.util.LazyRef;
import jlo.ioe.util.Tuple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 6:40:31 PM<br>
 */
public class SubscriberDelegate implements Serializable {

	public <T extends Identifiable> void subscribe(LazyRef<T> sender, Class msgClass, F.lambda action) {
		// record the subscription
		pickles.add(new TargetedMessagePickle(msgClass, sender, action));
		MessageService.singleton().subscribe(sender.get(), msgClass, action);
	}

	public void subscribe(Class msgClass, F.lambda action) {
		MessageService.singleton().subscribe(msgClass, action);
	}

	private List<MessagePickle> pickles = new LinkedList<MessagePickle>();

	private static class MessagePickle implements Serializable {
		public Class msgClass;
		public F.lambda action;

		public MessagePickle(Class msgClass, F.lambda action) {
			this.msgClass = msgClass;
			this.action = action;
		}

		public void subscribe() {
			MessageService.singleton().subscribe(msgClass, action);
		}
	}
	private static class TargetedMessagePickle extends MessagePickle {
		public LazyRef target;

		public TargetedMessagePickle(Class msgClass, LazyRef target, F.lambda action) {
			super(msgClass,action);
			this.target = target;
		}

		@Override
		public void subscribe() {
			if (target.isLoaded()) {
				MessageService.singleton().subscribe(target.get(), msgClass, action);
			} else {
				MessageService.singleton().subscribe(ObjectService.Loaded, Tuple.one(target.getOID()), subscribe);
			}
		}

		private F.lambda<Object> subscribe = new F.lambda<Object>(){protected Object code() {
			MessageService.singleton().subscribe(target.get(), msgClass, action);
			return null;
		}};
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		for (MessagePickle p : pickles) {
			p.subscribe(); // reconnect on deserialization
		}
	}
}
