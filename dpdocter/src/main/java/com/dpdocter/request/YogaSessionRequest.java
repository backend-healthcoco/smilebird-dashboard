package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.YogaClasses;
import com.dpdocter.beans.YogaDisease;
import com.dpdocter.beans.YogaSessionMultilingual;
import com.dpdocter.beans.YogaTeacher;
import com.dpdocter.enums.LevelEnum;

public class YogaSessionRequest {

	private String id;
	
	private List<YogaSessionMultilingual> yogaSessionMultilingual;
	
	private LevelEnum level;
	
	private Integer duration;
	
	private Double calories;
	
	private List<String>yogaClassesIds;
	
	private String coverImageUrl;
	
	private YogaTeacher teacher;
	
	private List<String>diseaseId;
	
	private List<String> precautionId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<YogaSessionMultilingual> getYogaSessionMultilingual() {
		return yogaSessionMultilingual;
	}

	public void setYogaSessionMultilingual(List<YogaSessionMultilingual> yogaSessionMultilingual) {
		this.yogaSessionMultilingual = yogaSessionMultilingual;
	}

	public LevelEnum getLevel() {
		return level;
	}

	public void setLevel(LevelEnum level) {
		this.level = level;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}

	public List<String> getYogaClassesIds() {
		return yogaClassesIds;
	}

	public void setYogaClassesIds(List<String> yogaClassesIds) {
		this.yogaClassesIds = yogaClassesIds;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public YogaTeacher getTeacher() {
		return teacher;
	}

	public void setTeacher(YogaTeacher teacher) {
		this.teacher = teacher;
	}

	public List<String> getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(List<String> diseaseId) {
		this.diseaseId = diseaseId;
	}

	public List<String> getPrecautionId() {
		return precautionId;
	}

	public void setPrecautionId(List<String> precautionId) {
		this.precautionId = precautionId;
	}

	@Override
	public String toString() {
		return "YogaSessionRequest [id=" + id + ", yogaSessionMultilingual=" + yogaSessionMultilingual + ", level="
				+ level + ", duration=" + duration + ", calories=" + calories + ", yogaClassesIds=" + yogaClassesIds
				+ ", coverImageUrl=" + coverImageUrl + ", teacher=" + teacher + ", diseaseId=" + diseaseId
				+ ", precautionId=" + precautionId + "]";
	}
	
	

}
