package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.BlogCategoryType;

public class BlogImageUploadReqest {

	private List<FileDetails> images;
	private String userId;
	private BlogCategoryType catagory;
	public BlogCategoryType getCatagory() {
		return catagory;
	}

	public void setCatagory(BlogCategoryType catagory) {
		this.catagory = catagory;
	}

	public List<FileDetails> getImages() {
		return images;
	}

	public void setImages(List<FileDetails> images) {
		this.images = images;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
