package com.dpdocter.beans;

public class Code {

	private String genericCode;
	
	private String reaction;
	
	private String explanation;

	public Code() {
		super();
	}

	public Code(String genericCode, String reaction, String explanation) {
		super();
		this.genericCode = genericCode;
		this.reaction = reaction;
		this.explanation = explanation;
	}

	public String getGenericCode() {
		return genericCode;
	}

	public void setGenericCode(String genericCode) {
		this.genericCode = genericCode;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "Code [genericCode=" + genericCode + ", reaction=" + reaction + ", explanation=" + explanation + "]";
	}
}
