package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ExcerciseSubType;
import com.dpdocter.enums.ExcerciseType;

@Document(collection = "exercise_cl")
public class ExerciseCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String name;
	
	@Field
	private ExcerciseType type;
	
	@Field
	private List<ExcerciseSubType> subType;

	@Field
	private Boolean discarded = false;
	
	@Field
	private String imageUrl;
	
	@Field
	private String videoUrl;
	
	@Field
	private String thumbnailUrl;
	
    public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "ExerciseCollection [id=" + id + ", name=" + name + ", type=" + type + ", subType=" + subType
				+ ", discarded=" + discarded + "]";
	}
}
