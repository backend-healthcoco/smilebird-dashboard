package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ConfexUserState;
import com.dpdocter.enums.EntityType;

public class CompanyDetail extends GenericCollection {

	private String id;

	private String companyName;

	private String companyNumber;

	private String companyLogoImageUrl;

	private String companyEmailId;

	private String companyWebsite;

	private String companyAddress;

	private String city;

	private String state;

	private String country;

	private int numberOfEmployees;

	private String adminName;

	private ConfexUserState userType = ConfexUserState.ADMIN;

	private Boolean isActive = Boolean.FALSE;

	private Boolean isDiscarded = Boolean.FALSE;
	
	private EntityType entity;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
		return "CompanyDetail [id=" + id + ", companyName=" + companyName + ", companyNumber=" + companyNumber
				+ ", companyLogoImageUrl=" + companyLogoImageUrl + ", companyEmailId=" + companyEmailId
				+ ", companyWebsite=" + companyWebsite + ", companyAddress=" + companyAddress + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", numberOfEmployees=" + numberOfEmployees
				+ ", adminName=" + adminName + ", userType=" + userType + ", isActive=" + isActive + ", isDiscarded="
				+ isDiscarded + "]";
	}

}
