package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.UserState;

public class DoctorResponseNew extends GenericCollection {

	private String id;

	private String firstName;

	private Boolean isActive = false;

	private String emailAddress;

	private UserState userState = UserState.USERSTATECOMPLETE;
	
	private String mobileNumber;
	
	private String city;

	private Boolean isNutritionist = false;
	
	private String title;
	
	private Boolean isDoctorListed=false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
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

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getIsDoctorListed() {
		return isDoctorListed;
	}

	public void setIsDoctorListed(Boolean isDoctorListed) {
		this.isDoctorListed = isDoctorListed;
	}

	@Override
	public String toString() {
		return "DoctorResponseNew [id=" + id + ", firstName=" + firstName + ", isActive=" + isActive + ", emailAddress="
				+ emailAddress + ", userState=" + userState + ", mobileNumber=" + mobileNumber
//				+ ", city=" + city 
				+ ", isNutritionist=" + isNutritionist + ", title=" + title + ", isDoctorListed="
				+ isDoctorListed + "]";
	}
	
}
