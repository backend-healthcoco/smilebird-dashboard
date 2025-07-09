package com.dpdocter.enums;

public enum LocationState {
	NOTVERIFIED("NOTVERIFIED"), NOTACTIVATED("NOTACTIVATED");

	String state;

	public String getState() {
		return state;
	}

	private LocationState(String state) {
		this.state = state;
	}

}
