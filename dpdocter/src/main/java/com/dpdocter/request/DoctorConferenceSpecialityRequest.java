package com.dpdocter.request;

import java.util.List;

public class DoctorConferenceSpecialityRequest {
	private String id;

	private List<String> speciality;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSpeciality() {
		return speciality;
	}

	public void setSpeciality(List<String> speciality) {
		this.speciality = speciality;
	}

}
