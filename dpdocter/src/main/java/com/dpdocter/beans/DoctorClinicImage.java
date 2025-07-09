package com.dpdocter.beans;

import java.util.List;

public class DoctorClinicImage {

	private String doctorId;
	
	private String locationId;
	
	List<ClinicImage> clinicImage;

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

	public List<ClinicImage> getClinicImage() {
		return clinicImage;
	}

	public void setClinicImage(List<ClinicImage> clinicImage) {
		this.clinicImage = clinicImage;
	}

	
	
	
	
}
