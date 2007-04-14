package jlo.ioe;

import jlo.ioe.data.VocabularyTerm;
import jlo.ioe.util.Opt;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 12, 2007<br>
 * Time: 2:41:46 PM<br>
 */
public class Command {
	private String fragment;
	private Opt<VocabularyTerm> verb = Opt.none();
	private Opt<VocabularyTerm> noun = Opt.none();

	public void termCompleted(List<VocabularyTerm> suggestions) {

	}

	public void execute() {
		
	}

	public void updateFragment(String txt) {
		fragment = txt;
	}

	public List<VocabularyTerm> suggestions(String lastFragment) {
		return new LinkedList<VocabularyTerm>();
	}
}
