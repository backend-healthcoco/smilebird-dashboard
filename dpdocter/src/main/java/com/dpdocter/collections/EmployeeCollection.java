package com.dpdocter.collections;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.CompanyLocation;
import com.dpdocter.enums.ConfexUserState;
import com.dpdocter.enums.EntityType;
import com.dpdocter.enums.Gender;


@Document(collection = "employee_cl")
public class EmployeeCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String employeeName;
	@Field
	private String mobileNumber;

	@Field
	private Integer age;
	@Field
	private String employeeId;
	@Field
	private Gender gender;
	@Field
	private String department;
	@Field
	private String reportingManager;
	@Field
	private String emailId;
	@Field
	private ObjectId companyId;
	@Field
	private String city;
	@Field
	private List<CompanyLocation> companyLocation;
	
	@Field
	private String timezone;


	@Field
	private String profileImageUrl;
	@Field
	private Boolean isDiscarded = false;

	@Field
	private Boolean isWorkFromHome = false;

	@Field
	private String countryCode;

	@Field
	private char[] password;

	@Field
	private ConfexUserState userType = ConfexUserState.EMPLOYEE;
	
	@Field
	private EntityType entity;


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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


	public ObjectId getCompanyId() {
		return companyId;
	}

	public void setCompanyId(ObjectId companyId) {
		this.companyId = companyId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public Boolean getIsWorkFromHome() {
		return isWorkFromHome;
	}

	public void setIsWorkFromHome(Boolean isWorkFromHome) {
		this.isWorkFromHome = isWorkFromHome;
	}

	public String getCountryCode() {
		return countryCode;

	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public ConfexUserState getUserType() {
		return userType;
	}

	public void setUserType(ConfexUserState userType) {
		this.userType = userType;
	}
	
	

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	

	public EntityType getEntity() {
		return entity;
	}

	public void setEntity(EntityType entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "EmployeeCollection [id=" + id + ", employeeName=" + employeeName + ", mobileNumber=" + mobileNumber
				+ ", age=" + age + ", employeeId=" + employeeId + ", gender=" + gender + ", department=" + department
				+ ", reportingManager=" + reportingManager + ", emailId=" + emailId + ", companyId=" + companyId
				+ ", city=" + city + ", companyLocation=" + companyLocation + ", profileImageUrl=" + profileImageUrl
				+ ", isDiscarded=" + isDiscarded + ", isWorkFromHome=" + isWorkFromHome + ", countryCode=" + countryCode
				+ ", password=" + Arrays.toString(password) + ", userType=" + userType + "]";
	}

	

	

}
