package jlo.ioe.ui;

import jlo.ioe.messaging.AbstractMessage;

import javax.swing.JButton;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 3:04:51 PM<br>
 */
public class Button extends JButton implements IComponent<JButton> {

	public JButton get() {
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

	public IComponent<JButton> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JButton> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public IComponent<JButton> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JButton> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JButton> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}

	private IComponent<JButton> comp = new Component<JButton>(this);

	public static final Class Pressed = PressedMsg.class;
	public static final class PressedMsg extends AbstractMessage {
		public PressedMsg(Object sender) {
			super(sender);
		}
	}

	public Button(String title) {
		super(title);
	}


}
