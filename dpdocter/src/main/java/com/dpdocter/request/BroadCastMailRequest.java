package com.dpdocter.request;

import java.util.List;

public class BroadCastMailRequest {

	private List<String> userTypes;

	private List<String> recivers;

	private List<String> subscriberIds;

	private String subject;

	private String body;

	private String sender;

	private String adminId;

	private List<String> speciality;

	private Boolean isDentalWorksLab;

	private Boolean isDentalImagingLab;

	private Boolean isParent;

	private Boolean isDiagnosisLab;
	
	private String gender;
	
	private String city;
	
	private Boolean activate;
	
	private String specialityType;

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

	public List<String> getSubscriberIds() {
		return subscriberIds;
	}

	public void setSubscriberIds(List<String> subscriberIds) {
		this.subscriberIds = subscriberIds;
	}

	public List<String> getRecivers() {
		return recivers;
	}

	public void setRecivers(List<String> recivers) {
		this.recivers = recivers;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<String> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<String> userTypes) {
		this.userTypes = userTypes;
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
	


}
