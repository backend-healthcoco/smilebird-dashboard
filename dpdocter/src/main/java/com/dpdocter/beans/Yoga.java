package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FitnessEnum;
import com.dpdocter.enums.GenderSpecialityEnum;
import com.dpdocter.enums.LevelEnum;
import com.dpdocter.enums.PoseEnum;
import com.dpdocter.enums.YogaTypeEnum;

public class Yoga extends GenericCollection {
	
	private String id;

	private FitnessEnum type;
	
	private List<YogMultilingual> multilingualYoga;
		
	private Integer duration;
	
	private GenderSpecialityEnum gender;
	
	private String imageUrl;
	
	private String fullVideoUrl;
	
	private String trailVideoUrl;
	
	private String thumbnailUrl;
	
	private YogaTeacher teacher;
	
	private List<YogaTypeEnum> yogaType;
		
	private Double calories;
	
	private PoseEnum pose;
	
	private LevelEnum level;
	
	private List<YogaDisease> disease;
	
	private List<String> diseaseIds;
	
	private List<YogaDisease> diseasePrecaution;
	
	private List<String> diseasePrecautionIds;
	
	private Integer priority;
	
	private List<Injury> injuryPrecaution;
	
	private List<String> injuryPrecautionIds;
	
	private Boolean discarded=false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FitnessEnum getType() {
		return type;
	}

	public void setType(FitnessEnum type) {
		this.type = type;
	}

	


	public List<String> getDiseaseIds() {
		return diseaseIds;
	}

	public void setDiseaseIds(List<String> diseaseIds) {
		this.diseaseIds = diseaseIds;
	}

	public List<String> getDiseasePrecautionIds() {
		return diseasePrecautionIds;
	}

	public void setDiseasePrecautionIds(List<String> diseasePrecautionIds) {
		this.diseasePrecautionIds = diseasePrecautionIds;
	}

	public List<YogMultilingual> getMultilingualYoga() {
		return multilingualYoga;
	}

	public void setMultilingualYoga(List<YogMultilingual> multilingualYoga) {
		this.multilingualYoga = multilingualYoga;
	}


	

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getFullVideoUrl() {
		return fullVideoUrl;
	}

	public void setFullVideoUrl(String fullVideoUrl) {
		this.fullVideoUrl = fullVideoUrl;
	}

	public String getTrailVideoUrl() {
		return trailVideoUrl;
	}

	public void setTrailVideoUrl(String trailVideoUrl) {
		this.trailVideoUrl = trailVideoUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public YogaTeacher getTeacher() {
		return teacher;
	}

	public void setTeacher(YogaTeacher teacher) {
		this.teacher = teacher;
	}

	public PoseEnum getPose() {
		return pose;
	}

	public void setPose(PoseEnum pose) {
		this.pose = pose;
	}

	
	
	
		public LevelEnum getLevel() {
		return level;
	}

	public void setLevel(LevelEnum level) {
		this.level = level;
	}

		public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}

	
		public List<YogaDisease> getDisease() {
		return disease;
	}

	public void setDisease(List<YogaDisease> disease) {
		this.disease = disease;
	}

	public List<YogaDisease> getDiseasePrecaution() {
		return diseasePrecaution;
	}

	public void setDiseasePrecaution(List<YogaDisease> diseasePrecaution) {
		this.diseasePrecaution = diseasePrecaution;
	}

		public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	
	
	public List<Injury> getInjuryPrecaution() {
		return injuryPrecaution;
	}

	public void setInjuryPrecaution(List<Injury> injuryPrecaution) {
		this.injuryPrecaution = injuryPrecaution;
	}

	public List<YogaTypeEnum> getYogaType() {
		return yogaType;
	}

	public void setYogaType(List<YogaTypeEnum> yogaType) {
		this.yogaType = yogaType;
	}
	
	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	

	public GenderSpecialityEnum getGender() {
		return gender;
	}

	public void setGender(GenderSpecialityEnum gender) {
		this.gender = gender;
	}
	
	

	public List<String> getInjuryPrecautionIds() {
		return injuryPrecautionIds;
	}

	public void setInjuryPrecautionIds(List<String> injuryPrecautionIds) {
		this.injuryPrecautionIds = injuryPrecautionIds;
	}

	@Override
	public String toString() {
		return "Yoga [id=" + id + ", type=" + type + ", duration=" + duration
				+ ", imageUrl=" + imageUrl + ", fullVideoUrl=" + fullVideoUrl + ", trailVideoUrl=" + trailVideoUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", teacher=" + teacher + ", yogaType=" + yogaType + ", calories="
				+ calories + ", pose=" + pose + ", level=" + level + ", disease=" + disease + ", diseasePrecaution="
				+ diseasePrecaution + ", priority=" + priority + ", injuryPrecaution=" + injuryPrecaution 
				 + ", discarded=" + discarded + "]";
	}
	
	
		
}
