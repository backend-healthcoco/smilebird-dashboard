package com.dpdocter.beans;

import java.util.List;

public class Patient {
	private String patientId;

	private String bloodGroup;
	
	private String localPatientName;

	private String profession;

	private List<Relations> relations;

	private String emailAddress;

	private String doctorId;

	private String addressId;

	private String secMobile;

	private String adhaarId;

	private String panCardNumber;

	private String drivingLicenseId;

	private String insuranceId;

	private String insuranceName;

	private List<String> notes;
	
	private String PNUM;

	private Boolean isChild = false;

	private Boolean isDataAvailableWithOtherDoctor = false;

	private Boolean isPatientOTPVerified = false;

	private String fatherName;

	private String motherName;
	
	private String backendPatientId;

	private String landlineNumber;
	
	private List<String> consultantDoctorIds;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public List<Relations> getRelations() {
		return relations;
	}

	public void setRelations(List<Relations> relations) {
		this.relations = relations;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getSecMobile() {
		return secMobile;
	}

	public void setSecMobile(String secMobile) {
		this.secMobile = secMobile;
	}

	public String getAdhaarId() {
		return adhaarId;
	}

	public void setAdhaarId(String adhaarId) {
		this.adhaarId = adhaarId;
	}

	public String getPanCardNumber() {
		return panCardNumber;
	}

	public void setPanCardNumber(String panCardNumber) {
		this.panCardNumber = panCardNumber;
	}

	public String getDrivingLicenseId() {
		return drivingLicenseId;
	}

	public void setDrivingLicenseId(String drivingLicenseId) {
		this.drivingLicenseId = drivingLicenseId;
	}

	public String getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(String insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	public Boolean getIsDataAvailableWithOtherDoctor() {
		return isDataAvailableWithOtherDoctor;
	}

	public void setIsDataAvailableWithOtherDoctor(Boolean isDataAvailableWithOtherDoctor) {
		this.isDataAvailableWithOtherDoctor = isDataAvailableWithOtherDoctor;
	}

	public Boolean getIsPatientOTPVerified() {
		return isPatientOTPVerified;
	}

	public void setIsPatientOTPVerified(Boolean isPatientOTPVerified) {
		this.isPatientOTPVerified = isPatientOTPVerified;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	
	    

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public String getBackendPatientId() {
		return backendPatientId;
	}

	public void setBackendPatientId(String backendPatientId) {
		this.backendPatientId = backendPatientId;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	
	public List<String> getConsultantDoctorIds() {
		return consultantDoctorIds;
	}

	public void setConsultantDoctorIds(List<String> consultantDoctorIds) {
		this.consultantDoctorIds = consultantDoctorIds;
	}

	@Override
	public String toString() {
		return "Patient [patientId=" + patientId + ", bloodGroup=" + bloodGroup + ", profession=" + profession
				+ ", relations=" + relations + ", emailAddress=" + emailAddress + ", doctorId=" + doctorId
				+ ", addressId=" + addressId + ", secMobile=" + secMobile + ", adhaarId=" + adhaarId
				+ ", panCardNumber=" + panCardNumber + ", drivingLicenseId=" + drivingLicenseId + ", insuranceId="
				+ insuranceId + ", insuranceName=" + insuranceName + ", notes=" + notes
				+ ", isDataAvailableWithOtherDoctor=" + isDataAvailableWithOtherDoctor + ", isPatientOTPVerified="
				+ isPatientOTPVerified + "]";
	}
}
