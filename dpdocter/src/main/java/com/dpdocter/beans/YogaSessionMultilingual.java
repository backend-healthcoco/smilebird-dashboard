package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class YogaSessionMultilingual {

	private String yogaSessionName;

	private String shortDescription;
	
	private Language language;
	
	private String languageId;

	public String getYogaSessionName() {
		return yogaSessionName;
	}

	public void setYogaSessionName(String yogaSessionName) {
		this.yogaSessionName = yogaSessionName;
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
