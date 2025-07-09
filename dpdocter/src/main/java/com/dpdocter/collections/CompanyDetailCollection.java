package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.EntityType;
import com.dpdocter.enums.ConfexUserState;

//for covid project

@Document(collection = "company_cl")
public class CompanyDetailCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String companyName;

	@Field
	private String companyNumber;

	@Field
	private String companyLogoImageUrl;

	@Field
	private String companyEmailId;

	@Field
	private String companyWebsite;

	@Field
	private String companyAddress;

	@Field
	private String city;

	@Field
	private String state;

	@Field
	private String country;

	@Field
	private int numberOfEmployees;

	@Field
	private String adminName;
	
	@Field
	private ConfexUserState userType = ConfexUserState.ADMIN;

	@Field
	private Boolean isActive = Boolean.FALSE;

	@Field
	private Boolean isDiscarded = Boolean.FALSE;
	
	@Field
	private EntityType entity;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public String getCompanyLogoImageUrl() {
		return companyLogoImageUrl;
	}

	public void setCompanyLogoImageUrl(String companyLogoImageUrl) {
		this.companyLogoImageUrl = companyLogoImageUrl;
	}

	public String getCompanyEmailId() {
		return companyEmailId;
	}

	public void setCompanyEmailId(String companyEmailId) {
		this.companyEmailId = companyEmailId;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getNumberOfEmployees() {
		return numberOfEmployees;
	}

	public void setNumberOfEmployees(int numberOfEmployees) {
		this.numberOfEmployees = numberOfEmployees;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	@Override
	public String toString() {
		return "CompanyDetailCollection [id=" + id + ", companyName=" + companyName + ", companyNumber=" + companyNumber
				+ ", companyLogoImageUrl=" + companyLogoImageUrl + ", companyEmailId=" + companyEmailId
				+ ", companyWebsite=" + companyWebsite + ", companyAddress=" + companyAddress + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", numberOfEmployees=" + numberOfEmployees
				+ ", adminName=" + adminName + ", isActive=" + isActive + ", isDiscarded=" + isDiscarded + "]";
	}

	

}
