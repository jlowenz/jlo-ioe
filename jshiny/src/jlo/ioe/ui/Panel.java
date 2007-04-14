package jlo.ioe.ui;

import javax.swing.JPanel;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 5:34:27 PM<br>
 */
public class Panel extends JPanel implements IComponent<JPanel> {
	private IComponent<JPanel> comp = new Component<JPanel>(this);

	public Panel() {
		preferredSize(100,20);
		setLayout(new java.awt.BorderLayout());
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

	public IComponent<JPanel> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JPanel> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JPanel> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JPanel> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JPanel> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}

	public JPanel get() {
		return comp.get();
	}
}
