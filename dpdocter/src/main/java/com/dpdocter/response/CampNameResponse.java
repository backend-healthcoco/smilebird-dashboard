package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.collections.GenericCollection;

public class CampNameResponse extends GenericCollection {
	private String id;

	private String campName;

	private Boolean isDiscarded = false;

	private Date campDate;

	private String city;

	private Boolean isCamp = false;
	
	private List<String> associateDoctors;
	
	private List<String> associateDoctorIds;

	public Date getCampDate() {
		return campDate;
	}

	public void setCampDate(Date campDate) {
		this.campDate = campDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public List<String> getAssociateDoctors() {
		return associateDoctors;
	}

	public void setAssociateDoctors(List<String> associateDoctors) {
		this.associateDoctors = associateDoctors;
	}

	public List<String> getAssociateDoctorIds() {
		return associateDoctorIds;
	}

	public void setAssociateDoctorIds(List<String> associateDoctorIds) {
		this.associateDoctorIds = associateDoctorIds;
	}

}
