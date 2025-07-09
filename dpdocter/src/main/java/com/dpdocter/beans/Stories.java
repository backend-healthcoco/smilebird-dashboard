package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.StandardsEnum;
import com.dpdocter.enums.TypeEnum;

public class Stories extends GenericCollection{

	private String id;
		
	private String imageUrl;
	
	private String thumbnailUrl;
	
	private String videoUrl;
	
	private List<StoryDetails> storyDetails;
	
	private TypeEnum type;
	
	private Boolean discarded;
	
	private Integer time;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public List<StoryDetails> getStoryDetails() {
		return storyDetails;
	}
	public void setStoryDetails(List<StoryDetails> storyDetails) {
		this.storyDetails = storyDetails;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	public TypeEnum getType() {
		return type;
	}
	public void setType(TypeEnum type) {
		this.type = type;
	}
	
	public Integer getTime() {
		return time;
	}
	public void setTime(Integer time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "Stories [id=" + id + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", videoUrl=" + videoUrl + ", storyDetails=" + storyDetails + ", type=" + type
				+ ", discarded=" + discarded + "]";
	}
	
	
	
	
}
