package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class CommunicationDoctorTeamRequest extends GenericCollection{
	
	private String id;
	
	private String doctorName;
	
	private String emailAddress;
	
	private String mobileNumber;
	
	private String speaciality;
	
	private String city;
	
	private List<CommunicationObject> communication;
	
	private DoctorResponseStatus status = DoctorResponseStatus.WAITING;
	
	private String doctorId;
	
	
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getSpeaciality() {
		return speaciality;
	}

	public void setSpeaciality(String speaciality) {
		this.speaciality = speaciality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	
	public List<CommunicationObject> getCommunication() {
		return communication;
	}

	public void setCommunication(List<CommunicationObject> communication) {
		this.communication = communication;
	}

	public DoctorResponseStatus getStatus() {
		return status;
	}

	public void setStatus(DoctorResponseStatus status) {
		this.status = status;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	private Boolean isDiscarded = false;
    
    
	
	
	
	

}
