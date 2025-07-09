package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.GenderType;

/**
 * 
 * @author parag
 *
 */

public class DoctorContactUs extends GenericCollection {

	/*
	 * title, firstName, userName,gender, emailAddress, mobileNumber,
	 * specialities, city, userCurrentState(value = VERIFIED, APPROACH,
	 * INTERESTED, NOT INTERESTED, SIGNED UP), Boolean isVerified, Boolean
	 * toList
	 */

	private String id;
	private String title;
	private String firstName;
	private String userName;
	private GenderType gender;
	private String emailAddress;
	private String mobileNumber;
	private String city;
	private List<String> specialities;
	private DoctorContactStateType contactState = DoctorContactStateType.APPROACH;
	private Date contactLaterOnDate;
	private Boolean isVerified;
	private Boolean toList;
	private String deviceType;

	public String getId() {
		return id;
	}

	public Date getContactLaterOnDate() {
		return contactLaterOnDate;
	}

	public void setContactLaterOnDate(Date contactLaterOnDate) {
		this.contactLaterOnDate = contactLaterOnDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
		this.gender = gender;
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

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public DoctorContactStateType getContactState() {
		return contactState;
	}

	public void setContactState(DoctorContactStateType contactState) {
		this.contactState = contactState;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getToList() {
		return toList;
	}

	public void setToList(Boolean toList) {
		this.toList = toList;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "DoctorContactUs [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName=" + userName
				+ ", gender=" + gender + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", city="
				+ city + ", specialities=" + specialities + ", contactState=" + contactState + ", isVerified="
				+ isVerified + ", toList=" + toList + "]";
	}

}
