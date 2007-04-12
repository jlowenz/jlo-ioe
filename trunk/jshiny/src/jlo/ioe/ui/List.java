package jlo.ioe.ui;

import javax.swing.JList;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 4:57:46 PM<br>
 */
public class List<T> extends JList implements IComponent<JList> {
	IComponent<JList> comp = new Component<JList>(this);

	private ListDelegate<T> delegate;
	public void setDelegate(ListDelegate<T> d) {
		delegate = d;
		setModel(delegate);
	}

	public JList get() {
		return comp.get();
	}

	public <V> void update(V val) {
		comp.update(val);
	}

	public double preferredWidth() {
		return comp.preferredWidth();
	}

	public double preferredHeight() {
		return comp.preferredHeight();
	}

	public IComponent<JList> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JList> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JList> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JList> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JList> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
