package com.dpdocter.response;

import java.util.List;

import com.dpdocter.enums.UserState;

public class DoctorSearchResponse {
	private String id;
	private String doctorName;
	private List<String> specialities;
	private List<String> superSpecialities;
	private Boolean isActive = false;
	private Boolean isVerified = false;
	private String emailAddress;
	private String mobileNumber;
	private UserState userState = UserState.USERSTATECOMPLETE;

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
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

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<String> getSuperSpecialities() {
		return superSpecialities;
	}

	public void setSuperSpecialities(List<String> superSpecialities) {
		this.superSpecialities = superSpecialities;
	}

}
