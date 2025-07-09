package com.dpdocter.beans;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;

public class UploadFile extends GenericCollection{

private String id;
	
private String path; 
	
private String imageUrl;

private String thumbnailUrl;
    
private String type;

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getPath() {
	return path;
}

public void setPath(String path) {
	this.path = path;
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

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}
    
    

}
