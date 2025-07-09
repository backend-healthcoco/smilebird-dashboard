package com.dpdocter.beans;

import java.util.List;

public class MindfulnessDetail {

	private String title;
	
	private String shortDescription;
	
	private Language language;
	
	private String languageId;
	
	private List<Step> steps;
	
	private String materialRequired;
	
	private String aim;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public String getMaterialRequired() {
		return materialRequired;
	}

	public void setMaterialRequired(String materialRequired) {
		this.materialRequired = materialRequired;
	}

	public String getAim() {
		return aim;
	}

	public void setAim(String aim) {
		this.aim = aim;
	}

	@Override
	public String toString() {
		return "MindfulnessDetail [title=" + title + ", shortDescription=" + shortDescription + ", language=" + language
				+ ", languageId=" + languageId + ", steps=" + steps + ", materialRequired=" + materialRequired
				+ ", aim=" + aim + "]";
	}
	
	
}
