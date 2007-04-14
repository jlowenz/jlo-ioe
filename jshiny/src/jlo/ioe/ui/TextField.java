package jlo.ioe.ui;

import javax.swing.JTextField;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 10:26:36 PM<br>
 */
public class TextField extends JTextField implements ITextComponent<JTextField>, ActionCapable {
	ITextComponent<JTextField> comp = new TextComponent<JTextField>(this);

	public ITextComponent<JTextField> initalFocus() {
		return comp.initalFocus();
	}

	public JTextField get() {
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

	public IComponent<JTextField> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JTextField> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JTextField> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JTextField> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JTextField> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
