package jlo.ioe.data;

import jlo.ioe.View;
import jlo.ioe.data.field.Field;
import jlo.ioe.messaging.AbstractMessage;
import jlo.ioe.messaging.MessageService;
import jlo.ioe.messaging.SubscriberDelegate;
import jlo.ioe.util.F;
import jlo.ioe.util.Identifiable;
import jlo.ioe.util.LazyRef;
import jlo.ioe.util.ObjectID;
import jlo.ioe.util.Opt;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Apr 4, 2007
 * Time: 6:20:44 PM
 */
public abstract class DataObject implements Identifiable {
	public static final String kOwner = "owner";
	public static final String kCreated = "created";
	public static final String kModified = "modified";
	public static final String kAccessed = "accessed";
	public static final String kModifier = "modifier";
	public static final String kShared = "shared";
	public static final String kKind = "kind";
	public static final String kTitle = "title";
	public static final String kDescription = "description";
	public static final List<String> kBaseMetadata = Arrays.asList(kOwner, kCreated, kModified, kAccessed, kModifier, kShared, kKind, kTitle, kDescription);


	public static final Class<ModifiedMsg> Modified = ModifiedMsg.class;

	public abstract View getDefaultView();

	static class ModifiedMsg extends AbstractMessage {
		public ModifiedMsg( Object sender) {
			super(sender);
		}
	}

	private ObjectID oid = new ObjectID(this.getClass());
	private Map<String,Object> metadata = new HashMap<String, Object>();
	private Map<String, Field> instanceFields = new HashMap<String, Field>();
	private SubscriberDelegate messenger = new SubscriberDelegate();

	protected DataObject() {
		meta(kOwner, "a user");
		meta(kCreated, new Date());
		meta(kModified, meta(kCreated).get(null));
		meta(kAccessed, meta(kCreated).get(null));
		meta(kModifier, "a user");
		meta(kShared, false);
		meta(kKind, kind());
	}

	protected void meta(String key, Object val) {
		metadata.put(key, val);
	}
	@SuppressWarnings("unchecked")
	public <T> Opt<T> meta(String key) {
		Object o = metadata.get(key);
		return (o != null) ? Opt.some((T)o) : Opt.<T>none();
	}
	public ObjectID getObjectID() { return oid; }
	public <T extends DataObject> LazyRef<T> getRef() { return null; }

	abstract Object kind();
	abstract Object view();

	protected void addField(String name, Field f) {
		instanceFields.put(name, f);
		messenger.subscribe(getRef(),Field.Changed,new F.lambda0<Object>(){protected Object code() {
		    meta(kModified,new Date());
			return MessageService.singleton().publish(new ModifiedMsg(DataObject.this));
		}});
	}


	@Override
	public String toString() {
		return kind().toString() + "(" + ((Date)meta(kCreated).get(null)) + ")";
	}
}
