package com.dpdocter.response;

public class LeadStageCountResponse {
	private Integer initialCall = 0;
	private Integer clinicVisit = 0;
	private Integer convertedStage = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}

	public Integer getInitialCall() {
		return initialCall;
	}

	public void setInitialCall(Integer initialCall) {
		this.initialCall = initialCall;
	}

	public Integer getClinicVisit() {
		return clinicVisit;
	}

	public void setClinicVisit(Integer clinicVisit) {
		this.clinicVisit = clinicVisit;
	}

	public Integer getConvertedStage() {
		return convertedStage;
	}

	public void setConvertedStage(Integer convertedStage) {
		this.convertedStage = convertedStage;
	}

}
