package com.dpdocter.enums;

public enum AppointmentForType {
	SELF("SELF"), OTHER("OTHER");

	private String type;

	public String getType() {
		return type;
	}

	private AppointmentForType(String type) {
		this.type = type;
	}
}
