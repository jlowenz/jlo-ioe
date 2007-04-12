package jlo.ioe.ui.behavior;

import jlo.ioe.messaging.AbstractMessageWithParams;
import jlo.ioe.messaging.Message;
import jlo.ioe.messaging.MessageService;
import jlo.ioe.util.Tuple;

import javax.swing.JComponent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 12, 2007<br>
 * Time: 2:00:54 PM<br>
 */
public class AncestorTracker implements AncestorListener {
	public static final Class<? extends Message> Ancestor = AncestorMsg.class;
	static class AncestorMsg extends AbstractMessageWithParams {
		private Tuple.One<String> kind;
		public AncestorMsg(Object sender, Tuple.One<String> t) {
			super(sender, t);
			kind = t;
		}
		public String getKind() {
			return kind.first();
		}
	}

	private JComponent comp;

	public AncestorTracker(JComponent comp) {
		this.comp = comp;
		this.comp.addAncestorListener(this);
	}

	public void ancestorAdded(AncestorEvent ancestorEvent) {
		MessageService.singleton().publish(new AncestorMsg(comp, Tuple.one("added")));
	}
	public void ancestorRemoved(AncestorEvent ancestorEvent) {
		MessageService.singleton().publish(new AncestorMsg(comp, Tuple.one("removed")));
	}
	public void ancestorMoved(AncestorEvent ancestorEvent) {
		MessageService.singleton().publish(new AncestorMsg(comp, Tuple.one("moved")));
	}
}
