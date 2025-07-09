package com.dpdocter.request;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DOB;

/**
 * @author veeraj
 */

public class DoctorSignupRequest {

	private String title;

	private String firstName;

	private String emailAddress;

	private String mobileNumber;
	
	private String countryCode;

	private String gender;

	private DOB dob;

	private List<String> specialities;

	private List<String> services;

	private String locationName;

	private String country;

	private String state;

	private String city;

	private String streetAddress;
	
	private Double latitude;

	private Double longitude;
	
	private String locality;

	private String registerNumber;

	private String locationId;

	private String hospitalId;

	private List<String> roles;

	private Boolean isNutritionist = false;

	private Boolean isSuperAdmin = false;

	private String mrCode;

	private String cityId;

	public Boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(Boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
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

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmailAddress() {
		return emailAddress != null ? emailAddress.toLowerCase() : emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public String getMrCode() {
		return mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
	

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	@Override
	public String toString() {
		return "DoctorSignupRequest [title=" + title + ", firstName=" + firstName + ", emailAddress=" + emailAddress
				+ ", mobileNumber=" + mobileNumber + ", countryCode=" + countryCode + ", gender=" + gender + ", dob="
				+ dob + ", specialities=" + specialities + ", services=" + services + ", locationName=" + locationName
				+ ", country=" + country + ", state=" + state + ", city=" + city + ", streetAddress=" + streetAddress
				+ ", registerNumber=" + registerNumber + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", roles=" + roles + ", isNutritionist=" + isNutritionist + ", isSuperAdmin=" + isSuperAdmin
				+ ", mrCode=" + mrCode + ", cityId=" + cityId + "]";
	}

	
}
