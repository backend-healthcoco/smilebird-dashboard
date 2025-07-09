package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "company_location_cl")
public class CompanyLocationCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String locationName;
	@Field
	private String companyName;
	@Field
	private ObjectId companyId;
	@Field
	private String timezone;
	@Field
	private Boolean isDiscarded = false;

//city	
	public ObjectId getId() {
		return id;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public ObjectId getCompanyId() {
		return companyId;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setCompanyId(ObjectId companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@Override
	public String toString() {
		return "CompanyLocationCollection [id=" + id + ", locationName=" + locationName + ", companyName=" + companyName
				+ ", companyId=" + companyId + ", timezone=" + timezone + ", isDiscarded=" + isDiscarded + "]";
	}

}
