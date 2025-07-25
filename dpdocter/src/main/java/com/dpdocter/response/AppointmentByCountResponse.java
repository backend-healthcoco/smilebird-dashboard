package com.dpdocter.response;

public class AppointmentByCountResponse {
	private Integer call = 0;
	private Integer website = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}

	public Integer getCall() {
		return call;
	}

	public void setCall(Integer call) {
		this.call = call;
	}

	public Integer getWebsite() {
		return website;
	}

	public void setWebsite(Integer website) {
		this.website = website;
	}

}
