package jlo.ioe.ui.behavior;

import jlo.ioe.messaging.AbstractMessageWithParams;
import jlo.ioe.messaging.MessageService;
import jlo.ioe.util.Tuple;

import javax.swing.JComponent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 11, 2007<br>
 * Time: 7:13:13 PM<br>
 */
public class KeyTracker<T extends JComponent> extends KeyAdapter {
	public static final Class Key = KeyMsg.class;
	static class KeyMsg extends AbstractMessageWithParams {
		private Tuple.One<KeyEvent> ke;
		public KeyMsg(Object sender, Tuple.One<KeyEvent> t) {
			super(sender, t);
			ke = t;
		}
		public KeyEvent getEvent() {
			return ke.first();
		}
	}

	private T comp;
	public KeyTracker(T comp) {
		this.comp = comp;
		this.comp.addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		MessageService.singleton().publish(new KeyMsg(comp, Tuple.one(keyEvent)));
	}
}
