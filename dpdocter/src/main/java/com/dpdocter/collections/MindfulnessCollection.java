package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import com.dpdocter.enums.TypeEnum;
import com.dpdocter.beans.MindfulnessDetail;
import com.dpdocter.enums.Goal;
import com.dpdocter.enums.StandardsEnum;

@Document(collection = "mindfulness_cl")
public class MindfulnessCollection extends GenericCollection {

	@Id
	private ObjectId id;
		
	@Field
	private List<String> goals;
	
	@Field
	private String imageUrl;
	
	@Field
	private String thumbnailUrl;
	
	@Field
	private String videoUrl;
	
	@Field
	private String title;
	
	@Field
	private List<MindfulnessDetail> details;
	
	@Field
	private TypeEnum type;
	
	@Field
	private Boolean discarded;
	
	@Field
	private String description;
	
	@Field
	private Integer time;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
	
	public Boolean getDiscarded() {
		return discarded;
	}


	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		return "MindfulnessCollection [id=" + id  + ", goals=" + goals + ", imageUrl="
				+ imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", videoUrl=" + videoUrl + ", title=" + title
				+ ", type=" + type + ", discarded=" + discarded + "]";
	}

	
	
}
