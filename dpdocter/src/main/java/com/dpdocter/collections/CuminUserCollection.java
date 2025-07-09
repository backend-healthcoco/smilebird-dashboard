package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.enums.DiseaseEnum;
import com.dpdocter.enums.UserState;

@Document(collection = "cumin_user_cl")
public class CuminUserCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private String mobileNumber;
	@Field
	private String emailAddress;
	@Field
	private String city;
	@Field
	private String comment;
	@Field
	private String response;
	@Field
	private String imageUrl;
	@Field
	private String alternateNumber;
	@Field
	private Address address;
	@Field
	private UserState userState;
	@Field
    private DiseaseEnum disease;    
	@Field
	private Boolean isPaid = false;
	@Field
	private Boolean isProfileComplete = false;
	@Field
	private Boolean discarded = false;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public Boolean getIsPaid() {
		return isPaid;
	}
	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getAlternateNumber() {
		return alternateNumber;
	}
	public void setAlternateNumber(String alternateNumber) {
		this.alternateNumber = alternateNumber;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public UserState getUserState() {
		return userState;
	}
	public void setUserState(UserState userState) {
		this.userState = userState;
	}
	public DiseaseEnum getDisease() {
		return disease;
	}
	public void setDisease(DiseaseEnum disease) {
		this.disease = disease;
	}
	public Boolean getIsProfileComplete() {
		return isProfileComplete;
	}
	public void setIsProfileComplete(Boolean isProfileComplete) {
		this.isProfileComplete = isProfileComplete;
	}
	
	
	
}
