package dev.tfduque.textO.NLU.textStructures;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.trie.PatriciaTrie;

import dev.tfduque.textO.NLU.relations.Term;

public class Context {

	private String contextName;
	private Set<Text> texts;
	private PatriciaTrie<Term> terms;

	public Context(String contextName) {
		super();
		this.contextName = contextName;
		this.texts = new HashSet<Text>();
		this.terms = new PatriciaTrie<Term>();
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}
	
	public void addText(Text text) {
		this.texts.add(text);
	}

	public Set<Text> getTexts() {
		return texts;
	}

	public void setTexts(Set<Text> texts) {
		this.texts = texts;
	}

	public void addTerm(Term term) {
		this.terms.put(term.getKeyTerm(), term);
	}
	
	public PatriciaTrie<Term> getTerms() {
		return terms;
	}

	public void setTerms(PatriciaTrie<Term> terms) {
		this.terms = terms;
	}

}
