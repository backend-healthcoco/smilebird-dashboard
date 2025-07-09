package com.dpdocter.enums;

public enum DentalAppointmentType {
	ONLINE("ONLINE"), OFFLINE("OFFLINE");

	private String type;

	public String getType() {
		return type;
	}

	private DentalAppointmentType(String type) {
		this.type = type;
	}

}
