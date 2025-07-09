package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Activity;
import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.School;
import com.dpdocter.beans.Stories;
import com.dpdocter.enums.DurationEnum;
import com.dpdocter.enums.StandardsEnum;

@Document(collection = "today_session_cl")
public class TodaySessionCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private ObjectId schoolId;
	
	@Field
	private List<StandardsEnum> standards;
	
	@Field
	private ObjectId activityId;
	
	@Field
	private ObjectId storiesId;
	
	@Field
	private ObjectId mindfulnessId;
	
	@Field
	private Date fromDate;
	
	@Field
	private Date toDate;
	
	@Field
	private Boolean discarded=Boolean.FALSE;
	
	@Field
	private DurationEnum duration;

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

	public ObjectId getActivityId() {
		return activityId;
	}

	public void setActivityId(ObjectId activityId) {
		this.activityId = activityId;
	}

	public ObjectId getStoriesId() {
		return storiesId;
	}

	public void setStoriesId(ObjectId storiesId) {
		this.storiesId = storiesId;
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

	
	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	
	public DurationEnum getDuration() {
		return duration;
	}

	public void setDuration(DurationEnum duration) {
		this.duration = duration;
	}
	
	public List<StandardsEnum> getStandards() {
		return standards;
	}

	public void setStandards(List<StandardsEnum> standards) {
		this.standards = standards;
	}

	@Override
	public String toString() {
		return "TodaySessionCollection [id=" + id + ", schoolId=" + schoolId + ", standards=" + standards + ", activityId="
				+ activityId + ", storiesId=" + storiesId + ", mindfulnessId=" + mindfulnessId + ", fromDate="
				+ fromDate + ", toDate=" + toDate + ", discarded=" + discarded + ", duration=" + duration + "]";
	}

	
	
	
}
