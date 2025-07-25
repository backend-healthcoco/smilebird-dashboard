package com.dpdocter.response;

import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;

public class DoctorHospitalDentalImagingAssociationLookupResponse {

	private String id;
	private String doctorId;
	private String doctorLocationId;
	private String hospitalId;
	private Boolean discarded = false;
	private Location location;
	private User doctor;
	private Hospital hospital;

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public String getDoctorLocationId() {
		return doctorLocationId;
	}

	public void setDoctorLocationId(String doctorLocationId) {
		this.doctorLocationId = doctorLocationId;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "DoctorHospitalDentalImagingAssociationLookupResponse [id=" + id + ", doctorId=" + doctorId
				+ ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", doctor=" + doctor + ", hospital="
				+ hospital + "]";
	}

}
