package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ConfexUserState;
import com.dpdocter.enums.EntityType;
import com.dpdocter.enums.Gender;

public class Employee extends GenericCollection {

	private String id;

	private String employeeName;

	private String mobileNumber;

	private String countryCode;

	private Integer age;

	private String employeeId;

	private Gender gender;

	private String department;

	private String reportingManager;

	private String emailId;

	private String companyId;

	private String city;

	private List<CompanyLocation> companyLocation;

	private String profileImageUrl;

	private String timezone;

	private Boolean isWorkFromHome;

	private Boolean isDiscarded = false;

	private Long currentTime;

	private ConfexUserState userType = ConfexUserState.EMPLOYEE;

	private EntityType entity;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getReportingManager() {
		return reportingManager;
	}

	public void setReportingManager(String reportingManager) {
		this.reportingManager = reportingManager;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public List<CompanyLocation> getCompanyLocation() {
		return companyLocation;
	}

	public void setCompanyLocation(List<CompanyLocation> companyLocation) {
		this.companyLocation = companyLocation;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Boolean getIsWorkFromHome() {
		return isWorkFromHome;
	}

	public void setIsWorkFromHome(Boolean isWorkFromHome) {
		this.isWorkFromHome = isWorkFromHome;
	}

	public ConfexUserState getUserType() {
		return userType;
	}

	public void setUserType(ConfexUserState userType) {
		this.userType = userType;
	}

	public EntityType getEntity() {
		return entity;
	}

	public void setEntity(EntityType entity) {
		this.entity = entity;
	}

	public Long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Long currentTime) {
		this.currentTime = currentTime;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", employeeName=" + employeeName + ", mobileNumber=" + mobileNumber
				+ ", countryCode=" + countryCode + ", age=" + age + ", employeeId=" + employeeId + ", gender=" + gender
				+ ", department=" + department + ", reportingManager=" + reportingManager + ", emailId=" + emailId
				+ ", companyId=" + companyId + ", city=" + city + ", companyLocation=" + companyLocation
				+ ", profileImageUrl=" + profileImageUrl + ", timezone=" + timezone + ", isWorkFromHome="
				+ isWorkFromHome + ", isDiscarded=" + isDiscarded + ", userType=" + userType + "]";
	}

}
