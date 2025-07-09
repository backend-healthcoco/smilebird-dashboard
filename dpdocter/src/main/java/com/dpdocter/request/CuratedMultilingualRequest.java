package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Language;
import com.dpdocter.beans.YogaSession;

public class CuratedMultilingualRequest {

	private String name;
	
	private String shortDescription;
	
	private Language language;
	
	private String languageId;

	

	
	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
