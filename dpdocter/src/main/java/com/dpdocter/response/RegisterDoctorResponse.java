package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Role;

public class RegisterDoctorResponse {

    private String userId;

    private String firstName;

    private String lastName;

    private String middleName;

    private String userName;

    private char[] password;

    private String emailAddress;

    private String mobileNumber;

    private String gender;

    private DOB dob;

    private String phoneNumber;

    private String imageUrl;

    private List<String> specialization;

    private String locationId;

    private String hospitalId;

    private String registerNumber;

    private Role role;

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
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

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
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

    public String getRegisterNumber() {
	return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
	this.registerNumber = registerNumber;
    }

    public Role getRole() {
	return role;
    }

    public void setRole(Role role) {
	this.role = role;
    }

    @Override
    public String toString() {
	return "RegisterDoctorResponse [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName + ", userName="
		+ userName + ", password=" + password + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", dob="
		+ dob + ", phoneNumber=" + phoneNumber + ", imageUrl=" + imageUrl + ", specialization=" + specialization + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", registerNumber=" + registerNumber + ", role=" + role + "]";
    }
}
