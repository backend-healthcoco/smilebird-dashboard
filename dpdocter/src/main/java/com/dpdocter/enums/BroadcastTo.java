package com.dpdocter.enums;

public enum BroadcastTo {
	DOCTOR("DOCTOR"), LEADS("LEADS"), PATIENT("PATIENT");

	private String type;

	private BroadcastTo(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
