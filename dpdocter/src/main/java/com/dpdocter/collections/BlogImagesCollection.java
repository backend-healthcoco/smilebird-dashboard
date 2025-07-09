package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.BlogCategoryType;

@Document(collection = "blog_image_cl")
public class BlogImagesCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String imageName;

	@Field
	private String imagePath;

	@Field
	private ObjectId userId;

	@Field
	private BlogCategoryType catagory;

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public BlogCategoryType getCatagory() {
		return catagory;
	}

	public void setCatagory(BlogCategoryType catagory) {
		this.catagory = catagory;
	}

}
