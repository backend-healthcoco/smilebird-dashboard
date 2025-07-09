package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.School;
import com.dpdocter.beans.SchoolBranch;

import com.dpdocter.enums.StandardsEnum;

@Document(collection = "mindfulness_assign_cl")
public class MindfulnessAssignCollection extends GenericCollection {

	@Id
	private ObjectId id;
	
	@Field
	private ObjectId schoolId;
	
	@Field
	private ObjectId branchId;
	
	@Field
	private School school;
	
	@Field
	private SchoolBranch schoolBranch;
	
	@Field
	private ObjectId mindfulnessId;
	
	@Field
	private Mindfulness mindfulness;
	
	@Field
	private Date fromDate;
	
	@Field
	private Date toDate;
	
	@Field
	private List<StandardsEnum> standard;
	
	@Field
	private Boolean isCompleted=false;
	
	@Field
	private Boolean discarded=false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	
	public ObjectId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(ObjectId schoolId) {
		this.schoolId = schoolId;
	}

	public ObjectId getBranchId() {
		return branchId;
	}

	public void setBranchId(ObjectId branchId) {
		this.branchId = branchId;
	}

	public ObjectId getMindfulnessId() {
		return mindfulnessId;
	}

	public void setMindfulnessId(ObjectId mindfulnessId) {
		this.mindfulnessId = mindfulnessId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	
	public List<StandardsEnum> getStandard() {
		return standard;
	}

	public void setStandard(List<StandardsEnum> standard) {
		this.standard = standard;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	
	public Mindfulness getMindfulness() {
		return mindfulness;
	}

	public void setMindfulness(Mindfulness mindfulness) {
		this.mindfulness = mindfulness;
	}
	
	

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public SchoolBranch getSchoolBranch() {
		return schoolBranch;
	}

	public void setSchoolBranch(SchoolBranch schoolBranch) {
		this.schoolBranch = schoolBranch;
	}

	@Override
	public String toString() {
		return "MindfulnessAssignCollection [id=" + id + ", schoolId=" + schoolId + ", branchId=" + branchId
				+ ", mindfulnessId=" + mindfulnessId + ", fromDate=" + fromDate + ", toDate=" + toDate + ", standard="
				+ standard + ", isCompleted=" + isCompleted + "]";
	}
	
	
}
