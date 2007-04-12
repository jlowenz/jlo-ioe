package jlo.ioe.ui;

import javax.swing.text.JTextComponent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 9:20:13 PM<br>
 */
public interface ITextComponent<T extends JTextComponent> extends IComponent<T> {
	ITextComponent<T> initalFocus();
}
