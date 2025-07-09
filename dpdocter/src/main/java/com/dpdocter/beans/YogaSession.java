package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LevelEnum;

public class YogaSession extends GenericCollection {

	private String id;
	
	private List<YogaSessionMultilingual> yogaSessionMultilingual;
	
	private LevelEnum level;
	
	private Integer duration;
	
	private Double calories;
	
	private List<YogaClasses> yogaClasses;
	
	private List<String>yogaClassesIds;
	
	private String imageUrl;
	
	private String thumbnailUrl;
	
	private YogaTeacher teacher;
	
	private String teacherId;
	
	private List<YogaDisease> disease;
	
	private List<String>diseaseIds;
	
	private List<YogaDisease> precaution;
	
	private List<String>precautionIds;
	
	private Boolean isPaid=false;
	
	private Boolean discarded=false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public LevelEnum getLevel() {
		return level;
	}

	public void setLevel(LevelEnum level) {
		this.level = level;
	}

	


	public List<YogaClasses> getYogaClasses() {
		return yogaClasses;
	}

	public void setYogaClasses(List<YogaClasses> yogaClasses) {
		this.yogaClasses = yogaClasses;
	}

	

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}

	public List<YogaSessionMultilingual> getYogaSessionMultilingual() {
		return yogaSessionMultilingual;
	}

	public void setYogaSessionMultilingual(List<YogaSessionMultilingual> yogaSessionMultilingual) {
		this.yogaSessionMultilingual = yogaSessionMultilingual;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public YogaTeacher getTeacher() {
		return teacher;
	}

	public void setTeacher(YogaTeacher teacher) {
		this.teacher = teacher;
	}



	public List<YogaDisease> getDisease() {
		return disease;
	}

	public void setDisease(List<YogaDisease> disease) {
		this.disease = disease;
	}

	public List<YogaDisease> getPrecaution() {
		return precaution;
	}

	public void setPrecaution(List<YogaDisease> precaution) {
		this.precaution = precaution;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	public List<String> getYogaClassesIds() {
		return yogaClassesIds;
	}

	public void setYogaClassesIds(List<String> yogaClassesIds) {
		this.yogaClassesIds = yogaClassesIds;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	

	public List<String> getDiseaseIds() {
		return diseaseIds;
	}

	public void setDiseaseIds(List<String> diseaseIds) {
		this.diseaseIds = diseaseIds;
	}

	public List<String> getPrecautionIds() {
		return precautionIds;
	}

	public void setPrecautionIds(List<String> precautionIds) {
		this.precautionIds = precautionIds;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}
	
	

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	@Override
	public String toString() {
		return "YogaSession [id=" + id + ", yogaSessionMultilingual=" + yogaSessionMultilingual + ", level=" + level
				+ ", duration=" + duration + ", calories=" + calories + ", yogaClasses=" + yogaClasses
				+ ", imageUrl=" + imageUrl + ", teacher=" + teacher + ", disease=" + disease + ", precaution="
				+ precaution + ", discarded=" + discarded + "]";
	}


}
