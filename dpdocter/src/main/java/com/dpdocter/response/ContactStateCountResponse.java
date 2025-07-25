package com.dpdocter.response;

public class ContactStateCountResponse {
	private Integer firstCall = 0;
	private Integer secondCall = 0;
	private Integer thirdCall = 0;
	private Integer firstAppointment = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}
	public Integer getFirstCall() {
		return firstCall;
	}

	public void setFirstCall(Integer firstCall) {
		this.firstCall = firstCall;
	}

	public Integer getSecondCall() {
		return secondCall;
	}

	public void setSecondCall(Integer secondCall) {
		this.secondCall = secondCall;
	}

	public Integer getThirdCall() {
		return thirdCall;
	}

	public void setThirdCall(Integer thirdCall) {
		this.thirdCall = thirdCall;
	}

	public Integer getFirstAppointment() {
		return firstAppointment;
	}

	public void setFirstAppointment(Integer firstAppointment) {
		this.firstAppointment = firstAppointment;
	}

}
