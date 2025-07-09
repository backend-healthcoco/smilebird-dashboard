package com.dpdocter.enums;

public enum BroadcastType {
	SMS("SMS"), WHATSAPP("WHATSAPP");

	private String type;

	private BroadcastType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
