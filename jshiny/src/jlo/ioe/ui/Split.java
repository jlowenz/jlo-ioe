package jlo.ioe.ui;

import jlo.ioe.util.F;
import jlo.ioe.util.Opt;

import java.io.Serializable;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 7, 2007<br>
 * Time: 7:35:15 PM<br>
 */
public class Split<T> implements Serializable {
	public SplitType kind;
	public Aspect aspect;
	public Opt<Split<T>> first;
	public Opt<Split<T>> second;

	public Opt<T> obj;
	public F.lambda1<IComponent,T> comp;
	private double weight;


	public Split(Opt<T> obj, Aspect aspect, double weight, F.lambda1<IComponent,T> comp) {
		this.obj = obj;
		this.aspect = aspect;
		this.weight = weight;
		this.comp = comp;
	}

	public IComponent component() {
		return comp.call(obj.get((T)null));
	}
	public double area() {
		return obj.match(
				new F.lambda1<Double,T>(){protected Double code(T o) {
					return (double)(component().get().getWidth() * component().get().getHeight());
				}},
				new F.lambda1<Double,T>(){protected Double code(T o) {
					return 0.0;
				}});
	}

	public void verticalDivider(T a, T b) {
		obj = Opt.none();
		first = Opt.some(new Split<T>(Opt.some(a),Aspect.Vertical,0.5,comp));
		second = Opt.some(new Split<T>(Opt.some(b),Aspect.Vertical,0.5,comp));
		kind = SplitType.Vertical;
	}
	public void horizontalDivider(T a, T b) {
		obj = Opt.none();
		first = Opt.some(new Split<T>(Opt.some(a),Aspect.Horizontal,0.5,comp));
		second = Opt.some(new Split<T>(Opt.some(b),Aspect.Horizontal,0.5,comp));
		kind = SplitType.Horizontal;
	}
}
