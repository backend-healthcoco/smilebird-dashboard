package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.ConfexUserState;

public class ConfexUser {

	private String id;

	private String title;

	private String firstName;

	private String userName;

	private String emailAddress;

	private String mobileNumber;

	private String imageUrl;

	private String thumbnailUrl;

	private Boolean isActive = false;

	private Boolean isVerified = false;
	
	private Boolean setPassword = false;

	private String colorCode;

	private ConfexUserState userType = ConfexUserState.ADMIN;

	private String userUId;

	private String role;

	private Address address;
	
	private DOB dob;
	
	private String designation;
	
	private String organization;
	
	private String registrationType;
	
	private List<String> specialities;

	private List<String> specialityIds;

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public ConfexUserState getUserType() {
		return userType;
	}

	public void setUserType(ConfexUserState userType) {
		this.userType = userType;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	public Boolean getSetPassword() {
		return setPassword;
	}

	public void setSetPassword(Boolean setPassword) {
		this.setPassword = setPassword;
	}

	public List<String> getSpecialityIds() {
		return specialityIds;
	}

	public void setSpecialityIds(List<String> specialityIds) {
		this.specialityIds = specialityIds;
	}

}
