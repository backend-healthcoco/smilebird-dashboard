package com.dpdocter.enums;

public enum DurationEnum {

	DAILY("DAILY"),WEEKLY("WEEKLY"),MONTHLY("MONTHLY"),YEARLY("YEARLY");
	
	private String type;

	private DurationEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
}
