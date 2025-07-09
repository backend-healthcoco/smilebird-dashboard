package com.dpdocter.enums;

public enum AppointmentCreatedBy {

    DOCTOR("DOCTOR"), PATIENT("PATIENT"),ADMIN("ADMIN");

    private String type;

    private AppointmentCreatedBy(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }
}
