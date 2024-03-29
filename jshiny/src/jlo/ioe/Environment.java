package jlo.ioe;

import jlo.ioe.data.DataObject;
import jlo.ioe.data.ObjectService;
import jlo.ioe.data.Vocabulary;
import jlo.ioe.util.FocusOwnerTracker;

/**
 * Copyright � 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 2:12:21 PM<br>
 */
public class Environment {

	private static final Environment _instance = new Environment();
	private FocusOwnerTracker fot;

	public Environment() {
		Vocabulary.load();
		screen = loadScreen();
		fot = new FocusOwnerTracker(screen) {
			public void focusLost() {
				System.out.println("focusLost");
			}
			public void focusGained() {
				System.out.println("focusGained");
			}
		};
		System.out.println("Hello World!");
	}

	// todo: eventually need to support multiple screens
	public Screen loadScreen()
	{
		if (ObjectService.singleton().numScreens() < 1) {
			Screen screen = new Screen("");
			ObjectService.singleton().addScreen(screen);
			return screen;
		} else {
			return ObjectService.singleton().getScreen(0);
		}
	}

	private Screen screen;

	// todo: this indirection is here to handle multiple screens
	public Sheet newSheet(DataObject o) {
		return screen.newSheet(o);
	}
	public void splitSheet(DataObject o) {
		screen.splitSheet(o);
	}
	public void nextSheet() { screen.nextSheet(); }
	public void prevSheet() { screen.prevSheet(); }
	public void commandRequested() { screen.commandRequested(); }

	public static void main(String[] args) {

	}

	public static Environment singleton() {
		return _instance;
	}
}
