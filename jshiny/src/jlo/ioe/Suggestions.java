package jlo.ioe;

import jlo.ioe.data.VocabularyTerm;
import jlo.ioe.ui.List;
import jlo.ioe.ui.ListDelegate;
import jlo.ioe.ui.Panel;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 12, 2007<br>
 * Time: 2:12:15 PM<br>
 */
public class Suggestions {
	private static Suggestions _instance = new Suggestions();

	public static Suggestions singleton() {
		return _instance;
	}

	private List<VocabularyTerm> uilist = new List<VocabularyTerm>() {
		{ setLayoutOrientation(List.HORIZONTAL_WRAP); }
	};
	private Panel ui = new Panel() {
		{
			setOpaque(false);
			setSize(400,200);
			setBorder(new EmptyBorder(10,10,10,10));
			setLayout(new BorderLayout());
			add(uilist);
		}


		@Override
		protected void paintChildren(Graphics graphics) {
			Graphics2D g2 = (Graphics2D) graphics;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
			super.paintChildren(g2);
		}


		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g2 = (Graphics2D) graphics;
			g2.setColor(Color.yellow.brighter().brighter());
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
		}
	};
	private class Delegate extends ListDelegate<VocabularyTerm> {
		private java.util.List<VocabularyTerm> data = new LinkedList<VocabularyTerm>();

		public Delegate(List l) {
			super(l);
		}

		public int getSize() {
			return data.size();
		}

		public Object getElementAt(int i) {
			return data.get(i);
		}

		public void update(@NotNull java.util.List<VocabularyTerm> l) { data = l; fireUpdate(data); }
	}
	private ListDelegate<VocabularyTerm> model = new Delegate(uilist);
	{ uilist.setDelegate(model); }

	private boolean shown;
	public void showAt(final double x, final double y, Container c) {
		if (!shown) {
			shown = true;
			addTo(c);
			SwingUtilities.invokeLater(new Runnable() {public void run() {
				ui.setLocation((int)x, (int)y);
				ui.setVisible(true);
				ui.validate();
			}});
		}
	}

	private void addTo(Container c) {
		c.add(ui);
	}


	public void hide(final Container c) {
		if (shown) {
			SwingUtilities.invokeLater(new Runnable() {public void run() {
				ui.setVisible(false);
				shown = false;
				removeFrom(c);
				ui.validate();
			}});
		}
	}

	private void removeFrom(Container c) {
		c.remove(ui);
	}

	public void showSuggestions(java.util.List<VocabularyTerm> s) {
		model.update(s);
	}


}
