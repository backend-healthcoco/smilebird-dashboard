package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Injury;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.beans.Yoga;
import com.dpdocter.beans.YogaClassesMultilingual;
import com.dpdocter.beans.YogaDisease;
import com.dpdocter.beans.YogaTeacher;
import com.dpdocter.enums.FitnessEnum;
import com.dpdocter.enums.GenderSpecialityEnum;
import com.dpdocter.enums.LevelEnum;
import com.dpdocter.enums.YogaTypeEnum;

@Document(collection = "yoga_classes_cl")
public class YogaClassesCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private List<YogaClassesMultilingual> yogaClassMultilingual; 
	@Field
	private LevelEnum level;
	@Field
	private GenderSpecialityEnum gender;
	
	@Field
	private Integer duration;
	@Field
	private Double calories;
	@Field
	private Boolean hasMultipleEntities=false;
	@Field
	private String imageUrl;
	@Field
	private String thumbnailUrl;
	@Field
	private FitnessEnum type;
	@Field
	private List<ObjectId> yogaIds;
	
	@Field
	private List<ObjectId>teacherIds;
	@Field
	private Boolean isPaid=false;
	@Field
	private YogaTypeEnum yogaType;
	@Field	
	private List<ObjectId> diseaseIds;
	@Field
	private List<ObjectId> diseasePrecautionIds;
	@Field	
	private List<ObjectId> injuryPrecautionIds;
	@Field
	private Boolean discarded=false;
	
	
	public List<YogaClassesMultilingual> getYogaClassMultilingual() {
		return yogaClassMultilingual;
	}
	public void setYogaClassMultilingual(List<YogaClassesMultilingual> yogaClassMultilingual) {
		this.yogaClassMultilingual = yogaClassMultilingual;
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
	public Boolean getHasMultipleEntities() {
		return hasMultipleEntities;
	}
	public void setHasMultipleEntities(Boolean hasMultipleEntities) {
		this.hasMultipleEntities = hasMultipleEntities;
	}
	
	public FitnessEnum getType() {
		return type;
	}
	public void setType(FitnessEnum type) {
		this.type = type;
	}
	public YogaTypeEnum getYogaType() {
		return yogaType;
	}
	public void setYogaType(YogaTypeEnum yogaType) {
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
	
	
	
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
	
	public Boolean getIsPaid() {
		return isPaid;
	}
	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public List<ObjectId> getYogaIds() {
		return yogaIds;
	}
	public void setYogaIds(List<ObjectId> yogaIds) {
		this.yogaIds = yogaIds;
	}
	public List<ObjectId> getDiseaseIds() {
		return diseaseIds;
	}
	public void setDiseaseIds(List<ObjectId> diseaseIds) {
		this.diseaseIds = diseaseIds;
	}
	public List<ObjectId> getDiseasePrecautionIds() {
		return diseasePrecautionIds;
	}
	public void setDiseasePrecautionIds(List<ObjectId> diseasePrecautionIds) {
		this.diseasePrecautionIds = diseasePrecautionIds;
	}
	public List<ObjectId> getInjuryPrecautionIds() {
		return injuryPrecautionIds;
	}
	public void setInjuryPrecautionIds(List<ObjectId> injuryPrecautionIds) {
		this.injuryPrecautionIds = injuryPrecautionIds;
	}
	public List<ObjectId> getTeacherIds() {
		return teacherIds;
	}
	public void setTeacherIds(List<ObjectId> teacherIds) {
		this.teacherIds = teacherIds;
	}
	
	
	


}
