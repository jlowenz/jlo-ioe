package jlo.ioe.ui;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 8:54:23 PM<br>
 */
public class TextComponent<T extends JTextComponent> extends Component<T> implements ITextComponent<T> {

	public TextComponent(T t) {
		super(t);
	}

	public ITextComponent<T> initalFocus() {
		get().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				get().requestFocusInWindow();
			}
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				get().requestFocusInWindow();
			}
		});
		get().addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent ancestorEvent) {
				get().requestFocusInWindow();
			}
			public void ancestorRemoved(AncestorEvent ancestorEvent) {
				get().requestFocusInWindow();
			}
			public void ancestorMoved(AncestorEvent ancestorEvent) {}
		});
		return this;
	}
}
