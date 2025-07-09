package com.dpdocter.enums;

public enum CampaignDeviceType {

	MOBILE("MOBILE"), DESKTOP("DESKTOP");

	private String type;

	private CampaignDeviceType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
