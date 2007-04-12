package jlo.ioe.ui;

import javax.swing.JEditorPane;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 11:10:42 PM<br>
 */
public class TextPane extends JEditorPane implements ITextComponent<JEditorPane> {
	ITextComponent<JEditorPane> comp = new TextComponent<JEditorPane>(this);

	public ITextComponent<JEditorPane> initalFocus() {
		return comp.initalFocus();
	}

	public JEditorPane get() {
		return comp.get();
	}

	public <V> void update(V val) {
		setText((String)val);
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return super.getScrollableTracksViewportHeight();
	}

	public double preferredWidth() {
		return comp.preferredWidth();
	}

	public double preferredHeight() {
		return comp.preferredHeight();
	}

	public IComponent<JEditorPane> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JEditorPane> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JEditorPane> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JEditorPane> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JEditorPane> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
