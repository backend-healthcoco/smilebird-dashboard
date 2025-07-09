package com.dpdocter.beans;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AdminType;
import com.dpdocter.enums.UserState;

public class User extends GenericCollection {
	private String id;

	private String locationId;

	private String hospitalId;

	private String title;

	private String firstName;

	private String localPatientName;

	private String userName;

	private String emailAddress;

	private String mobileNumber;

	private String whatsAppNumber;

	private String countryCode;

	private String gender;

	private DOB dob;

	private String bloodGroup;

	private String secPhoneNumber;

	private Boolean isPartOfClinic;

	private String imageUrl;

	private String thumbnailUrl;

	private String colorCode;

	private UserState userState;

	private String userUId;

	private Integer regularCheckUpMonths;

	private Boolean isPasswordSet = false;

	private Boolean isMedicalStudent = false;

	private String role;

	private Boolean isActive = false;

	private List<String> specialities;

	private List<String> services;

	private List<String> campIds;
	private List<String> campNames;

	// this two field used only at ADMIN side
	private AdminType adminType = AdminType.ADMIN;

	private String city;

	private String notes;

	private long rankingCount = 1000;

	private Boolean isAnonymousAppointment = false;

	private String adminId;

	private Boolean isBuddy = false;
	private Boolean isAgent = false;
	private Boolean isCampDoctor = false;

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public AdminType getAdminType() {
		return adminType;
	}

	public void setAdminType(AdminType adminType) {
		this.adminType = adminType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getIsPartOfClinic() {
		return isPartOfClinic;
	}

	public void setIsPartOfClinic(Boolean isPartOfClinic) {
		this.isPartOfClinic = isPartOfClinic;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getSecPhoneNumber() {
		return secPhoneNumber;
	}

	public void setSecPhoneNumber(String secPhoneNumber) {
		this.secPhoneNumber = secPhoneNumber;
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

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public Integer getRegularCheckUpMonths() {
		return regularCheckUpMonths;
	}

	public void setRegularCheckUpMonths(Integer regularCheckUpMonths) {
		this.regularCheckUpMonths = regularCheckUpMonths;
	}

	public Boolean getIsPasswordSet() {
		return isPasswordSet;
	}

	public void setIsPasswordSet(Boolean isPasswordSet) {
		this.isPasswordSet = isPasswordSet;
	}

	public Boolean getIsMedicalStudent() {
		return isMedicalStudent;
	}

	public void setIsMedicalStudent(Boolean isMedicalStudent) {
		this.isMedicalStudent = isMedicalStudent;
	}

	public List<String> getCampIds() {
		return campIds;
	}

	public void setCampIds(List<String> campIds) {
		this.campIds = campIds;
	}

	public Boolean getIsAnonymousAppointment() {
		return isAnonymousAppointment;
	}

	public void setIsAnonymousAppointment(Boolean isAnonymousAppointment) {
		this.isAnonymousAppointment = isAnonymousAppointment;
	}

	public Boolean getIsBuddy() {
		return isBuddy;
	}

	public void setIsBuddy(Boolean isBuddy) {
		this.isBuddy = isBuddy;
	}

	public Boolean getIsAgent() {
		return isAgent;
	}

	public void setIsAgent(Boolean isAgent) {
		this.isAgent = isAgent;
	}

	public String getWhatsAppNumber() {
		return whatsAppNumber;
	}

	public void setWhatsAppNumber(String whatsAppNumber) {
		this.whatsAppNumber = whatsAppNumber;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName=" + userName
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", countryCode=" + countryCode
				+ ", gender=" + gender + ", dob=" + dob + ", bloodGroup=" + bloodGroup + ", secPhoneNumber="
				+ secPhoneNumber + ", isPartOfClinic=" + isPartOfClinic + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", colorCode=" + colorCode + ", userState=" + userState + ", userUId=" + userUId
				+ ", regularCheckUpMonths=" + regularCheckUpMonths + ", isPasswordSet=" + isPasswordSet
				+ ", isMedicalStudent=" + isMedicalStudent + ", role=" + role + ", isActive=" + isActive
				+ ", specialities=" + specialities + ", services=" + services + ", adminType=" + adminType + ", city="
				+ city + ", notes=" + notes + ", rankingCount=" + rankingCount + "]";
	}

	public Boolean getIsCampDoctor() {
		return isCampDoctor;
	}

	public void setIsCampDoctor(Boolean isCampDoctor) {
		this.isCampDoctor = isCampDoctor;
	}

	public List<String> getCampNames() {
		return campNames;
	}

	public void setCampNames(List<String> campNames) {
		this.campNames = campNames;
	}

}
