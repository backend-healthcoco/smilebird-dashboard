package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.LocaleContactStateType;

@Document(collection = "locale_contact_us_cl")
public class LocaleContactUsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String registeredOwnerName;

	@Field
	private String localeEmailAddress;

	@Field
	private String localeName;

	@Field
	private String city;

	@Field
	private Address address;

	@Indexed(unique = true)
	private String contactNumber;

	@Field
	private String licenseNumber;

	@Field
	private LocaleContactStateType contactStateType;

	@Field
	private DeviceType deviceType;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRegisteredOwnerName() {
		return registeredOwnerName;
	}

	public void setRegisteredOwnerName(String registeredOwnerName) {
		this.registeredOwnerName = registeredOwnerName;
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
		return "LocaleContactUsCollection [id=" + id + ", registeredOwnerName=" + registeredOwnerName
				+ ", localeEmailAddress=" + localeEmailAddress + ", localeName=" + localeName + ", city=" + city
				+ ", address=" + address + ", contactNumber=" + contactNumber + ", licenseNumber=" + licenseNumber
				+ ", contactStateType=" + contactStateType + ", deviceType=" + deviceType + "]";
	}

}
