package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DurationEnum;
import com.dpdocter.enums.StandardsEnum;

public class TodaySession extends GenericCollection {

	private String id;
	
	private String schoolId;
	
	private List<StandardsEnum> standards;
	
	private String activityId;
	
	private String storiesId;
	
	private String mindfulnessId;
	
	private Date fromDate;
	
	private Date toDate;
	
	//private DurationEnum duration;
	
	private Boolean discarded=Boolean.FALSE;

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

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getStoriesId() {
		return storiesId;
	}

	public void setStoriesId(String storiesId) {
		this.storiesId = storiesId;
	}

	public String getMindfulnessId() {
		return mindfulnessId;
	}

	public void setMindfulnessId(String mindfulnessId) {
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

	
	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	public List<StandardsEnum> getStandards() {
		return standards;
	}

	public void setStandards(List<StandardsEnum> standards) {
		this.standards = standards;
	}

	@Override
	public String toString() {
		return "TodaySession [id=" + id + ", schoolId=" + schoolId + ", standards=" + standards + ", activityId="
				+ activityId + ", storiesId=" + storiesId + ", mindfulnessId=" + mindfulnessId + ", fromDate="
				+ fromDate + ", toDate=" + toDate + ", discarded=" + discarded + "]";
	}

	
	
}
