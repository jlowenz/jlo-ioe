package jlo.ioe.ui;

import javax.swing.JComponent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 3:30:43 PM<br>
 */
public interface Wrapped<T extends JComponent> {
	Component<T> wrapped();
}
