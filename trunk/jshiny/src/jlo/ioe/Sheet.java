package jlo.ioe;

import jlo.ioe.data.DataObject;
import jlo.ioe.data.Ref;
import jlo.ioe.messaging.AbstractMessageWithParams;
import jlo.ioe.messaging.MessageService;
import jlo.ioe.messaging.SubscriberDelegate;
import jlo.ioe.ui.Aspect;
import jlo.ioe.ui.IComponent;
import jlo.ioe.ui.Panel;
import jlo.ioe.ui.Split;
import jlo.ioe.ui.Splitter;
import jlo.ioe.util.F;
import jlo.ioe.util.Identifiable;
import jlo.ioe.util.ObjectID;
import jlo.ioe.util.Opt;
import jlo.ioe.util.Tuple;
import jlo.ioe.util.Util;

import javax.swing.border.EmptyBorder;
import java.util.Arrays;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 2:25:30 PM<br>
 */
public class Sheet extends Panel implements Identifiable {
	public static Class TitleChanged = TitleChangedMsg.class;

	class TitleChangedMsg extends AbstractMessageWithParams {
		public TitleChangedMsg(Object sender, Tuple.One<String> title) {
			super(sender, title);
		}
	}

	public String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
		MessageService.singleton().publish(new TitleChangedMsg(Sheet.this, Tuple.one(title)));
	}

	private String title;
	private ObjectID oid = new ObjectID(getClass());
	private Screen screen;
	private DataObject obj;
	private Split<Ref<DataObject>> splits;
	private Splitter splitPane;
	private SubscriberDelegate subscriber = new SubscriberDelegate();


	public Sheet(Screen screen, final DataObject obj) {
		this.screen = screen;
		this.obj = obj;
		splits = new Split<Ref<DataObject>>(Opt.some(new Ref<DataObject>(obj)), Aspect.Horizontal, 1.0, new F.lambda1<IComponent, Ref<DataObject>>(){protected View code(Ref<DataObject> p) {
			return p.get().getDefaultView();
		}});
		splitPane = new Splitter();
		add(splitPane);
		splitPane.resplit(splits);
		setBorder(new EmptyBorder(2,2,2,2));
		subscriber.subscribe(
				new Ref<DataObject>(obj),
				DataObject.Modified,
				DataObject.kTitle,
				new F.lambda0<Object>(){protected Object code() {
					setTitle(obj.meta(DataObject.kTitle).get(""));
					return null;
				}});
	}

	public Sheet display() {
		screen.display(this);
		return this;
	}

	public void split(DataObject obj) {
		Split<Ref<DataObject>> max = findSplit(splits, splits);
		switch (max.aspect) {
			case Horizontal:
				max.verticalDivider(max.obj.get(null), new Ref<DataObject>(obj));
				break;
			case Vertical:
				max.horizontalDivider(max.obj.get(null), new Ref<DataObject>(obj));
				break;
		}
		splitPane.resplit(splits);
	}

	private Split<Ref<DataObject>> findSplit(Split<Ref<DataObject>> max, Split<Ref<DataObject>> s) {
		switch (s.kind) {
			case NoSplit: return (s.area() >= max.area()) ? s : max;
			default:
				return Util.argmax(new F.lambda2<Split<Ref<DataObject>>,Split<Ref<DataObject>>,Split<Ref<DataObject>>>(){protected Split<Ref<DataObject>> code(Split<Ref<DataObject>> t, Split<Ref<DataObject>> u) {
					return (t.area() > u.area()) ? t : u;
				}}, Arrays.asList(findSplit(max, s.first.get(null)), findSplit(max, s.second.get(null))));
		}
	}

	public ObjectID getObjectID() {
		return oid;
	}
}
