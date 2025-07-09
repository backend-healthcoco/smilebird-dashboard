package com.dpdocter.enums;

public enum FollowupStatus {
	STRONG("STRONG"), WEAK("WEAK"), MODERATE("MODERATE");

	private String type;

	public String getType() {
		return type;
	}

	private FollowupStatus(String type) {
		this.type = type;
	}
}
