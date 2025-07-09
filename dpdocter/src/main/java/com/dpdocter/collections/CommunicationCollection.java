package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.CommunicationObject;
import com.dpdocter.beans.DoctorResponseStatus;

//this collection used for saving detail/ communication between doctors & sales team
@Document(collection = "communication_details_cl")
public class CommunicationCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String doctorName;

	@Field
	private String emailAddress;

	@Field
	private String mobileNumber;

	@Field
	private String speaciality;

	@Field
	private String city;

	@Field
	private List<CommunicationObject> communication;

	@Field
	private DoctorResponseStatus status;

	@Field
	private ObjectId doctorId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	
	
}
