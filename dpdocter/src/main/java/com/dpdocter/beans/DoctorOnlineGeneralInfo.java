package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.ConsultationType;

import com.dpdocter.enums.DoctorFacility;

public class DoctorOnlineGeneralInfo {
	
	private String id;

    private String doctorId;

    private List<DoctorConsultation> consultationType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<DoctorConsultation> getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(List<DoctorConsultation> consultationType) {
		this.consultationType = consultationType;
	}

	

	

	

}
