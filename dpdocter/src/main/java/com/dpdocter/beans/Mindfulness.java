package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.TypeEnum;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.Goal;
import com.dpdocter.enums.StandardsEnum;

public class Mindfulness extends GenericCollection{

	private String id;
		
	private List<String> goals;
	
	private String imageUrl;
	
	private String thumbnailUrl;
	
	private String videoUrl;
	
	private String title;
	
	private List<MindfulnessDetail> details;
	
	private String description;
	
	private TypeEnum type;
	
	private Boolean discarded=false;
	
	private Integer time;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getGoals() {
		return goals;
	}

	public void setGoals(List<String> goals) {
		this.goals = goals;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public TypeEnum getType() {
		return type;
	}

	public void setType(TypeEnum type) {
		this.type = type;
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}
	
	

	public List<MindfulnessDetail> getDetails() {
		return details;
	}

	public void setDetails(List<MindfulnessDetail> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "Mindfulness [id=" + id + ", goals=" + goals + ", imageUrl=" + imageUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", videoUrl=" + videoUrl + ", title=" + title + ", type=" + type
				+ "]";
	}

	
	
	
}
