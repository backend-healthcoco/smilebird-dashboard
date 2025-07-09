package com.dpdocter.enums;

public enum Gender {

	MALE("MALE"),FEMALE("FEMALE");
	
	private String type;

	private Gender(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
}
