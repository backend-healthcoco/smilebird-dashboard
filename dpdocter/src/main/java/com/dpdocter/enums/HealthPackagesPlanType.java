package com.dpdocter.enums;

public enum HealthPackagesPlanType {
	
	 PHYSIOTHERAPY("PHYSIOTHERAPY"), ALL("ALL");

	private String type;

	public String getType() {
		return type;
	}

	private HealthPackagesPlanType(String type) {
		this.type = type;
	}


}
