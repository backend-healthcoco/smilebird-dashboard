package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.School;
import com.dpdocter.beans.SchoolBranch;

@Document(collection = "super_star_health_camp_cl")
public class SuperStarHealthCampCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private Long startDate;
	@Field
	private Long endDate;
	@Field
	private Boolean discarded = Boolean.FALSE;
	@Field
	private List<ObjectId> doctorId;
	@Field
	private List<Doctor> doctors;
	@Field
	private ObjectId branchId;
	@Field
	private School school;
	@Field
	private SchoolBranch branch;
	@Field
	private ObjectId schoolId;
	@Field
	private String description;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public List<ObjectId> getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(List<ObjectId> doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getBranchId() {
		return branchId;
	}

	public void setBranchId(ObjectId branchId) {
		this.branchId = branchId;
	}

	public ObjectId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(ObjectId schoolId) {
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

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public SchoolBranch getBranch() {
		return branch;
	}

	public void setBranch(SchoolBranch branch) {
		this.branch = branch;
	}

	@Override
	public String toString() {
		return "SuperStarHealthCampCollection [id=" + id + ", title=" + title + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", discarded=" + discarded + ", doctorId=" + doctorId + ", branchId="
				+ branchId + ", schoolId=" + schoolId + ", description=" + description + "]";
	}

}
