package jlo.ioe.util;

import java.io.Serializable;

/**
 * Copyright � 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 8:14:21 AM<br>
 */
public interface Identifiable extends Serializable {
	ObjectID getObjectID();
}
