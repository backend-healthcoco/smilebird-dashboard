package com.dpdocter.beans;

import java.util.List;

public class PCUser {

	private String id;

	private String name;

	private String userName;

	private String emailAddress;

	private String mobileNumber;

	private String gender;

	private String imageUrl;

	private String thumbnailUrl;

	private String colorCode;

	private String userUId;

	private String role;

	private String title;

	private Boolean isActive = false;

	private List<String> divisionId;

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<String> getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(List<String> divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public String toString() {
		return "PCUser [id=" + id + ", name=" + name + ", userName=" + userName + ", emailAddress=" + emailAddress
				+ ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", colorCode=" + colorCode + ", userUId=" + userUId + ", role=" + role + ", title="
				+ title + ", isActive=" + isActive + "]";
	}

}
