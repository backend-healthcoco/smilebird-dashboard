package com.dpdocter.request;

import com.dpdocter.enums.DentalImageTag;

public class DentalCampImages {
	private String imageUrl;

	private String thumbnailUrl;

	private DentalImageTag tag;

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

	public DentalImageTag getTag() {
		return tag;
	}

	public void setTag(DentalImageTag tag) {
		this.tag = tag;
	}

}
