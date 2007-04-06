package jlo.ioe;

import jlo.ioe.messaging.AbstractMessage;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 8:53:33 PM<br>
 */
public class ObjectService {

	public static Class Loaded = LoadedMsg.class;
	static class LoadedMsg extends AbstractMessage {
		public LoadedMsg(Object sender) {
			super(sender);
		}
	}
}
