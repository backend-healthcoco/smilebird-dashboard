package com.dpdocter.enums;

public enum LevelEnum {

	BASIC("BASIC"),INTERMEDIATE("INTERMEDIATE"),ADVANCED("ADVANCED"),TEACHER("TEACHER");
	
	private String level;

	private LevelEnum(String level) {
		this.level = level;
	}

	public String getLevel() {
		return level;
	}
	
	
	
	
}
