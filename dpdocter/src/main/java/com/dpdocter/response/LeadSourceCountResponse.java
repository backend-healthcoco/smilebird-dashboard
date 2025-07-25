package com.dpdocter.response;

public class LeadSourceCountResponse {
	private Integer directCall = 0;
	private Integer facebook = 0;
	private Integer instagram = 0;
	private Integer whatsapp = 0;
	private Integer googleAds = 0;
	private Integer website = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}

	public Integer getDirectCall() {
		return directCall;
	}

	public void setDirectCall(Integer directCall) {
		this.directCall = directCall;
	}

	public Integer getFacebook() {
		return facebook;
	}

	public void setFacebook(Integer facebook) {
		this.facebook = facebook;
	}

	public Integer getInstagram() {
		return instagram;
	}

	public void setInstagram(Integer instagram) {
		this.instagram = instagram;
	}

	public Integer getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(Integer whatsapp) {
		this.whatsapp = whatsapp;
	}

	public Integer getGoogleAds() {
		return googleAds;
	}

	public void setGoogleAds(Integer googleAds) {
		this.googleAds = googleAds;
	}

	public Integer getWebsite() {
		return website;
	}

	public void setWebsite(Integer website) {
		this.website = website;
	}

}
