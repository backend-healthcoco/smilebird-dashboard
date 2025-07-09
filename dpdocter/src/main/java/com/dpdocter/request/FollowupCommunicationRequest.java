package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.FollowupObject;
import com.dpdocter.collections.GenericCollection;

public class FollowupCommunicationRequest  extends GenericCollection{
	
	private String id;
	
	private String userName;
	
	private String emailAddress;
	
	private String mobileNumber;
	
	private String city;
	
	private List<FollowupObject> communication;
	
	private String userId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
