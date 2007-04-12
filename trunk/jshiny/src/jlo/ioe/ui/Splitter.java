package jlo.ioe.ui;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 7:32:42 PM<br>
 */
public class Splitter extends JXMultiSplitPane implements IComponent<JXMultiSplitPane> {
	private IComponent<JXMultiSplitPane> comp = new Component<JXMultiSplitPane>(this);

	public <T> void resplit(Split<T> root) {
		getMultiSplitLayout().setFloatingDividers(true);
		final Map<String,Split<T>> compMap = new HashMap<String,Split<T>>();
		MultiSplitLayout.Node layoutDef = buildLayout(root,compMap);
		System.out.println("Layout: " + layoutDef);
		//val modelRoot = MSPLayout.parseModel(layoutDef);
		getMultiSplitLayout().setModel(layoutDef);
		javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() {
			removeAll();
			for (String k : compMap.keySet()) {
				add(k, compMap.get(k).component().get());
			}
			revalidate();
			repaint();
		}});
	}

	private <T> MultiSplitLayout.Node buildLayout(Split<T> split, Map<String,Split<T>> map) {
		switch (split.kind) {
			case Horizontal: {
				MultiSplitLayout.Split n = new MultiSplitLayout.Split(
						buildLayout(split.first.get(null),map),
						new MultiSplitLayout.Divider(),
						buildLayout(split.second.get(null),map));
				n.setRowLayout(false);
				return n;
			}
			case Vertical: {
				MultiSplitLayout.Split n = new MultiSplitLayout.Split(
						buildLayout(split.first.get(null),map),
						new MultiSplitLayout.Divider(),
						buildLayout(split.second.get(null),map));
				n.setRowLayout(true);
				return n;
			}
			default: {
				String name = "comp" + split.component().get();
				map.put(name, split);
				return new MultiSplitLayout.Leaf(name);
			}
		}
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

	public IComponent<JXMultiSplitPane> preferredWidth(double v) {
		return comp.preferredWidth(v);
	}

	public IComponent<JXMultiSplitPane> preferredHeight(double v) {
		return comp.preferredHeight(v);
	}

	public JXMultiSplitPane get() {
		return comp.get();
	}

	public IComponent<JXMultiSplitPane> preferredSize(double w, double h) {
		return comp.preferredSize(w, h);
	}

	public IComponent<JXMultiSplitPane> setWidth(double w) {
		return comp.setWidth(w);
	}

	public IComponent<JXMultiSplitPane> setHeight(double h) {
		return comp.setHeight(h);
	}

	public ActionsHandler actions() {
		return comp.actions();
	}
}
