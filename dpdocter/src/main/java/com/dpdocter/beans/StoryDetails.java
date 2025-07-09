package com.dpdocter.beans;



public class StoryDetails {

	private String title;
	
	private String shortDescription;
	
	private String moral;
	
	private String languageId;
	
	private Language language;
	
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
	public String getMoral() {
		return moral;
	}
	public void setMoral(String moral) {
		this.moral = moral;
	}
	
	public String getLanguageId() {
		return languageId;
	}
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}
	public Language getLanguage() {
		return language;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}
	@Override
	public String toString() {
		return "StoryDetails [title=" + title  + ", moral=" + moral + ", language="
			+ "]";
	}
	
	
}
