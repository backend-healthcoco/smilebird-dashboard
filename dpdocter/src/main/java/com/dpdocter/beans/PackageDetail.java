package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PackageType;

public class PackageDetail extends GenericCollection {
	
	private String id;

	private PackageType packageName;
	
	private Country country;
	
	private Boolean discarded=Boolean.FALSE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "PackageDetail [id=" + id + ", packageName=" + packageName + ", country=" + country + ", discarded="
				+ discarded + "]";
	}

		

}
