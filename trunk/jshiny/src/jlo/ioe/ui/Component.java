package jlo.ioe.ui;

import jlo.ioe.Environment;
import jlo.ioe.messaging.MessageService;
import jlo.ioe.ui.behavior.Action;
import jlo.ioe.util.F;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 2:34:05 PM<br>
 */
class Component<T extends JComponent> implements IComponent<T> {
	protected ActionsHandler actions;
	private CommandInterceptor ci;
	private T comp;

	public Component(T comp) {
		this.comp = comp;
		ci = new CommandInterceptor();
		if (comp instanceof ActionCapable) {
			final ActionCapable ac = (ActionCapable)comp;
			actions = new ActionsHandler() {
				ActionListener al = new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						MessageService.singleton().publish(new Action(Component.this.comp, actionEvent));
					}
				};
				public void areHandled() {
					ac.addActionListener(al);
				}
				public void areNotHandled() {
					ac.removeActionListener(al);
				}
			};
		} else {
			actions = new ActionsHandler() {
				public void areHandled() {}
				public void areNotHandled() {}
			};
		}
		actions.areNotHandled();
	}

	protected void onSwingThread(final F.lambda<Object> f) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.call();
			}
		});
	}


	public T get() {
		return comp;
	}

	public <T> void update(T val) { }
	public ActionsHandler actions() { return actions; }


	public T comp() {
		return comp;
	}
	protected void comp(T c) { comp = c; }
	public double preferredWidth() { return comp.getPreferredSize().getWidth(); }
	public double preferredHeight() { return comp.getPreferredSize().getHeight(); }
	public Component<T> preferredWidth(double w) { comp.setPreferredSize(new Dimension((int)w,(int)preferredHeight())); return this; }
	public Component<T> preferredHeight(double h) { comp.setPreferredSize(new Dimension((int)preferredWidth(),(int)h)); return this; }
	public Component<T> preferredSize(double w, double h) { comp.setPreferredSize(new Dimension((int)w,(int)h)); return this; }
	public Component<T> setWidth(double w) { comp.setSize(new Dimension((int)w, comp.getHeight())); return this; }
	public Component<T> setHeight(double h) { comp.setSize(new Dimension(comp.getWidth(), (int)h)); return this; }

	private class CommandInterceptor {
		private static final long DIFF = 500;
		private long lastTime = System.currentTimeMillis();

		public CommandInterceptor() {
			System.out.println("added command interceptor: " + comp);
			//new Throwable().printStackTrace();
			comp.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent e) {
					KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
					String s = ks.toString();
					if (s.equals("meta pressed TAB")) {
						Environment.singleton().nextSheet();
						lastTime = 0;
					} else if (s.equals("shift meta pressed TAB")) {
						Environment.singleton().prevSheet();
						lastTime = 0;
					} else if (s.equals("meta pressed META")) {
						System.out.println("meta pressed META");
						long currTime = System.currentTimeMillis();
						if ((currTime - lastTime) < DIFF) {
							Environment.singleton().commandRequested();
							lastTime = 0;
						} else {
							lastTime = currTime;
						}
					}
				}
			});
		}
	}
}
