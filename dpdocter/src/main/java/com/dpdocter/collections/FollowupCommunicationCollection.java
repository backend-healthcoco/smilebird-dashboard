package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.CommunicationObject;
import com.dpdocter.beans.FollowupObject;

@Document(collection = "followUp_communication_details_cl")
public class FollowupCommunicationCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String userName;

	@Field
	private String emailAddress;

	@Field
	private String mobileNumber;

	@Field
	private String city;

	@Field
	private List<FollowupObject> communication;

	@Field
	private ObjectId userId;
	
	@Field
	private String audioFile;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<FollowupObject> getCommunication() {
		return communication;
	}

	public void setCommunication(List<FollowupObject> communication) {
		this.communication = communication;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}
