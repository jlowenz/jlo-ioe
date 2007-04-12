package jlo.ioe.ui.behavior;

import jlo.ioe.messaging.AbstractMessage;

import java.awt.event.ActionEvent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 2:40:49 PM<br>
 */
public class Action extends AbstractMessage {
	private ActionEvent event;
	public Action(Object sender, ActionEvent e) {
		super(sender);
		event = e;
	}

	public ActionEvent getEvent() {
		return event;
	}
}
