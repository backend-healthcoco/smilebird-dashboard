package com.dpdocter.response;

import com.dpdocter.beans.Address;
import com.dpdocter.collections.GenericCollection;

public class PharmaCompanyResponse extends GenericCollection {

	private String id;
	private String name;
	private String mobileNumber;
	private Address address;
	private ImageURLResponse logo;
	private String companyWebsiteURL;
	private String emailAddress;
	private String companyInitial;
	private String userName;
	private String companyCode;
	private Boolean isPasswordSet = false;
	private Boolean isActivated = false;
	private Boolean isVerified = false;
	private String drugCompanyId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public ImageURLResponse getLogo() {
		return logo;
	}

	public void setLogo(ImageURLResponse logo) {
		this.logo = logo;
	}

	public String getCompanyWebsiteURL() {
		return companyWebsiteURL;
	}

	public void setCompanyWebsiteURL(String companyWebsiteURL) {
		this.companyWebsiteURL = companyWebsiteURL;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getCompanyInitial() {
		return companyInitial;
	}

	public void setCompanyInitial(String companyInitial) {
		this.companyInitial = companyInitial;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public Boolean getIsPasswordSet() {
		return isPasswordSet;
	}

	public void setIsPasswordSet(Boolean isPasswordSet) {
		this.isPasswordSet = isPasswordSet;
	}

	public Boolean getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getDrugCompanyId() {
		return drugCompanyId;
	}

	public void setDrugCompanyId(String drugCompanyId) {
		this.drugCompanyId = drugCompanyId;
	}

	@Override
	public String toString() {
		return "PharmaCompanyResponse [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", address="
				+ address + ", logo=" + logo + ", companyWebsiteURL=" + companyWebsiteURL + ", emailAddress="
				+ emailAddress + ", companyInitial=" + companyInitial + ", userName=" + userName + ", companyCode="
				+ companyCode + ", isPasswordSet=" + isPasswordSet + "]";
	}

}
