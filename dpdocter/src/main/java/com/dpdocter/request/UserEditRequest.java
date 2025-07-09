package com.dpdocter.request;

import java.util.List;

public class UserEditRequest {

	private String id;

	private String name;

	private String mobileNumber;

	private String gender;

	private String imageUrl;

	private String thumbnailUrl;

	private String colorCode;

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
		return "UserEditRequest [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", gender=" + gender
				+ ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", colorCode=" + colorCode + ", title="
				+ title + ", isActive=" + isActive + ", divisionId=" + divisionId + "]";
	}

}
