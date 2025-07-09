package com.dpdocter.enums;

public enum TypeEnum {

	BASIC("BASIC"),INTERMIDIATE("INTERMIDIATE"),ADVANCED("ADVANCED");
	
	private String type;

	public String getType() {
		return type;
	}

	private TypeEnum(String type) {
		this.type = type;
	}

}
