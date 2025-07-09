package com.dpdocter.enums;

public enum ConfexUserState {
	ADMIN("ADMIN"), STAFF("STAFF"), USER("USER"),TEAM_MEMBER("TEAM_MEMBER"),EMPLOYEE("EMPLOEE");

	String state;

	public String getState() {
		return state;
	}

	private ConfexUserState(String state) {
		this.state = state;
	}

}
