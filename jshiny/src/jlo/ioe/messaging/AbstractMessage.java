package jlo.ioe.messaging;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 6:30:55 PM<br>
 */
public class AbstractMessage implements Message {
	public Object sender;

	public AbstractMessage(Object sender) {
		this.sender = sender;
	}

	public Object getSender() {
		return sender;
	}
}
