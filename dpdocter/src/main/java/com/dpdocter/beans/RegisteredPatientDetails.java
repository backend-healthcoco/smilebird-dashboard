package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ContactState;
import com.dpdocter.enums.FollowUpType;
import com.dpdocter.response.UserNutritionSubscriptionResponse;

public class RegisteredPatientDetails extends GenericCollection {

	private String firstName;

	private String localPatientName;

	private String lastName;

	private String middleName;

	private String imageUrl;

	private String thumbnailUrl;

	private DOB dob;

	private String userId;

	private String userName;

	private String mobileNumber;
	
	private String whatsAppNumber;

	private String gender;

	private Patient patient;

	private Address address;

	private List<Group> groups;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String PID;

	private String colorCode;

	private Reference referredBy;

	private Boolean isPartOfClinic;

	private Boolean isPartOfConsultantDoctor = true;

	private String backendPatientId;

	private List<String> consultantDoctorIds;

	private Boolean isPatientDiscarded = false;

	private String PNUM;

//	private List<UserNutritionSubscriptionResponse> userNutritionSubscriptions;

	private Boolean isChild = false;

	private String fatherName;

	private String motherName;

	private String landlineNumber;

	private Boolean isSuperStar = false;
	
	private ContactState contactState;

	private Date followUp;
	
	private List<String> platform;
	
	private String followUpReason;
	
	private List<String> treatmentId;
	
	private List<String> treatmentNames;
	
    private String city;
    
	private String smileBuddyId;

	private String smileBuddyName;
	
	private String language;
	
	private String createdFrom;

	public ContactState getContactState() {
		return contactState;
	}

	public void setContactState(ContactState contactState) {
		this.contactState = contactState;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Reference getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(Reference referredBy) {
		this.referredBy = referredBy;
	}

	public Boolean getIsPartOfClinic() {
		return isPartOfClinic;
	}

	public void setIsPartOfClinic(Boolean isPartOfClinic) {
		this.isPartOfClinic = isPartOfClinic;
	}

	public Boolean getIsPartOfConsultantDoctor() {
		return isPartOfConsultantDoctor;
	}

	public void setIsPartOfConsultantDoctor(Boolean isPartOfConsultantDoctor) {
		this.isPartOfConsultantDoctor = isPartOfConsultantDoctor;
	}

	public String getBackendPatientId() {
		return backendPatientId;
	}

	public void setBackendPatientId(String backendPatientId) {
		this.backendPatientId = backendPatientId;
	}

	public List<String> getConsultantDoctorIds() {
		return consultantDoctorIds;
	}

	public void setConsultantDoctorIds(List<String> consultantDoctorIds) {
		this.consultantDoctorIds = consultantDoctorIds;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
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

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	public Boolean getIsSuperStar() {
		return isSuperStar;
	}

	public void setIsSuperStar(Boolean isSuperStar) {
		this.isSuperStar = isSuperStar;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public Date getFollowUp() {
		return followUp;
	}

	public void setFollowUp(Date followUp) {
		this.followUp = followUp;
	}

	public List<String> getPlatform() {
		return platform;
	}

	public void setPlatform(List<String> platform) {
		this.platform = platform;
	}

	public String getFollowUpReason() {
		return followUpReason;
	}

	public void setFollowUpReason(String followUpReason) {
		this.followUpReason = followUpReason;
	}

	public List<String> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<String> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public List<String> getTreatmentNames() {
		return treatmentNames;
	}

	public void setTreatmentNames(List<String> treatmentNames) {
		this.treatmentNames = treatmentNames;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(String smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public String getSmileBuddyName() {
		return smileBuddyName;
	}

	public void setSmileBuddyName(String smileBuddyName) {
		this.smileBuddyName = smileBuddyName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCreatedFrom() {
		return createdFrom;
	}

	public void setCreatedFrom(String createdFrom) {
		this.createdFrom = createdFrom;
	}

	public String getWhatsAppNumber() {
		return whatsAppNumber;
	}

	public void setWhatsAppNumber(String whatsAppNumber) {
		this.whatsAppNumber = whatsAppNumber;
	}

	@Override
	public String toString() {
		return "RegisteredPatientDetails [firstName=" + firstName + ", lastName=" + lastName + ", middleName="
				+ middleName + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", dob=" + dob
				+ ", userId=" + userId + ", userName=" + userName + ", mobileNumber=" + mobileNumber + ", gender="
				+ gender + ", patient=" + patient + ", address=" + address + ", groups=" + groups + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", PID=" + PID
				+ ", colorCode=" + colorCode + ", referredBy=" + referredBy + ", isPartOfClinic=" + isPartOfClinic
				+ "]";
	}

}
