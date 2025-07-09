package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Exercise extends GenericCollection{

	private String id;

	private String name;
	
	private String type;
	
	private List<ExcerciseSubType> subType;

	private Boolean discarded = false;
	
	private String imageUrl;
	
	private String videoUrl;
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
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
		return "Exercise [id=" + id + ", name=" + name + ", type=" + type + ", subType=" + subType + ", discarded="
				+ discarded + "]";
	}
}
