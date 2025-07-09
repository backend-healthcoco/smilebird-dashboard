package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Language;
import com.dpdocter.beans.YogaMultilingual;

@Document(collection = "yoga_teacher_cl")
public class YogaTeacherCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private String imageUrl;
	
	@Field
	private List<YogaMultilingual> multilingualYogaTeacher;
	
	@Field
	private Boolean discarded=false;

	

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

	
	
	

	public List<YogaMultilingual> getMultilingualYogaTeacher() {
		return multilingualYogaTeacher;
	}

	public void setMultilingualYogaTeacher(List<YogaMultilingual> multilingualYogaTeacher) {
		this.multilingualYogaTeacher = multilingualYogaTeacher;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "YogaTeacherCollection [id=" + id + ", imageUrl=" + imageUrl 
				+ ", discarded=" + discarded + "]";
	}
	

}
