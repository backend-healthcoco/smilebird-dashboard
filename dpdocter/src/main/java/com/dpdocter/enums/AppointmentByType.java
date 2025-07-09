package com.dpdocter.enums;

public enum AppointmentByType {
	CALL("CALL"), WEBSITE("WEBSITE");

	private String type;

	public String getType() {
		return type;
	}

	private AppointmentByType(String type) {
		this.type = type;
	}
}
