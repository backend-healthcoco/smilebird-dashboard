package com.dpdocter.response;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.UserState;

public class DoctorResponse extends GenericCollection {

	private String id;
	
	private String userId;

	private String firstName;

	private Boolean isActive = false;

	private String emailAddress;

	private UserState userState = UserState.USERSTATECOMPLETE;

	private String userUId;

	private String role;
	
	private String mobileNumber;
	
	private String city;

	private Boolean isNutritionist = false;

	private String registrationNumber;
	
	private String qualification;
	
	private List<String> specialities;
	
	private List<ObjectId> specialitiesIds;
	
	private String title;
	
	private Boolean isDoctorListed=false;
	
	private Boolean isRegistrationDetailsVerified = false;

	private Boolean isPhotoIdVerified = false;

	private Boolean isOnlineConsultationAvailable = false;

	private Boolean isHealthcocoDoctor=false;
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	
	
	
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	
	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

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
	

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public Boolean getIsDoctorListed() {
		return isDoctorListed;
	}

	public void setIsDoctorListed(Boolean isDoctorListed) {
		this.isDoctorListed = isDoctorListed;
	}

	
	public List<ObjectId> getSpecialitiesIds() {
		return specialitiesIds;
	}

	public void setSpecialitiesIds(List<ObjectId> specialitiesIds) {
		this.specialitiesIds = specialitiesIds;
	}
	
	

	public Boolean getIsRegistrationDetailsVerified() {
		return isRegistrationDetailsVerified;
	}

	public void setIsRegistrationDetailsVerified(Boolean isRegistrationDetailsVerified) {
		this.isRegistrationDetailsVerified = isRegistrationDetailsVerified;
	}

	public Boolean getIsPhotoIdVerified() {
		return isPhotoIdVerified;
	}

	public void setIsPhotoIdVerified(Boolean isPhotoIdVerified) {
		this.isPhotoIdVerified = isPhotoIdVerified;
	}

	public Boolean getIsOnlineConsultationAvailable() {
		return isOnlineConsultationAvailable;
	}

	public void setIsOnlineConsultationAvailable(Boolean isOnlineConsultationAvailable) {
		this.isOnlineConsultationAvailable = isOnlineConsultationAvailable;
	}
	
	

	public Boolean getIsHealthcocoDoctor() {
		return isHealthcocoDoctor;
	}

	public void setIsHealthcocoDoctor(Boolean isHealthcocoDoctor) {
		this.isHealthcocoDoctor = isHealthcocoDoctor;
	}

	@Override
	public String toString() {
		return "DoctorResponse [id=" + id + ", firstName=" + firstName + ", isActive=" + isActive + ", emailAddress="
				+ emailAddress + ", userState=" + userState + ", userUId=" + userUId + ", role=" + role + "]";
	}

}
