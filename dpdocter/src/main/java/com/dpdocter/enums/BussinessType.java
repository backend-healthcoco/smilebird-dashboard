package com.dpdocter.enums;

public enum BussinessType {
LLP("LLP"),NGO("NGO"),OTHERS("OTHERS"),INDIVIDUAL("INDIVIDUAL"),PARTNERSHIP("PARTNERSHIP"),PROPRIETORSHIP("PROPRIETORSHIP"),PUBLIC_LIMITED("PUBLIC_LIMITED"), PRIVATE_LIMITED("PRIVATE_LIMITED"), TRUST("TRUST"),
SOCIETY("SOCIETY"), NOT_YET_REGISTERED("NOT_YET_REGISTERED"),EDUCATIONAL_INSTITUES("EDUCATIONAL_INSTITUTES");
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private BussinessType(String type) {
		this.type = type;
	}
	
	
}
