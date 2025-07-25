package com.dpdocter.response;

public class RescheduledByCountResponse {
	private Integer rescheduleByPatient = 0;
	private Integer rescheduleByDoctor = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	} 
	
	public Integer getRescheduleByPatient() {
		return rescheduleByPatient;
	}

	public void setRescheduleByPatient(Integer rescheduleByPatient) {
		this.rescheduleByPatient = rescheduleByPatient;
	}

	public Integer getRescheduleByDoctor() {
		return rescheduleByDoctor;
	}

	public void setRescheduleByDoctor(Integer rescheduleByDoctor) {
		this.rescheduleByDoctor = rescheduleByDoctor;
	}

}
