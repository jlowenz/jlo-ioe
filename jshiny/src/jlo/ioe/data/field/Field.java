package jlo.ioe.data.field;

import jlo.ioe.messaging.AbstractMessage;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 4:00:40 PM<br>
 */
public class Field {
	public static final Class Changed = ChangedMsg.class;
	static class ChangedMsg extends AbstractMessage {
		public ChangedMsg(Object sender) {
			super(sender);
		}
	}
	
}
