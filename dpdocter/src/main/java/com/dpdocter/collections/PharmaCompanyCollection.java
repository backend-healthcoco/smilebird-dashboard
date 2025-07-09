package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.response.ImageURLResponse;

@Document(collection = "pharma_company_cl")
public class PharmaCompanyCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private String mobileNumber;
	@Field
	private Address address;
	@Field
	private ImageURLResponse logo;
	@Field
	private String companyWebsiteURL;
	@Field
	private String emailAddress;
	@Indexed(unique = true)
	private String companyInitial;
	@Indexed(unique = true)
	private String userName;
	@Field
	private char[] password;
	@Field
	private char[] salt;
	@Field
	private String companyCode;
	@Field
	private Boolean isPasswordSet = false;
	@Field
	private Boolean isActivated = false;
	@Field
	private Boolean isVerified = false;
	@Field
	private ObjectId drugCompanyId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public char[] getSalt() {
		return salt;
	}

	public void setSalt(char[] salt) {
		this.salt = salt;
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

	public ObjectId getDrugCompanyId() {
		return drugCompanyId;
	}

	public void setDrugCompanyId(ObjectId drugCompanyId) {
		this.drugCompanyId = drugCompanyId;
	}

	@Override
	public String toString() {
		return "PharmaCompanyCollection [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", address="
				+ address + ", logo=" + logo + ", companyWebsiteURL=" + companyWebsiteURL + ", emailAddress="
				+ emailAddress + ", companyInitial=" + companyInitial + ", userName=" + userName + ", companyCode="
				+ companyCode + "]";
	}

}
