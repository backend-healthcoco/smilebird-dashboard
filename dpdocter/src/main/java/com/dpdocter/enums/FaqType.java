package com.dpdocter.enums;

public enum FaqType {
	HOME_PAGE("HOME_PAGE");

	private String type;

	private FaqType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
