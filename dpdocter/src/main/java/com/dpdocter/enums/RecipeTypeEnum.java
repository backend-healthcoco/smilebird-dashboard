package com.dpdocter.enums;

public enum RecipeTypeEnum {
	
	VEG("VEG"),NON_VEG("NON_VEG"),EGG("EGG"),VEGAN("VEGAN");
	
	private String type;
	

	public String getType() {
		return type;
	}

	private RecipeTypeEnum(String type) {
		this.type = type;
	}
	
}
