package jlo.ioe.util;

import java.rmi.server.UID;
import java.io.Serializable;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 7:07:15 PM<br>
 */
public class ObjectID implements Serializable {
	private UID     uid;
	private Class   clazz;

	public ObjectID(Class clazz) {
		this.clazz = clazz;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ObjectID objectID = (ObjectID) o;

		if (clazz != null ? !clazz.equals(objectID.clazz) : objectID.clazz != null)
			return false;
		if (uid != null ? !uid.equals(objectID.uid) : objectID.uid != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (uid != null ? uid.hashCode() : 0);
		result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
		return result;
	}
}
