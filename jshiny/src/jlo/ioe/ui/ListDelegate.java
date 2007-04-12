package jlo.ioe.ui;

import jlo.ioe.messaging.AbstractMessage;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.LinkedList;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 4:58:37 PM<br>
 */
public abstract class ListDelegate<A> implements ListModel {
	public static final Class Update = UpdateMsg.class;
	static class UpdateMsg<A> extends AbstractMessage {
		java.util.List<A> changed;
		public UpdateMsg(Object sender, java.util.List<A> changed) {
			super(sender);
			this.changed = changed;
		}
	}

	private List list;
	private java.util.List<ListDataListener> listeners = new LinkedList<ListDataListener>();

	public ListDelegate(List l) {
		list = l;
	}

	public void fireUpdate(java.util.List<A> changed) {
		for (ListDataListener l : listeners) {
			l.contentsChanged(new ListDataEvent(list, ListDataEvent.CONTENTS_CHANGED, 0, this.getSize()-1));
		}
	}

	public void addListDataListener(ListDataListener listDataListener) {
		listeners.add(listDataListener);
	}

	public void removeListDataListener(ListDataListener listDataListener) {
		listeners.remove(listDataListener);
	}

	public abstract void update(java.util.List<A> v);
}
