package com.dpdocter.enums;

public enum GenderSpecialityEnum {
	MEN("MEN"),WOMEN("WOMEN"),BOTH("BOTH");
	
	private String type;

	public String getType() {
		return type;
	}

	private GenderSpecialityEnum(String type) {
		this.type = type;
	}
	
}
