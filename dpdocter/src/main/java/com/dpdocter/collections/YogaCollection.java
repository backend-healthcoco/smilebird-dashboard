package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Disease;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.Injury;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.beans.YogMultilingual;
import com.dpdocter.beans.YogaDisease;
import com.dpdocter.beans.YogaTeacher;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FitnessEnum;
import com.dpdocter.enums.GenderSpecialityEnum;
import com.dpdocter.enums.LevelEnum;
import com.dpdocter.enums.PoseEnum;
import com.dpdocter.enums.YogaTypeEnum;

@Document(collection ="yoga_cl" )
public class YogaCollection extends GenericCollection {
	
	@Id
	private ObjectId id;

	@Field
	private FitnessEnum type;
	
	@Field
	private List<YogMultilingual> multilingualYoga;
	
	@Field
	private GenderSpecialityEnum gender;
	
	@Field
	private Integer duration;
	
	@Field
	private String imageUrl;
	
	@Field
	private String fullVideoUrl;
	
	@Field
	private String trailVideoUrl;
	
	@Field
	private String thumbnailUrl;
	
	@Field
	private YogaTeacher teacher;
	
	@Field
	private List<YogaTypeEnum> yogaType;
	
	@Field
	private Integer calories;
	
	@Field
	private PoseEnum pose;
	
	@Field
	private LevelEnum level;
	
	@Field
	private List<ObjectId> diseaseIds;
	
	@Field
	private List<ObjectId> diseasePrecautionIds;
	
	@Field
	private Integer priority;

	@Field
	private List<ObjectId> injuryPrecautionIds;
	
	@Field
	private Boolean discarded=false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public FitnessEnum getType() {
		return type;
	}

	public void setType(FitnessEnum type) {
		this.type = type;
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

	

	public List<YogMultilingual> getMultilingualYoga() {
		return multilingualYoga;
	}

	public void setMultilingualYoga(List<YogMultilingual> multilingualYoga) {
		this.multilingualYoga = multilingualYoga;
	}

	public List<YogaTypeEnum> getYogaType() {
		return yogaType;
	}

	public void setYogaType(List<YogaTypeEnum> yogaType) {
		this.yogaType = yogaType;
	}

	
	public Integer getCalories() {
		return calories;
	}

	public void setCalories(Integer calories) {
		this.calories = calories;
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	public List<ObjectId> getInjuryPrecautionIds() {
		return injuryPrecautionIds;
	}

	public void setInjuryPrecautionIds(List<ObjectId> injuryPrecautionIds) {
		this.injuryPrecautionIds = injuryPrecautionIds;
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

	
	
		
}
