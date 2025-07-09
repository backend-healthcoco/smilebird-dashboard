package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.SmsRoute;

public class BroadCastRequest {

	private List<String> userTypes;

	private String message;

	private List<String> subscriberIds;

	private List<String> mobileNumbers;

	private List<String> speciality;

	private String adminId;

	private String image;

	private Boolean isDentalWorksLab;

	private Boolean isDentalImagingLab;

	private Boolean isParent;

	private Boolean isDiagnosisLab;	

	private String gender;
	
	private String city;
	
	private Boolean activate;
	
	private String specialityType;
	
	private SmsRoute smsRoute;

	public Boolean getIsDiagnosisLab() {
		return isDiagnosisLab;
	}

	public void setIsDiagnosisLab(Boolean isDiagnosisLab) {
		this.isDiagnosisLab = isDiagnosisLab;
	}

	public Boolean getIsDentalWorksLab() {
		return isDentalWorksLab;
	}

	public void setIsDentalWorksLab(Boolean isDentalWorksLab) {
		this.isDentalWorksLab = isDentalWorksLab;
	}

	public Boolean getIsDentalImagingLab() {
		return isDentalImagingLab;
	}

	public void setIsDentalImagingLab(Boolean isDentalImagingLab) {
		this.isDentalImagingLab = isDentalImagingLab;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public List<String> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<String> userTypes) {
		this.userTypes = userTypes;
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getMobileNumbers() {
		return mobileNumbers;
	}

	public void setMobileNumbers(List<String> mobileNumbers) {
		this.mobileNumbers = mobileNumbers;
	}

	public List<String> getSubscriberIds() {
		return subscriberIds;
	}

	public void setSubscriberIds(List<String> subscriberIds) {
		this.subscriberIds = subscriberIds;
	}

	public List<String> getSpeciality() {
		return speciality;
	}

	public void setSpeciality(List<String> speciality) {
		this.speciality = speciality;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Boolean getActivate() {
		return activate;
	}

	public void setActivate(Boolean activate) {
		this.activate = activate;
	}

	public String getSpecialityType() {
		return specialityType;
	}

	public void setSpecialityType(String specialityType) {
		this.specialityType = specialityType;
	}

	public SmsRoute getSmsRoute() {
		return smsRoute;
	}

	public void setSmsRoute(SmsRoute smsRoute) {
		this.smsRoute = smsRoute;
	}
	
	
	

}
