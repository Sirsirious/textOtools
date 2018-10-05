package dev.tfduque.textO.NLU.textStructures;

import java.util.HashSet;
import java.util.Set;

import dev.tfduque.textO.NLU.relations.Term;

public class Text {

	private String textName;
	private StringBuffer content;
	private Set<Term> terms;

	public Text(String textName) {
		super();
		this.textName = textName;
		this.terms = new HashSet<Term>();
		this.content = new StringBuffer();
	}
	
	public Text(String textName, String content) {
		super();
		this.textName = textName;
		this.terms = new HashSet<Term>();
		this.content = new StringBuffer(content);
	}
	
	public Text(String textName, StringBuffer sb) {
		super();
		this.textName = textName;
		this.terms = new HashSet<Term>();
		this.content = sb;
	}

	public String getTextName() {
		return textName;
	}

	public void setTextName(String textName) {
		this.textName = textName;
	}

	public Set<Term> getTerms() {
		return terms;
	}

	public void setTerms(Set<Term> terms) {
		this.terms = terms;
	}

	public StringBuffer getContent() {
		return content;
	}

	public void setContent(StringBuffer content) {
		this.content = content;
	}
	

}
