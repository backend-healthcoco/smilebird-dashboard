package com.dpdocter.beans;

import com.dpdocter.enums.AdminType;

public class AdminSignupRequest {

	private String Id;
	private String firstName;
	private String emailAddress;
	private String mobileNumber;
	private String pwd;
	private AdminType adminType = AdminType.ADMIN;
	private String city;
	private String notes;
	private Boolean isAnonymousAppointment = false;
	//for Smilebird admin
	private Boolean isBuddy = false;
	private Boolean isAgent = false;

	public AdminType getAdminType() {
		return adminType;
	}

	public void setAdminType(AdminType adminType) {
		this.adminType = adminType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getId() {
		return Id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setId(String id) {
		Id = id;
	}

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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getIsAnonymousAppointment() {
		return isAnonymousAppointment;
	}

	public void setIsAnonymousAppointment(Boolean isAnonymousAppointment) {
		this.isAnonymousAppointment = isAnonymousAppointment;
	}

	public Boolean getIsBuddy() {
		return isBuddy;
	}

	public void setIsBuddy(Boolean isBuddy) {
		this.isBuddy = isBuddy;
	}

	public Boolean getIsAgent() {
		return isAgent;
	}

	public void setIsAgent(Boolean isAgent) {
		this.isAgent = isAgent;
	}

}
