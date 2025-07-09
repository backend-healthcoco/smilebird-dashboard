package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class CampNameRequest extends GenericCollection {
	private String id;

	private String campName;

	private String city;

	private Boolean isDiscarded = false;

	private Date campDate;

	private Boolean isCamp = false;
	
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

	public List<String> getAssociateDoctorIds() {
		return associateDoctorIds;
	}

	public void setAssociateDoctorIds(List<String> associateDoctorIds) {
		this.associateDoctorIds = associateDoctorIds;
	}

}
