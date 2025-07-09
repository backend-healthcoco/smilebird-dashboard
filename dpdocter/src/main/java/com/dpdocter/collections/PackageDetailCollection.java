package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Country;
import com.dpdocter.enums.PackageType;

@Document(collection = "package_detail_cl")
public class PackageDetailCollection  extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private PackageType packageName;
	
	@Field
	private Country country;
	
	@Field
	private Boolean discarded=Boolean.FALSE;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "PackageDetailCollection [id=" + id + ", packageName=" + packageName + ", country=" + country
				+ ", discarded=" + discarded + "]";
	}
	
	

}
