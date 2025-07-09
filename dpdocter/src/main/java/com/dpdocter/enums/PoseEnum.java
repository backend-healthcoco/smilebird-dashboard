package com.dpdocter.enums;

public enum PoseEnum {

	STANDING("STANDING"), SEATED("SEATED"),LYING("LYING"),KNEELING("KNEELING"),ARM_BALANCE("ARM_BALANCE");

	private String poseType;

	private PoseEnum(String poseType) {
		this.poseType = poseType;
	}

	public String getPoseType() {
		return poseType;
	}
	
	
}
