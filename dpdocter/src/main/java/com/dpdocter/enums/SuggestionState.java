package com.dpdocter.enums;

public enum SuggestionState {
	PENDING("PENDING"), ACCEPTED("ACCEPTED"), CANCELLED("CANCELLED");

	String state;

	public String getState() {
		return state;
	}

	 SuggestionState(String state) {
		this.state = state;
	}

}
