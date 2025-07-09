package com.dpdocter.beans;

public class YogMultilingual {

	private String yogaName;
	
	private String alternateYogaName;
	
	private String benefits;
	
	private String beCareful;
	
	private Language language;
	
	private String languageId;

	

	public String getYogaName() {
		return yogaName;
	}

	public void setYogaName(String yogaName) {
		this.yogaName = yogaName;
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
	
	

	public String getAlternateYogaName() {
		return alternateYogaName;
	}

	public void setAlternateYogaName(String alternateYogaName) {
		this.alternateYogaName = alternateYogaName;
	}

	@Override
	public String toString() {
		return "YogMultilingual [yogaName=" + yogaName + ", benefits=" + benefits + ", beCareful=" + beCareful + ", language="
				+ language + ", languageId=" + languageId + "]";
	}
	
	
	
}
