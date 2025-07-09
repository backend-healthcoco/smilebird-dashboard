package com.dpdocter.enums;

public enum FollowupType {

	CALL("CALL"), CHAT("CHAT"), WHATSAPP("WHATSAPP");

	private String type;

	public String getType() {
		return type;
	}

	private FollowupType(String type) {
		this.type = type;
	}

}
