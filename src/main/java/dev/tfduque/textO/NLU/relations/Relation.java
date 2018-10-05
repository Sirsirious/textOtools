package dev.tfduque.textO.NLU.relations;

public class Relation {

	private String relatioName;
	private Term targetTerm;

	public Relation(String relatioName, Term targetTerm) {
		super();
		this.relatioName = relatioName;
		this.targetTerm = targetTerm;
	}

	public String getRelatioName() {
		return relatioName;
	}

	public void setRelatioName(String relatioName) {
		this.relatioName = relatioName;
	}

	public Term getTargetTerm() {
		return targetTerm;
	}

	public void setTargetTerm(Term targetTerm) {
		this.targetTerm = targetTerm;
	}

}
