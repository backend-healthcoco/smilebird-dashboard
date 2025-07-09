package com.dpdocter.enums;

public enum LocaleContactStateType {
	
	VERIFIED("VERIFIED"), APPROACH("APPROACH"), INTERESTED("INTERESTED"), NOT_INTERESTED("NOT_INTERESTED"), SIGNED_UP(
			"SIGNED_UP"),CONTACT_LATER("CONTACT_LATER");

	private String type;

	private LocaleContactStateType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
