package jlo.ioe.ui;

import javax.swing.JLayeredPane;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 11, 2007<br>
 * Time: 5:56:45 PM<br>
 */
public class LayeredPane extends JLayeredPane implements IComponent<JLayeredPane> {
	IComponent<JLayeredPane> comp = new Component<JLayeredPane>(this);


	public JLayeredPane get() {
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

	public IComponent<JLayeredPane> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JLayeredPane> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JLayeredPane> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JLayeredPane> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JLayeredPane> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
