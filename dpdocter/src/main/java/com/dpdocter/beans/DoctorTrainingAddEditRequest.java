package com.dpdocter.beans;

import java.util.List;

public class DoctorTrainingAddEditRequest {

	private String doctorId;

    private List<String> trainingsCertifications;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<String> getTrainingsCertifications() {
		return trainingsCertifications;
	}

	public void setTrainingsCertifications(List<String> trainingsCertifications) {
		this.trainingsCertifications = trainingsCertifications;
	}
   
    
}
