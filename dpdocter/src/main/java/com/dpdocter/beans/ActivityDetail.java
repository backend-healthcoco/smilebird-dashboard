package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

public class ActivityDetail {

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
	
	public Language getLanguage() {
		return language;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}
	@Override
	public String toString() {
		return "ActivityDetail [title=" + title + ", shortDescription=" + shortDescription  
				+ ", steps=" + steps + ", materialRequired=" + materialRequired + ", aim=" + aim + "]";
	}
	
	
	
	
}
