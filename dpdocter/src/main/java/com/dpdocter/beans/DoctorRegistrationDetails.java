package com.dpdocter.beans;

import com.dpdocter.enums.RegistrationType;

public class DoctorRegistrationDetails {

	private String doctorId;
	
	private String locationId;

    private FileDetails image;
    
    private RegistrationType type;

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

	public FileDetails getImage() {
		return image;
	}

	public void setImage(FileDetails image) {
		this.image = image;
	}

	public RegistrationType getType() {
		return type;
	}

	public void setType(RegistrationType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DoctorRegistrationDetails [doctorId=" + doctorId + ", locationId=" + locationId 
				+ ", type=" + type + "]";
	}
    
    
}
