package jlo.ioe.ui;

import javax.swing.JRootPane;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 7:27:06 PM<br>
 */
public class RootPane extends JRootPane implements IComponent<JRootPane> {
	private IComponent<JRootPane> comp = new Component<JRootPane>(this);


	public JRootPane get() {
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

	public IComponent<JRootPane> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JRootPane> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JRootPane> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JRootPane> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JRootPane> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
