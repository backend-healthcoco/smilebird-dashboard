package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.StandardsEnum;

public class Activity extends GenericCollection{

	private String id;
		
	private String imageUrl;
	
	private String thumbnailUrl;
	
	private String videoUrl;
	
	private List<ActivityDetail> details;
	
	private Boolean discarded=Boolean.FALSE;
	
	private Boolean isCompleted=false;

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
	
	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", videoUrl=" + videoUrl + ", details=" + details + ", discarded=" + discarded
				+ ", time=" + time + "]";
	}

	
	
}
