package com.dpdocter.response;

public class AppointmentForCountResponse {
	private Integer self = 0;
	private Integer other = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}
	
	public Integer getSelf() {
		return self;
	}

	public void setSelf(Integer self) {
		this.self = self;
	}

	public Integer getOther() {
		return other;
	}

	public void setOther(Integer other) {
		this.other = other;
	}

}
