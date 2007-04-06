package jlo.ioe.data;

import jlo.ioe.util.Identifiable;
import jlo.ioe.util.LazyRef;
import jlo.ioe.util.ObjectID;

import java.io.Serializable;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 9:59:22 PM<br>
 */
public class Ref<T extends Identifiable> extends LazyRef<T> implements Serializable {


	@Override
	public boolean isLoaded(ObjectID oid) {
		return false;
	}


}
