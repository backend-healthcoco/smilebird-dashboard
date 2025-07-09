package com.dpdocter.request;

import com.dpdocter.beans.Address;
import com.dpdocter.response.ImageURLResponse;

import common.util.web.JacksonUtil;

public class PharmaCompanySignupRequest {

	private String name;
	private String mobileNumber;
	private Address address;
	private ImageURLResponse logo;
	private String companyWebsiteURL;
	private String emailAddress;
	private String drugCompanyId;
	private String companyInitial;

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

	public String getDrugCompanyId() {
		return drugCompanyId;
	}

	public void setDrugCompanyId(String drugCompanyId) {
		this.drugCompanyId = drugCompanyId;
	}

	public static void main(String[] args) {
		System.out.println(JacksonUtil.obj2Json(new PharmaCompanySignupRequest()));
	}

}
