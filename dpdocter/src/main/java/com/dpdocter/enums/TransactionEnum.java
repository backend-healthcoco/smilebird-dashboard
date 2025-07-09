package com.dpdocter.enums;

public enum TransactionEnum {

	CREDIT("CREDIT"),DEBIT("DEBIT");
	
	private String type;

	public String getType() {
		return type;
	}

	private TransactionEnum(String type) {
		this.type = type;
	}
	
	
	
	
}
