package com.dpdocter.response;

public class LeadTypeCountResponse {
	private Integer hot = 0;
	private Integer genuine = 0;
	private Integer converted = 0;
	private Integer invalid = 0;
	private Integer notInterested = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}

	public Integer getHot() {
		return hot;
	}

	public void setHot(Integer hot) {
		this.hot = hot;
	}

	public Integer getGenuine() {
		return genuine;
	}

	public void setGenuine(Integer genuine) {
		this.genuine = genuine;
	}

	public Integer getConverted() {
		return converted;
	}

	public void setConverted(Integer converted) {
		this.converted = converted;
	}

	public Integer getInvalid() {
		return invalid;
	}

	public void setInvalid(Integer invalid) {
		this.invalid = invalid;
	}

	public Integer getNotInterested() {
		return notInterested;
	}

	public void setNotInterested(Integer notInterested) {
		this.notInterested = notInterested;
	}

}
