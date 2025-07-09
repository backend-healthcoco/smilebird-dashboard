package com.dpdocter.enums;

public enum ExcerciseType {
	
	CONTINOUS("CONTINOUS"), LIMITED("LIMITED"), CONTINOUS_SPORTS("CONTINOUS_SPORTS");
	
	private String type;

	public String getType() {
		return type;
	}

	private ExcerciseType(String type) {
		this.type = type;
	}

}
