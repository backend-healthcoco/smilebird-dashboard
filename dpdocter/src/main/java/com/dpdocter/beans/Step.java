package com.dpdocter.beans;

public class Step {

	private String stepno;
	private String shortDescription;
	private String doAndDonts;
	private String imageUrl;
	private String thumbnailUrl;
	private String videoUrl;
	public String getStepno() {
		return stepno;
	}
	public void setStepno(String stepno) {
		this.stepno = stepno;
	}
	
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getDoAndDonts() {
		return doAndDonts;
	}
	public void setDoAndDonts(String doAndDonts) {
		this.doAndDonts = doAndDonts;
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
	
	@Override
	public String toString() {
		return "Step [stepno=" + stepno + ", shortDescription=" + shortDescription + ", doAndDonts=" + doAndDonts + ", imageUrl="
				+ imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", videoUrl=" + videoUrl + "]";
	}
	
	
	
	
}
