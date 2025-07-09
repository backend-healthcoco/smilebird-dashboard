package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class YogaClassesMultilingual {

	private String yogaClassesName;
	
	private String benefits;
	
	private String beCareful;
	
	private String shortDescription;

	private Language language;
	
	private String languageId;

	public String getYogaClassesName() {
		return yogaClassesName;
	}

	public void setYogaClassesName(String yogaClassesName) {
		this.yogaClassesName = yogaClassesName;
	}

	public String getBenefits() {
		return benefits;
	}

	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}

	public String getBeCareful() {
		return beCareful;
	}

	public void setBeCareful(String beCareful) {
		this.beCareful = beCareful;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	
	
	
	
	
}
