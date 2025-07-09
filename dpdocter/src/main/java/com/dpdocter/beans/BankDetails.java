package com.dpdocter.beans;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AccountType;


public class BankDetails extends GenericCollection{
	
	private String id;
	
	private String doctorId;
	
	private String doctorName;
	
	private String accountholderName;
	
	private String accountNumber;
	
	private String ifscNumber;
	
	private String panCardNumber;
	
	private AccountType accountType;
	
	private String bankName;
	
	private String branchCity;
	
	private String mobileNumber;
	
	private String emailAddress;
	
	private String razorPayAccountId;
	
	private String city;
	
	private String speciality;
	
	private Boolean isRegistrationDetailsVerified =false;
	
	
	private Boolean isPhotoIdVerified =false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountholderName() {
		return accountholderName;
	}

	public void setAccountholderName(String accountholderName) {
		this.accountholderName = accountholderName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIfscNumber() {
		return ifscNumber;
	}

	public void setIfscNumber(String ifscNumber) {
		this.ifscNumber = ifscNumber;
	}

	public String getPanCardNumber() {
		return panCardNumber;
	}

	public void setPanCardNumber(String panCardNumber) {
		this.panCardNumber = panCardNumber;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}
	
	

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	
	

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	
	
	
	public String getRazorPayAccountId() {
		return razorPayAccountId;
	}

	public void setRazorPayAccountId(String razorPayAccountId) {
		this.razorPayAccountId = razorPayAccountId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	
	
	

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	

	public Boolean getIsRegistrationDetailsVerified() {
		return isRegistrationDetailsVerified;
	}

	public void setIsRegistrationDetailsVerified(Boolean isRegistrationDetailsVerified) {
		this.isRegistrationDetailsVerified = isRegistrationDetailsVerified;
	}

	public Boolean getIsPhotoIdVerified() {
		return isPhotoIdVerified;
	}

	public void setIsPhotoIdVerified(Boolean isPhotoIdVerified) {
		this.isPhotoIdVerified = isPhotoIdVerified;
	}

	@Override
	public String toString() {
		return "BankDetails [id=" + id + ", accountholderName=" + accountholderName + ", accountNumber=" + accountNumber
				+ ", ifscNumber=" + ifscNumber + ", panCardNumber=" + panCardNumber + ", accountType=" + accountType
				+ ", bankName=" + bankName + ", branchCity=" + branchCity + "]";
	}
	
	 
}
