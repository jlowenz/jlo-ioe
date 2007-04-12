package jlo.ioe;

import jlo.ioe.messaging.SubscriberDelegate;
import jlo.ioe.ui.Button;
import jlo.ioe.ui.Panel;
import jlo.ioe.util.F;
import jlo.ioe.util.Identifiable;
import jlo.ioe.util.ObjectID;
import jlo.ioe.util.Opt;

import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.Serializable;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 11:30:00 PM<br>
 */
public class SheetSelector extends Panel {
	Opt<SheetButton> sheets = Opt.none();
	Opt<SheetButton> current = Opt.none();


	public SheetSelector(int width)
	{
		setBackground(Color.white);
		setMinimumSize(new Dimension(width,27));
		setLayout(new GridLayout(1,0,0,0));
	}

	public Sheet getCurrentSheet() {
		return current.get(null).sheet();
	}

	public void newSheet(final Sheet aSheet) {
		sheets.match(
				new F.lambda1<Object,SheetButton>(){protected Object code(SheetButton s) {
					SheetButton btn = new SheetButton(aSheet);
					btn.prev = s.prev;
					btn.next = s;
					s.prev.next = btn;
					s.prev = btn;
					current = Opt.some(s.prev);
					hideOthers(current.get(null));
					add(current.get(null));
					validate();
					repaint(); return null;
				}},
				new F.lambda1<Object,SheetButton>(){protected Object code(SheetButton p) {
					sheets = Opt.some(new SheetButton(aSheet));
					current = sheets;
					add(sheets.get(null));
					validate();
					repaint(); return null;
				}});
	}

	public void removeSheet() {
		current.match(
				new F.lambda1<Object,SheetButton>(){protected Object code(SheetButton s) {
					s.prev.next = s.next;
					s.next.prev = s.prev;
					validate();
					repaint(); return null;
				}},
				new F.lambda1<Object, SheetButton>(){protected Object code(SheetButton p) {
					return null;
				}});
	}

	public Sheet nextSheet() {
		return current.match(
				new F.lambda1<Sheet,SheetButton>(){protected Sheet code(SheetButton p) {
					current = Opt.some(p.next);
					current.get(null).select(true);
					hideOthers(current.get(null));
					return current.get(null).sheet.display();
				}},
				new F.lambda1<Sheet, SheetButton>(){protected Sheet code(SheetButton p) {
					return null;
				}});
	}

	public Sheet prevSheet() {
		return current.match(
				new F.lambda1<Sheet,SheetButton>(){protected Sheet code(SheetButton p) {
					current = Opt.some(p.prev);
					current.get(null).select(true);
					hideOthers(current.get(null));
					return current.get(null).sheet.display();
				}},
				new F.lambda1<Sheet, SheetButton>(){protected Sheet code(SheetButton p) {
					return null;
				}});
	}

	private void hideOthers(SheetButton shown) {
		SheetButton curr = shown.next;
		while (curr != shown) {
			curr.select(false);
			curr = curr.next;
		}
	}

	class SheetButton extends Button implements Serializable, Identifiable {
		public SheetButton prev;
		public SheetButton next;
		public Sheet sheet;
		private SubscriberDelegate subscriber = new SubscriberDelegate();
		private ObjectID oid = new ObjectID(getClass());

		public SheetButton(Sheet aSheet) {
			super(aSheet.getTitle());
			sheet = aSheet;
			setBorder(new EmptyBorder(1,1,1,1));
			setBorderPainted(false);
			preferredWidth(10000);
			subscriber.subscribe(this, Button.Pressed, new F.lambda0<Sheet>(){protected Sheet code() {
				hideOthers(SheetButton.this);
				select(true);
				return sheet.display();
			}});
			subscriber.subscribe(sheet, Sheet.TitleChanged, new F.lambda1<Object,String>(){protected Object code(String p) {
				setText(p); return null;
			}});
		}


		public ObjectID getObjectID() {
			return oid;
		}

		public Sheet sheet() {
			return null;
		}

		public void select(boolean b) {

		}
	}
}
