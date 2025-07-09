package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dashboard_user_cl")
public class DashboardUserCollection {

	@Id
	private ObjectId id;

	@Field
	private String firstName;

	@Field
	private String userName;

	@Field
	private ObjectId companyId;

	@Field
	private String emailAddress;

	@Field
	private String mobileNumber;

	@Field
	private String countryCode;

	@Field
	private List<String> locationId;

	@Field
	private String companyName;

	@Field
	private List<String> userState;

	@Field
	private String profileImage;

	@Field
	private Boolean isPasswordSet = false;

	@Field
	private Boolean isActivate = Boolean.FALSE;

	@Field
	private Boolean discarded = Boolean.FALSE;

	@Field
	private List<String> uiPermission;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public List<String> getLocationId() {
		return locationId;
	}

	public void setLocationId(List<String> locationId) {
		this.locationId = locationId;
	}

	

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public Boolean getIsPasswordSet() {
		return isPasswordSet;
	}

	public void setIsPasswordSet(Boolean isPasswordSet) {
		this.isPasswordSet = isPasswordSet;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<String> getUserState() {
		return userState;
	}

	public void setUserState(List<String> userState) {
		this.userState = userState;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getUiPermission() {
		return uiPermission;
	}

	public void setUiPermission(List<String> uiPermission) {
		this.uiPermission = uiPermission;
	}

	public ObjectId getCompanyId() {
		return companyId;
	}

	public void setCompanyId(ObjectId companyId) {
		this.companyId = companyId;
	}

	@Override
	public String toString() {
		return "DashboardUserCollection [id=" + id + ", firstName=" + firstName + ", userName=" + userName
				+ ", companyId=" + companyId + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
				+ ", countryCode=" + countryCode + ", locationId=" + locationId + ", companyName=" + companyName
				+ ", userState=" + userState + ", profileImage=" + profileImage + ", isPasswordSet=" + isPasswordSet
				+ ", isActivate=" + isActivate + ", discarded=" + discarded + ", uiPermission=" + uiPermission + "]";
	}

	

}
