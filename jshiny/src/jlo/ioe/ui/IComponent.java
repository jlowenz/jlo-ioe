package jlo.ioe.ui;

import javax.swing.JComponent;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 4:31:50 PM<br>
 */
public interface IComponent<T extends JComponent> {
	T get();
	<V> void update(V val);
	double preferredWidth();
	double preferredHeight();
	IComponent<T> preferredWidth(double v);
	IComponent<T> preferredHeight(double v);
	IComponent<T> preferredSize(double w, double h);
	IComponent<T> setWidth(double w);
	IComponent<T> setHeight(double h);
	ActionsHandler actions();
}
