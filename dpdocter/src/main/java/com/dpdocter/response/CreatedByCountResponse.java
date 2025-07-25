package com.dpdocter.response;

public class CreatedByCountResponse {
	private Integer createdByPatient = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}

	public Integer getCreatedByPatient() {
		return createdByPatient;
	}

	public void setCreatedByPatient(Integer createdByPatient) {
		this.createdByPatient = createdByPatient;
	}

}
