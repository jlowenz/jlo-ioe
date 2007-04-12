package jlo.ioe.messaging;

import jlo.ioe.util.Tuple;

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
}
