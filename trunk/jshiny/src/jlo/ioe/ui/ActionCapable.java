package jlo.ioe.ui;

import java.awt.event.ActionListener;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 2:36:15 PM<br>
 */
public interface ActionCapable {
	void addActionListener(ActionListener e);
	void removeActionListener(ActionListener e);
}
