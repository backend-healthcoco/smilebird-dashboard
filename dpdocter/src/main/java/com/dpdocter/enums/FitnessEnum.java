package com.dpdocter.enums;

public enum FitnessEnum {
	
	YOGA("YOGA"),PRANAYAMA("PRANAYAMA"),KRIYA("KRIYA"),BANDHA("BANDHA"),DHARNA("DHARNA"),DHYANA("DHYANA");
	
	private String fitnessType;

	private FitnessEnum(String fitnessType) {
		this.fitnessType = fitnessType;
	}

	public String getFitnessType() {
		return fitnessType;
	}


}
