package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.StandardsEnum;

public class ActivityAssign extends GenericCollection{

	private String id;
	
	private String schoolId;
	
	private String branchId;
	
	private School school;
	
	private SchoolBranch schoolBranch;

	
	private String activityId;
	
	private Activity activity;
	
	private Date fromDate;
	
	private Date toDate;
	
	private List<StandardsEnum> standard;
	
	private Boolean isCompleted=false;
	
	private Boolean discarded=false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
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

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
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
		return "ActivityAssign [schoolId=" + schoolId + ", branchId=" + branchId + ", activity=" + activity
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", standard=" + standard + ", isCompleted="
				+ isCompleted + "]";
	}
	
	
	
}
