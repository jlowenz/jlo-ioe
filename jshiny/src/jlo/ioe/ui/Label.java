package jlo.ioe.ui;

import javax.swing.JLabel;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 4:45:12 PM<br>
 */
public class Label extends JLabel implements IComponent<JLabel> {
	private IComponent<JLabel> comp = new Component<JLabel>(this);

	public JLabel get() {
		return comp.get();
	}

	public Label(String string) {
		super(string);
	}

	public Label() {
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

	public IComponent<JLabel> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JLabel> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JLabel> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JLabel> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JLabel> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
