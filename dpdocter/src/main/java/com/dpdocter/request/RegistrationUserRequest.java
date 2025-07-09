package com.dpdocter.request;

import com.dpdocter.enums.DiseaseEnum;

public class RegistrationUserRequest {

	private String id;
	private String name;
	private String mobileNumber;
	private String emailAddress;
    private DiseaseEnum disease;    
	private String city;
	private Boolean discarded = false;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	public DiseaseEnum getDisease() {
		return disease;
	}
	public void setDisease(DiseaseEnum disease) {
		this.disease = disease;
	}
	
	
}
