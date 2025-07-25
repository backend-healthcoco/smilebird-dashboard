package com.dpdocter.response;

public class AppointmentTypeCountResponse {
	private Integer online = 0;
	private Integer offline = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}
	public Integer getOnline() {
		return online;
	}

	public void setOnline(Integer online) {
		this.online = online;
	}

	public Integer getOffline() {
		return offline;
	}

	public void setOffline(Integer offline) {
		this.offline = offline;
	}

}
