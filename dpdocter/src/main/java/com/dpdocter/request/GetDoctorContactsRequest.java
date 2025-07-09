package com.dpdocter.request;

import java.util.List;

public class GetDoctorContactsRequest {
    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String updatedTime = "0";

    private Boolean discarded = true;

    private int page;

    private int size;

    private List<String> groups;

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

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public int getPage() {
	return page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public int getSize() {
	return size;
    }

    public void setSize(int size) {
	this.size = size;
    }

    public List<String> getGroups() {
	return groups;
    }

    public void setGroups(List<String> groups) {
	this.groups = groups;
    }

	public String getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "GetDoctorContactsRequest [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", updatedTime=" + updatedTime + ", discarded=" + discarded + ", page=" + page
				+ ", size=" + size + ", groups=" + groups + "]";
	}
}
