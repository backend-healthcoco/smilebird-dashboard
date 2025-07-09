package com.dpdocter.enums;

public enum FacilityType {

	HIP("HIP"),HIU("HIU");
	
	private String type;

	public String getType() {
		return type;
	}

	private FacilityType(String type) {
		this.type = type;
	}
	
	
}
