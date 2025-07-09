package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.YogaClasses;
import com.dpdocter.beans.YogaDisease;
import com.dpdocter.beans.YogaSessionMultilingual;
import com.dpdocter.beans.YogaTeacher;
import com.dpdocter.enums.LevelEnum;

@Document(collection = "yoga_session_cl")
public class YogaSessionCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private List<YogaSessionMultilingual> yogaSessionMultilingual;
	@Field
	private LevelEnum level;
	@Field
	private Integer duration;
	@Field
	private Double calories;
	@Field
	private List<ObjectId> yogaClassesIds;
	@Field
	private String imageUrl;
	
	@Field
	private String thumbnailUrl;
	@Field
	private YogaTeacher teacher;
	@Field
	private List<ObjectId> diseaseIds;
	@Field
	private List<ObjectId> precautionIds;
	@Field
	private Boolean isPaid=false;
	@Field
	private Boolean discarded=false;
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
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
	public List<ObjectId> getYogaClassesIds() {
		return yogaClassesIds;
	}
	public void setYogaClassesIds(List<ObjectId> yogaClassesIds) {
		this.yogaClassesIds = yogaClassesIds;
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
	
	
	
	public YogaTeacher getTeacher() {
		return teacher;
	}
	public void setTeacher(YogaTeacher teacher) {
		this.teacher = teacher;
	}
	public List<ObjectId> getDiseaseIds() {
		return diseaseIds;
	}
	public void setDiseaseIds(List<ObjectId> diseaseIds) {
		this.diseaseIds = diseaseIds;
	}
	public List<ObjectId> getPrecautionIds() {
		return precautionIds;
	}
	public void setPrecautionIds(List<ObjectId> precautionIds) {
		this.precautionIds = precautionIds;
	}
	public Boolean getIsPaid() {
		return isPaid;
	}
	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	

}
