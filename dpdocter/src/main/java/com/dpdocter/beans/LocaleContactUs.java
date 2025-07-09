package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.LocaleContactStateType;

public class LocaleContactUs extends GenericCollection {

	private String id;

	private String registeredOwnerName;

	private String localeEmailAddress;

	private String localeName;

	private String city;

	private Address address;

	private String contactNumber;

	private String licenseNumber;

	private LocaleContactStateType contactStateType = LocaleContactStateType.APPROACH;

	private DeviceType deviceType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocaleEmailAddress() {
		return localeEmailAddress;
	}

	public void setLocaleEmailAddress(String localeEmailAddress) {
		this.localeEmailAddress = localeEmailAddress;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getRegisteredOwnerName() {
		return registeredOwnerName;
	}

	public void setRegisteredOwnerName(String registeredOwnerName) {
		this.registeredOwnerName = registeredOwnerName;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public LocaleContactStateType getContactStateType() {
		return contactStateType;
	}

	public void setContactStateType(LocaleContactStateType contactStateType) {
		this.contactStateType = contactStateType;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "LocaleContactUs [id=" + id + ", registeredOwnerName=" + registeredOwnerName + ", localeEmailAddress="
				+ localeEmailAddress + ", localeName=" + localeName + ", city=" + city + ", address=" + address
				+ ", contactNumber=" + contactNumber + ", licenseNumber=" + licenseNumber + ", contactStateType="
				+ contactStateType + "]";
	}

}
