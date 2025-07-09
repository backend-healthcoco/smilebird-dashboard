package com.dpdocter.enums;

public enum DoctorContactStateType {

	VERIFIED("VERIFIED"), APPROACH("APPROACH"), INTERESTED("INTERESTED"), NOT_INTERESTED("NOT_INTERESTED"), SIGNED_UP(
			"SIGNED_UP"),CONTACT_LATER("CONTACT_LATER");

	private String type;

	private DoctorContactStateType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
