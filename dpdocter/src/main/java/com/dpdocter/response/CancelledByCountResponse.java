package com.dpdocter.response;

public class CancelledByCountResponse {
	private Integer cancelledByPatient = 0;
	private Integer cancelledByDoctor = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}
	public Integer getCancelledByPatient() {
		return cancelledByPatient;
	}
	public void setCancelledByPatient(Integer cancelledByPatient) {
		this.cancelledByPatient = cancelledByPatient;
	}
	public Integer getCancelledByDoctor() {
		return cancelledByDoctor;
	}
	public void setCancelledByDoctor(Integer cancelledByDoctor) {
		this.cancelledByDoctor = cancelledByDoctor;
	}
	
	
}
