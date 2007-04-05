package jlo.ioe.messaging;

import java.io.Serializable;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 6:40:31 PM<br>
 */
public class SubscriberDelegate implements Subscriber, Serializable {

	public void subscribe(Object sender, Class msgClass) {
		// record the subscription
		MessageService.singleton().subscribe(sender, msgClass);
	}

	public void subscribe(Class msgClass) {
		MessageService.singleton().subscribe(msgClass);
	}

	private class MessagePickle {
		public Class msgClass;
		// todo: implement object reference
	}
}
