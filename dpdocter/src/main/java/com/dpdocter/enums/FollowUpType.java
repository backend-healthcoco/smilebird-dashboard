package com.dpdocter.enums;

public enum FollowUpType {
	
	ONE_MONTH("ONE_MONTH"),THREE_MONTH("THREE_MONTH"),SIX_MONTH("SIX_MONTH");
	
	private String type;

	public String getType() {
		return type;
	}

	private FollowUpType(String type) {
		this.type = type;
	}
}
