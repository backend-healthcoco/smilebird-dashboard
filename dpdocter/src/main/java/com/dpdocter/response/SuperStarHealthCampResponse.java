package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Doctor;

public class SuperStarHealthCampResponse {

	private String id;
	private String title;
	private Long startDate;
	private Long endDate;
	private Boolean discarded = Boolean.FALSE;
	private List<String> doctorId;
	private String branchId;
	private String schoolId;
	private String description;
	private List<Doctor> doctors;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(List<String> doctorId) {
		this.doctorId = doctorId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Doctor> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<Doctor> doctors) {
		this.doctors = doctors;
	}

}
