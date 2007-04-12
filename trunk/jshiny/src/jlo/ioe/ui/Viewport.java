package jlo.ioe.ui;

import javax.swing.JViewport;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 3:02:45 PM<br>
 */
public class Viewport extends JViewport implements IComponent<JViewport> {
	public <V> void update(V val) {
		comp.update(val);
	}


	public JViewport get() {
		return comp.get();
	}

	public double preferredWidth() {
		return comp.preferredWidth();
	}

	public double preferredHeight() {
		return comp.preferredHeight();
	}

	public IComponent<JViewport> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JViewport> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JViewport> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JViewport> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JViewport> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}

	IComponent<JViewport> comp = new Component<JViewport>(this);
}
