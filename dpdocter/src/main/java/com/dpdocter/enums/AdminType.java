package com.dpdocter.enums;

public enum AdminType {
	ADMIN("ADMIN"), MARKETING("MARKETING"), DIGITAL_MARKETING("DIGITAL_MARKETING"), DM_MARKETING("DM_MARKETING"),
	QA("QA"), DEV("DEV"), SEMI_ADMIN("SEMI_ADMIN"), SMILEBIRD("SMILEBIRD"), SUPER_ADMIN("SUPER_ADMIN"),
	CLINIC_HEAD("CLINIC_HEAD"), DOCTOR("DOCTOR"), FINANCE("FINANCE"), VIEWER("VIEWER"),NUTRITION("NUTRITION");

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private AdminType(String type) {
		this.type = type;
	}

}
