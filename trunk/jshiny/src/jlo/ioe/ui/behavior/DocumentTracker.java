package jlo.ioe.ui.behavior;

import jlo.ioe.messaging.AbstractMessageWithParams;
import jlo.ioe.messaging.MessageService;
import jlo.ioe.util.Tuple;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 11:13:24 PM<br>
 */
public class DocumentTracker {
	public static final Class DocumentChanged = DocumentChangedMsg.class;
	static class DocumentChangedMsg extends AbstractMessageWithParams {
		public DocumentEvent event;
		public DocumentChangedMsg(Object sender, Tuple.One<DocumentEvent> e) {
			super(sender, e);
			this.event = e.first();
		}
	}

	public DocumentTracker(final JTextComponent comp) {
		comp.getDocument().addDocumentListener(new DocumentListener() {

			public void insertUpdate(DocumentEvent documentEvent) {
				MessageService.singleton().publish(new DocumentChangedMsg(comp, Tuple.one(documentEvent)));
			}

			public void removeUpdate(DocumentEvent documentEvent) {
				MessageService.singleton().publish(new DocumentChangedMsg(comp, Tuple.one(documentEvent)));
			}

			public void changedUpdate(DocumentEvent documentEvent) {
				MessageService.singleton().publish(new DocumentChangedMsg(comp, Tuple.one(documentEvent)));				
			}
		});
	}
}
