package com.dpdocter.enums;

public enum CampaignStatus {
	ACTIVE("ACTIVE"), DEACTIVE("DEACTIVE"), HOLD("HOLD");

	private String type;

	private CampaignStatus(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
