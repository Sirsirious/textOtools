package dev.tfduque.textO.NLU.relations;

import java.util.HashSet;
import java.util.Set;

public class Term {

	private String termString;
	private String keyTerm;
	private Set<Relation> relations;

	public Term(String termString, String keyTerm) {
		super();
		this.termString = termString;
		this.keyTerm = keyTerm;
		this.relations = new HashSet<Relation>();
	}

	public String getTermString() {
		return termString;
	}

	public void setTermString(String termString) {
		this.termString = termString;
	}

	public String getKeyTerm() {
		return keyTerm;
	}

	public void setKeyTerm(String keyTerm) {
		this.keyTerm = keyTerm;
	}
	
	public void addRelation (Relation rel) {
		this.relations.add(rel);
	}

	public Set<Relation> getRelations() {
		return relations;
	}

	public void setRelations(Set<Relation> relations) {
		this.relations = relations;
	}

}
