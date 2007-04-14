package jlo.ioe.data;

import jlo.ioe.Command;
import jlo.ioe.Environment;
import jlo.ioe.Suggestions;
import jlo.ioe.messaging.MessageService;
import jlo.ioe.messaging.SubscriberDelegate;
import jlo.ioe.ui.Label;
import jlo.ioe.ui.Panel;
import jlo.ioe.ui.TextField;
import jlo.ioe.ui.behavior.Action;
import jlo.ioe.ui.behavior.AncestorTracker;
import jlo.ioe.ui.behavior.DocumentTracker;
import jlo.ioe.ui.behavior.KeyTracker;
import jlo.ioe.util.F;
import jlo.ioe.util.Opt;
import jlo.ioe.util.Tuple;
import jlo.ioe.util.Util;
import org.jdesktop.layout.GroupLayout;

import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 11, 2007<br>
 * Time: 7:03:20 PM<br>
 */
public class CommandInterface {

	private static final CommandInterface _instance = new CommandInterface();

	public static CommandInterface singleton() {
		return _instance;
	}

	private CommandInterface() {
		MessageService.singleton().subscribe(
				commandPanel,
				AncestorTracker.Ancestor,
				Tuple.one("added"),
				new F.lambda1<Object, Tuple.One<String>>(){protected Object code(Tuple.One<String> p) {
					commandField.setText("");
					commandField.requestFocusInWindow();
					return null;
				}}
		);
		MessageService.singleton().subscribe(
				commandField,
				Action.class,
				new F.lambda0<Object>(){protected Object code() {
					System.out.println("action");
					currentCommand.ifSet(
							new F.lambda1<Object,Command>(){protected Object code(Command p) {
								p.termCompleted(suggestionMatches());
								p.execute();
								currentCommand = Opt.none();
								return null;
							}});
					Environment.singleton().commandRequested(); return null;
				}});
		MessageService.singleton().subscribe(
				commandField,
				DocumentTracker.DocumentChanged,
				new F.lambda1<Object, Tuple.One<DocumentEvent>>(){protected Object code(Tuple.One<DocumentEvent> p) {
					try {
						final String txt = commandField.getText();
						if (txt.length() > 0) {
							LinkedList<String> words = Util.toList(txt.split(" "));
							if (!words.isEmpty()) {
								final String lastFragment = words.getLast();
								currentCommand.match(
										new F.lambda1<Object,Command>(){protected Object code(Command p) {
											System.out.println("some command");
											p.updateFragment(lastFragment);
											return null;
										}},
										new F.lambda1<Object,Command>(){protected Object code(Command p) {
											System.out.println("none command");
											currentCommand = Opt.some(new Command());
											currentCommand.get(null).updateFragment(txt);
											return null;
										}}
								);
								updateSuggestions(lastFragment);
							}
						} else {
							updateSuggestions(txt);
						}
					} catch (Throwable e) {
						e.printStackTrace(System.err);
					}
					return null;
				}}
		);
		MessageService.singleton().subscribe(
				commandField,
				KeyTracker.Key,
				new F.lambda1<Object, Tuple.One<KeyEvent>>(){protected Object code(Tuple.One<KeyEvent> p) {
					switch (p.first().getKeyCode()) {
						case KeyEvent.VK_ESCAPE:
							Environment.singleton().commandRequested();
							break;
						case KeyEvent.VK_SPACE:
							currentCommand.ifSet(new F.lambda1<Object,Command>(){protected Object code(Command p) {
								p.termCompleted(suggestionMatches());
								return null;
							}});
							break;
						case KeyEvent.VK_TAB:
							selectSuggestedValue();
							break;
					}
					return null;
				}}
		);
		commandField.setText("");
	}

	private TextField commandField = new TextField() {
		KeyTracker kt = new KeyTracker<JTextField>(this);
		DocumentTracker dt = new DocumentTracker(this);
		{
			initalFocus();
			actions().areHandled();
			setBorder(new CompoundBorder(new LineBorder(Color.black,1), new EmptyBorder(2,2,2,2)));
		}

		@Override
		public String toString() {
			return "commandField";
		}
	};
	private Opt<Command> currentCommand = Opt.none();
	private Panel commandPanel = new Panel() {
		SubscriberDelegate subscriber = new SubscriberDelegate();
		{
			AncestorTracker at = new AncestorTracker(this);
			setPreferredSize(new Dimension(400,75));
			setSize(getPreferredSize());
			setOpaque(false);
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(10,10,10,10));
			initComponents();
		}

		private void initComponents() {
			Label jLabel1 = new Label();

			setOpaque(false);
			setPreferredSize(new java.awt.Dimension(400, 100));
			jLabel1.setText("Enter a command:");
			jLabel1.setFont(jLabel1.getFont().deriveFont(12f));

			commandField.setText("");
			commandField.setOpaque(false);

			GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
			this.setLayout(layout);
			layout.setHorizontalGroup(
			  layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			  .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
			       .addContainerGap()
			       .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
			            .add(org.jdesktop.layout.GroupLayout.LEADING, commandField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
			            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
			       .addContainerGap())
			);
			layout.setVerticalGroup(
			  layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			  .add(layout.createSequentialGroup()
			       .addContainerGap()
			       .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
			       .add(commandField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
			       .addContainerGap())
			);
			commandField.requestFocusInWindow();
		}


		@Override
		public void setVisible(boolean b) {
			if (!b) {
				Suggestions.singleton().hide(getParent());
			}
			super.setVisible(b);
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g2 = (Graphics2D) graphics;
			g2.setColor(Color.gray.brighter());
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
		}


		@Override
		protected void paintChildren(Graphics graphics) {
			Graphics2D g2 = (Graphics2D) graphics;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
			super.paintChildren(g2);
		}
	};

	private void selectSuggestedValue() {
		// todo finish
	}

	private void updateSuggestions(String lastFragment) {
		if (lastFragment.length() < 1) {
			Suggestions.singleton().hide(commandPanel.getParent());
		} else {
			Suggestions.singleton().showSuggestions(currentCommand.get(null).suggestions(lastFragment));
			Point p = new Point();
			commandPanel.getLocation(p);
			Suggestions.singleton().showAt(p.getX(), p.getY()+commandPanel.getHeight(), commandPanel.getParent());
		}
	}

	private List<VocabularyTerm> suggestionMatches() {
		String frag = commandField.getText();
		if (frag.length() > 0) {
			String lastFragment = Util.toList(frag.split(" ")).peekLast();
			return Vocabulary.matchingTerms(lastFragment);
		} else {
			return new LinkedList<VocabularyTerm>();
		}
	}


	public Panel component() {
		return commandPanel;
	}

	public int level() {
		return JLayeredPane.MODAL_LAYER;
	}
}
