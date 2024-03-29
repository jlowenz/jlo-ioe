package jlo.ioe;

import jlo.ioe.data.CommandInterface;
import jlo.ioe.data.DataObject;
import jlo.ioe.ui.LayeredPane;
import jlo.ioe.ui.Panel;
import jlo.ioe.ui.RootPane;
import jlo.ioe.util.F;
import jlo.ioe.util.FocusOwnerTracker;
import jlo.ioe.util.Opt;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright � 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 2:17:44 PM<br>
 */
public class Screen extends JFrame {
	transient GraphicsDevice screenDevice =
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	Panel top = new Panel() {{ setBorder(new LineBorder(Color.gray,1)); }};
	Panel center = new Panel() {{ setBorder(null); }};
	SheetSelector sheetSelector = new SheetSelector(width());
	boolean commandOn = false;
	ScreenState sstate;
	private FocusOwnerTracker fot;

	public Screen(String name) {
		sstate = new ScreenState(name);
		setup();
	}

	private void setup() {
		RootPane rp = new RootPane();
		fot = new FocusOwnerTracker(this) {
			public void focusLost() {
				System.out.println("lost focus");
			}

			public void focusGained() {
				System.out.println("gained focus");
			}
		};
		setRootPane(rp);
		setLayeredPane(new LayeredPane());
		setContentPane(new Panel());
		_layout();
		//setFullscreen(true);
		setVisible(true);
		enableEvents(AWTEvent.KEY_EVENT_MASK);
		setFocusable(true);
		getContentPane().requestFocusInWindow();
	}

	private void setFullscreen(boolean b) {
		DisplayMode dm = screenDevice.getDisplayMode();
		if (screenDevice.isFullScreenSupported()) {
			setUndecorated(true);
			setResizable(false);
			screenDevice.setFullScreenWindow(this);
			validate();
		} else {
			System.out.println("full-screen mode unsupported");
		}
	}

	private void _layout() {
		setLayout(new BorderLayout());
		top.setBackground(Color.black);
		center.setBackground(Color.white);
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(sheetSelector, BorderLayout.SOUTH);
		getLayeredPane().setLayout(null);
		validate();
		repaint();
	}

	public Sheet newSheet(DataObject o) {
		return null;
	}

	int width() { return screenDevice.getDisplayMode().getWidth(); }
	int height() { return screenDevice.getDisplayMode().getHeight(); }
	void moveToCenter(JComponent c) {
		int w = (width()-c.getWidth())/2;
		int h = (height()-c.getHeight())/2;
		c.setLocation(w,h);
	}

	public void splitSheet(DataObject o) {

	}

	public void nextSheet() {
		sstate.currentSheet.match(
				new F.lambda1<Object,Sheet>(){protected Object code(Sheet p) {
					sstate.currentSheet = Opt.some(sheetSelector.nextSheet()); return null;
				}},
				new F.lambda1<Object, Sheet>(){protected Object code(Sheet p) {
					return null;
				}});
	}

	public void prevSheet() {
		sstate.currentSheet.match(
				new F.lambda1<Object,Sheet>(){protected Object code(Sheet p) {
					sstate.currentSheet = Opt.some(sheetSelector.prevSheet()); return null;
				}},
				new F.lambda1<Object, Sheet>(){protected Object code(Sheet p) {
					return null;
				}});
	}

	public void commandRequested() {
		if (!commandOn) {
			commandOn = true; showCommand();
		} else {
			commandOn = false; removeCommand();
		}
	}

	private void removeCommand() {
		System.out.println("removeCommand");
		SwingUtilities.invokeLater(new Runnable() {public void run() {
			getLayeredPane().remove(CommandInterface.singleton().component());
			CommandInterface.singleton().component().setVisible(false);
			validate();
			repaint();
			getContentPane().requestFocusInWindow();
		}});
	}

	private void showCommand() {
		System.out.println("showCommand");
		SwingUtilities.invokeLater(new Runnable() {public void run() {
			CommandInterface.singleton().component().setVisible(true);
			moveToCenter(CommandInterface.singleton().component());
			getLayeredPane().add(CommandInterface.singleton().component(),CommandInterface.singleton().level());
			validate();
			repaint();
		}});
	}

	public void display(final Sheet sheet) {
		Runnable displayer = new Runnable() {public void run() {
			sstate.currentSheet.match(
					new F.lambda1<Object,Sheet>(){protected Object code(Sheet p) {
						center.remove(p); p.setVisible(false); return null;
					}},
					new F.lambda1<Object, Sheet>(){protected Object code(Sheet p) {
						return null;
					}}
			);
			sheet.setVisible(true);
			center.add(sheet);
			center.invalidate();
			validate();
			repaint();
			sstate.currentSheet = Opt.some(sheet);
		}};
		if (Thread.currentThread().getName().startsWith("AWT")) {
			displayer.run();
		} else {
			SwingUtilities.invokeLater(displayer);
		}
	}

	public static class ScreenState implements Serializable {
		public List<Sheet> sheets = new LinkedList<Sheet>();
		public Opt<Sheet> currentSheet = Opt.none();
		public String name;

		public ScreenState(String name) {
			this.name = name;
		}
	}
}
