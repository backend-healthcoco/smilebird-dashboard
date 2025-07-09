package com.dpdocter.enums;

public enum DeviceType {

	ANDROID("ANDROID"), IOS("IOS"), WINDOWS("WINDOWS"), IPAD("IPAD") ,WEB_ADMIN("WEB_ADMIN"), WEB("WEB"),ANDROID_PAD("ANDROID_PAD");
	
	private String type;

	private DeviceType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
