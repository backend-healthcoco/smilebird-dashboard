package com.dpdocter.beans;

import java.util.Date;

public class PatientRecord {
	private String doctorId;
	private String firstName;
	private String emailAddress;
	private String locationName;
	private String locality;
	private String city;
	private int totalNoOfPatients;
	private int totalNoOfRX;
	private int totalNoOfClinincalNotes;
	private int totalNoOfRecords;
	private int totalNoOfAppointments;
	private Date createdTime;
	private Date updatedTime;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getTotalNoOfPatients() {
		return totalNoOfPatients;
	}

	public void setTotalNoOfPatients(int totalNoOfPatients) {
		this.totalNoOfPatients = totalNoOfPatients;
	}

	public int getTotalNoOfRX() {
		return totalNoOfRX;
	}

	public void setTotalNoOfRX(int totalNoOfRX) {
		this.totalNoOfRX = totalNoOfRX;
	}

	public int getTotalNoOfClinincalNotes() {
		return totalNoOfClinincalNotes;
	}

	public void setTotalNoOfClinincalNotes(int totalNoOfClinincalNotes) {
		this.totalNoOfClinincalNotes = totalNoOfClinincalNotes;
	}

	public int getTotalNoOfRecords() {
		return totalNoOfRecords;
	}

	public void setTotalNoOfRecords(int totalNoOfRecords) {
		this.totalNoOfRecords = totalNoOfRecords;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public int getTotalNoOfAppointments() {
		return totalNoOfAppointments;
	}

	public void setTotalNoOfAppointments(int totalNoOfAppointments) {
		this.totalNoOfAppointments = totalNoOfAppointments;
	}

}
