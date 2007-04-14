package jlo.ioe.data;

import jlo.ioe.util.LazyRef;
import jlo.ioe.util.ObjectID;
import jlo.ioe.util.Opt;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 6, 2007<br>
 * Time: 5:42:51 PM<br>
 */
public class Ref<T extends DataObject> extends LazyRef<T> {
	public Ref(T object) {
		super(object);
	}

	public Ref(ObjectID oid) {
		super(oid);
	}

	@Override
	protected boolean isLoaded(ObjectID oid) {
		return ObjectService.singleton().isLoaded(oid);
	}

	@Override
	protected Opt<T> loadObject(ObjectID oid) {
		T o = ObjectService.singleton().<T>load(oid); // WTF???
		return Opt.some(o);
	}
}
