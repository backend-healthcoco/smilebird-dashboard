package com.dpdocter.enums;

public enum ReasonType {
	LEAD_TYPE("LEAD_TYPE");

	private String type;

	public String getType() {
		return type;
	}

	private ReasonType(String type) {
		this.type = type;
	}

}
