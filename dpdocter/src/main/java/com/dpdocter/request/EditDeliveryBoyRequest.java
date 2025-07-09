package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Age;
import com.dpdocter.enums.LabType;
import com.dpdocter.response.ImageURLResponse;

public class EditDeliveryBoyRequest {

	private String id;
	private String name;
	private Age age;
	private String gender;
	private Address address;
	private String profileImageURL;
	private Boolean discarded = false;
	private Boolean isAvailable = false;
	private List<ImageURLResponse> images;
	private LabType labType = LabType.MEDICINE_ORDER;
	private List<String> documents;

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

	public List<String> getDocuments() {
		return documents;
	}

	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}
	
	

}
