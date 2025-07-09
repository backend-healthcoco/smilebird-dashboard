package com.dpdocter.enums;

public enum SmsRoute {

	OTP("OTP"),MKT("MKT"),TXN("TXN"),BULK("BULK");
	
	private String type;

	private SmsRoute(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
	
}
