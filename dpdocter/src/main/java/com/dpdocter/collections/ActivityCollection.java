package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ActivityDetail;

import com.dpdocter.enums.StandardsEnum;

@Document(collection = "activity_cl")
public class ActivityCollection extends GenericCollection{

	@Id
	private ObjectId id;
		
	@Field
	private String imageUrl;
	@Field
	private String thumbnailUrl;
	
	@Field
	private String videoUrl;
	
	@Field
	private List<ActivityDetail> details;
	
	@Field
	private Boolean discarded=Boolean.FALSE;
	
	private Integer time;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public List<ActivityDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ActivityDetail> details) {
		this.details = details;
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

	@Override
	public String toString() {
		return "ActivityCollection [id=" + id + ", imageUrl=" + imageUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", videoUrl=" + videoUrl + ", details=" + details + ", discarded="
				+ discarded + ", time=" + time + "]";
	}

	
	
	
	
	
}
