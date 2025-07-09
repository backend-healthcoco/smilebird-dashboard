package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.LabType;
import com.dpdocter.response.ImageURLResponse;

public class CollectionBoy {

	private String id;
	private String locationId;
	private String hospitalId;
	private String userId;
	private String name;
	private Age age;
	private String gender;
	private Address address;
	private String mobileNumber;
	private String password;
	private String profileImageURL;
	private Boolean discarded = false;
	private Boolean isAvailable = false;
	private List<ImageURLResponse> images;
	private LabType labType = LabType.MEDICINE_ORDER;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfileImageURL() {
		return profileImageURL;
	}

	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public List<ImageURLResponse> getImages() {
		return images;
	}

	public void setImages(List<ImageURLResponse> images) {
		this.images = images;
	}

	public LabType getLabType() {
		return labType;
	}

	public void setLabType(LabType labType) {
		this.labType = labType;
	}

	@Override
	public String toString() {
		return "CollectionBoy [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", userId="
				+ userId + ", name=" + name + ", age=" + age + ", gender=" + gender + ", address=" + address
				+ ", mobileNumber=" + mobileNumber + ", password=" + password + ", profileImageURL=" + profileImageURL
				+ ", discarded=" + discarded + ", isAvailable=" + isAvailable + ", images=" + images + ", labType="
				+ labType + "]";
	}

	
}
