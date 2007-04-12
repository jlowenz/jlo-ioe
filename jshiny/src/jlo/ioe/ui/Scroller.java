package jlo.ioe.ui;

import javax.swing.JScrollPane;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 3:03:41 PM<br>
 */
public class Scroller extends JScrollPane implements IComponent<JScrollPane> {
	private IComponent<JScrollPane> comp = new Component<JScrollPane>(this);

	public JScrollPane get() {
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

	public IComponent<JScrollPane> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JScrollPane> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JScrollPane> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JScrollPane> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JScrollPane> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
