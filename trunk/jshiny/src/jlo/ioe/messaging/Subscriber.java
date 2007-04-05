package jlo.ioe.messaging;

import jlo.ioe.util.F;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 6:39:45 PM<br>
 */
public interface Subscriber {
	void subscribe(Object sender, Class msgClass, F.lambda f);
	void subscribe(Class msgClass, F.lambda f);
}
