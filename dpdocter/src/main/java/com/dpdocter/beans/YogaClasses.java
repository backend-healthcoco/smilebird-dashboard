package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FitnessEnum;
import com.dpdocter.enums.GenderSpecialityEnum;
import com.dpdocter.enums.LevelEnum;
import com.dpdocter.enums.YogaTypeEnum;

public class YogaClasses extends GenericCollection {

	private String id;
	
	private List<String> yogaIds;
	
	private GenderSpecialityEnum gender;
	
	private List<String> injuryPrecautionIds;
	
	
	
	private List<YogaClassesMultilingual> yogaClassMultilingual; 
	
	private LevelEnum level;
	
	private Integer duration;
	
	private Double calories;
	
	private Boolean hasMultipleEntities=false;
	
	private List<Yoga> yoga;
	
	private String ImageUrl;
	
	private String thumbnailUrl;
	
	private FitnessEnum type;
	
	private YogaTypeEnum yogaType;
	
	private List<YogaTeacher> teacher;
	
	private List<String> teacherIds;
	
	private List<YogaDisease> disease;
	
	private List<String> diseaseIds;
	
	private List<YogaDisease> diseasePrecaution;
	
	private List<String> diseasePrecautionIds;
	
	private List<Injury> injuryPrecaution;
	
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

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Boolean getHasMultipleEntities() {
		return hasMultipleEntities;
	}

	public void setHasMultipleEntities(Boolean hasMultipleEntities) {
		this.hasMultipleEntities = hasMultipleEntities;
	}

	public List<Yoga> getYoga() {
		return yoga;
	}

	public void setYoga(List<Yoga> yoga) {
		this.yoga = yoga;
	}

	

	public String getImageUrl() {
		return ImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public FitnessEnum getType() {
		return type;
	}

	public void setType(FitnessEnum type) {
		this.type = type;
	}

	public List<String> getYogaIds() {
		return yogaIds;
	}

	public void setYogaIds(List<String> yogaIds) {
		this.yogaIds = yogaIds;
	}

	public GenderSpecialityEnum getGender() {
		return gender;
	}

	public void setGender(GenderSpecialityEnum gender) {
		this.gender = gender;
	}

	public YogaTypeEnum getYogaType() {
		return yogaType;
	}

	public void setYogaType(YogaTypeEnum yogaType) {
		this.yogaType = yogaType;
	}

	public List<YogaTeacher> getTeacher() {
		return teacher;
	}

	public void setTeacher(List<YogaTeacher> teacher) {
		this.teacher = teacher;
	}

	public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}
	
	
	public List<String> getInjuryPrecautionIds() {
		return injuryPrecautionIds;
	}

	public void setInjuryPrecautionIds(List<String> injuryPrecautionIds) {
		this.injuryPrecautionIds = injuryPrecautionIds;
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

	public List<Injury> getInjuryPrecaution() {
		return injuryPrecaution;
	}

	public void setInjuryPrecaution(List<Injury> injuryPrecaution) {
		this.injuryPrecaution = injuryPrecaution;
	}

		public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<YogaClassesMultilingual> getYogaClassMultilingual() {
		return yogaClassMultilingual;
	}

	public void setYogaClassMultilingual(List<YogaClassesMultilingual> yogaClassMultilingual) {
		this.yogaClassMultilingual = yogaClassMultilingual;
	}
	

	public List<String> getTeacherIds() {
		return teacherIds;
	}

	public void setTeacherIds(List<String> teacherIds) {
		this.teacherIds = teacherIds;
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

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	@Override
	public String toString() {
		return "YogaClasses [id=" + id + ", yogaClassMultilingual=" + yogaClassMultilingual + ", level=" + level
				+ ", duration=" + duration + ", calories=" + calories + ", hasMultipleEntities=" + hasMultipleEntities
				+ ", yoga=" + yoga  + ", type=" + type + ", yogaType=" + yogaType
				+ ", teacher=" + teacher + ", disease=" + disease + ", diseasePrecaution=" + diseasePrecaution
				+ ", injuryPrecaution=" + injuryPrecaution + ", discarded=" + discarded + "]";
	}

	
	
	
	
}
