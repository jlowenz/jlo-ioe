package jlo.ioe.data;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import jlo.ioe.Screen;
import jlo.ioe.messaging.AbstractMessage;
import jlo.ioe.util.F;
import jlo.ioe.util.ObjectID;
import jlo.ioe.util.Opt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 5, 2007<br>
 * Time: 8:53:33 PM<br>
 */
public class ObjectService implements Serializable {
	public static Class Loaded = LoadedMsg.class;

	static class LoadedMsg extends AbstractMessage {
		public LoadedMsg(Object sender) {
			super(sender);
		}
	}

	private static ObjectService _instance = loadObjectService();

	transient private Environment dbEnv;
	private List<Screen> screens = new LinkedList<Screen>();
	private Map<ObjectID, Ref<DataObject>> activeObjects = new HashMap<ObjectID, Ref<DataObject>>();

	private ObjectService(Environment dbEnv) {
		this.dbEnv = dbEnv;
	}

	public static ObjectService singleton() {
		return _instance;
	}

	public boolean isLoaded(ObjectID oid) {
		return activeObjects.containsKey(oid);
	}

	public <T extends DataObject> T load(ObjectID oid) {
		return null;
	}

	public void objectCreated(DataObject o) {
		activeObjects.put(o.getObjectID(), new Ref<DataObject>(o));
	}

	public void addScreen(Screen s) {
		screens.add(s);
	}
	public int numScreens() {
		return screens.size();
	}
	public Screen getScreen(int i) {
		return screens.get(i);
	}

	private static ObjectService loadObjectService() {
		Environment dbEnv;
		try {
			File envDir = new File("./db");
			if (!envDir.exists()) envDir.mkdir();
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setTransactional(true);
			envConfig.setAllowCreate(true);
			dbEnv = new Environment(new File("./db"), envConfig);
			DatabaseConfig dbc = new DatabaseConfig();
			dbc.setAllowCreate(true);
			Database db = dbEnv.openDatabase(null,"ObjectService",dbc);
			final Environment dbEnv1 = dbEnv;
			return (ObjectService)restore(db, "ObjectService".getBytes()).getOrElse(new F.lambda0<Object>() {
				protected Object code() {
					return new ObjectService(dbEnv1);
				}
			});
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		System.err.println("Failed to load object service. Terminating...");
		System.exit(-1);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Opt<T> restore(Database db, byte[] key) {
		DatabaseEntry val = new DatabaseEntry();
		try {
			if (db.get(null, new DatabaseEntry(key), val, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				ByteArrayInputStream bytes = new ByteArrayInputStream(val.getData());
				ObjectInputStream in = new ObjectInputStream(bytes);
				return Opt.some((T)in.readObject());
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Opt.none();
	}


}
