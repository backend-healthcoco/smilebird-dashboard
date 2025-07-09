package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;

@Document(collection = "camp_name_cl")
public class CampNameCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String campName;

	@Field
	private Date campDate;
	
	@Field
	private Address campAddress;

	@Field
	private Boolean isDiscarded = false;
	
	@Field
	private String city;
	
	@Field
	private Boolean isCamp = false;
	
	@Field
	private List<ObjectId> associateDoctorIds;


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCampName() {
		return campName;
	}

	public void setCampName(String campName) {
		this.campName = campName;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public Date getCampDate() {
		return campDate;
	}

	public void setCampDate(Date campDate) {
		this.campDate = campDate;
	}

	public Address getCampAddress() {
		return campAddress;
	}

	public void setCampAddress(Address campAddress) {
		this.campAddress = campAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Boolean getIsCamp() {
		return isCamp;
	}

	public void setIsCamp(Boolean isCamp) {
		this.isCamp = isCamp;
	}

	public List<ObjectId> getAssociateDoctorIds() {
		return associateDoctorIds;
	}

	public void setAssociateDoctorIds(List<ObjectId> associateDoctorIds) {
		this.associateDoctorIds = associateDoctorIds;
	}

}
