package com.dpdocter.enums;

public enum EntityType {
	SCHOOL("SCHOOL"),COMPANY("COMPANY"),HOSPITAL("HOSPITAL"),SOCIETY("SOCIETY");
	
	private String type;

	public String getType() {
		return type;
	}

	private EntityType(String type) {
		this.type = type;
	}

	
	
}
