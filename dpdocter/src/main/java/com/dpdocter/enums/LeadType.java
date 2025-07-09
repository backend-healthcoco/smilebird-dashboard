package com.dpdocter.enums;

public enum LeadType {
	GENUINE("GENUINE"), INVALID("INVALID"),HOT_LEAD("HOT_LEAD"),CONVERTED("CONVERTED"),
	NOT_INTERESTED("NOT_INTERESTED");

	private String type;

	public String getType() {
		return type;
	}

	private LeadType(String type) {
		this.type = type;
	}

}
