package com.dpdocter.enums;

public enum PlanStatus {
	ONGOING("ONGOING"), COMPLETE("COMPLETE");

	private String type;

	public String getType() {
		return type;
	}

	private PlanStatus(String type) {
		this.type = type;
	}

}
