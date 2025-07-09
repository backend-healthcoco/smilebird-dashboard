package com.dpdocter.enums;

public enum CampaignRunningAt {
	META("META"), GOOGLE_ADS("GOOGLE_ADS"), YOUTUBE("YOUTUBE");

	private String type;

	private CampaignRunningAt(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
