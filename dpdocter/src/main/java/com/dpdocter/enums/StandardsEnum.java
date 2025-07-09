package com.dpdocter.enums;

public enum StandardsEnum {
FIRST("FIRST"),SECOND("SECOND"),THIRD("THIRD"),FOURTH("FOURTH"),FIFTH("FIFTH"),SIXTH("SIXTH"),SEVENTH("SEVENTH"),EIGHTH("EIGHTH"),NINE("NINTH"),TENTH("TENTH");
	
	private String standard;

	public String getStandards() {
		return standard;
	}

	private StandardsEnum(String standard) {
		this.standard = standard;
	}

}
