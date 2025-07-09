package com.dpdocter.enums;

public enum DiseaseEnum {

	DIABETES_TYPE_TWO("DIABETES_TYPE_TWO") , PRE_DIABETES("PRE_DIABETES");
	   
	String disease;

	DiseaseEnum(String disease) {
		this.disease = disease;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

}
