package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.dpdocter.beans.ExcerciseSubType;
import com.dpdocter.enums.ExcerciseType;

@Document(indexName = "exercises_in", type = "exercises")
public class ESExerciseDocument {
	@Id
	private String id;

	@Field(type = FieldType.Text, fielddata = true)
	private String name;

	@Field(type = FieldType.Text)
	private ExcerciseType type;
	
	@MultiField(mainField = @Field(type = FieldType.Nested))
	private List<ExcerciseSubType> subType;
	
	@Field(type = FieldType.Date)
	private Date updatedTime;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;
	
	@Field(type = FieldType.Text)
	private String imageUrl;

	@Field(type = FieldType.Text)
	private String thumbnailUrl;
	
	@Field(type = FieldType.Text)
	private String videoUrl;

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

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExcerciseType getType() {
		return type;
	}

	public void setType(ExcerciseType type) {
		this.type = type;
	}

	public List<ExcerciseSubType> getSubType() {
		return subType;
	}

	public void setSubType(List<ExcerciseSubType> subType) {
		this.subType = subType;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "ESExerciseDocument [id=" + id + ", name=" + name + ", type=" + type + ", subType=" + subType
				+ ", updatedTime=" + updatedTime + ", discarded=" + discarded + "]";
	}
}
