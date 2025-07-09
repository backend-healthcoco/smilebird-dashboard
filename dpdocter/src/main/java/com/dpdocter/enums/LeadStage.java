package com.dpdocter.enums;

public enum LeadStage {
	
	INITIAL_CALL("INITIAL_CALL"), CLINIC_VISIT("CLINIC_VISIT"),CONVERTED("CONVERTED");
	
	private String type;

	public String getType() {
		return type;
	}

	private LeadStage(String type) {
		this.type = type;
	}

}
