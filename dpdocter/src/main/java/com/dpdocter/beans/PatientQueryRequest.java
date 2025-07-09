package com.dpdocter.beans;

public class PatientQueryRequest {

	private String patientId;
	
	private String doctorId;
	
	private String locationId;

	private String patient_problem;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getPatient_problem() {
		return patient_problem;
	}

	public void setPatient_problem(String patient_problem) {
		this.patient_problem = patient_problem;
	}
	
	


}
