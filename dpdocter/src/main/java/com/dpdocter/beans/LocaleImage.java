package com.dpdocter.beans;

public class LocaleImage {

    private String imageUrl;

    private String thumbnailUrl;

    private String id;

    @Override
	public String toString() {
		return "LocaleImage [imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", id=" + id + "]";
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

   

}
