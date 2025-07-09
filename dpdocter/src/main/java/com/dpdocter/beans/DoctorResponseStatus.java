package com.dpdocter.beans;

public enum DoctorResponseStatus {

	INTERESTED("INTERESTED"),NOT_INTERESTED("NOT_INTERESTED"),WAITING("WAITING"),CONVERTED("CONVERTED");
	
	private String type;


	public String getType() {
		return type;
	}

	private DoctorResponseStatus(String type) {
		this.type = type;
	}
	
}
