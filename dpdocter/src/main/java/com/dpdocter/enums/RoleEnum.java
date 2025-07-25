package com.dpdocter.enums;

public enum RoleEnum {
	SUPER_ADMIN("SUPER_ADMIN"), ADMIN("ADMIN"), CLINIC_HEAD("CLINIC_HEAD"), DOCTOR("DOCTOR"), FINANCE("FINANCE"),
	MARKETING("MARKETING"), VIEWER("VIEWER");

	private String role;

	private RoleEnum(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

}
