package com.dpdocter.request;

import com.dpdocter.beans.Address;

public class AddEditLocaleAddressDetailsRequest {

	private String id;
	private String localeName;
	private Address address;
	private String localeAddress;

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getLocaleAddress() {
		return localeAddress;
	}

	public void setLocaleAddress(String localeAddress) {
		this.localeAddress = localeAddress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "AddEditLocaleAddressDetailsRequest [localeName=" + localeName + ", address=" + address
				+ ", localeAddress=" + localeAddress + "]";
	}

}
