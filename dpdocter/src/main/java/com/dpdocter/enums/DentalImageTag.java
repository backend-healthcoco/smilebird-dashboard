package com.dpdocter.enums;

public enum DentalImageTag {
	FRONT_TEETH("Front Teeth"), UPPER_JAW("Upper Jaw"), LOWER_JAW("Lower Jaw"), LEFT_SIDE("Left Side"),
	RIGHT_SIDE("Right Side"), FRONT_FACE("Front Face"), SIDE_FACE("Side Face");

	private String type;

	private DentalImageTag(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
