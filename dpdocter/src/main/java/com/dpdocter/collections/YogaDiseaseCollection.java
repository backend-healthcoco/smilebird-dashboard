package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.YogaDiseaseMultilingual;
import com.dpdocter.collections.GenericCollection;

@Document(collection = "yoga_disease_cl")
public class YogaDiseaseCollection extends GenericCollection {

	@Id
	private ObjectId id;
	
	@Field
	private List<YogaDiseaseMultilingual> yogaDiseaseMultilingual;
	
	@Field
	private Boolean discarded=false;

	

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<YogaDiseaseMultilingual> getYogaDiseaseMultilingual() {
		return yogaDiseaseMultilingual;
	}

	public void setYogaDiseaseMultilingual(List<YogaDiseaseMultilingual> yogaDiseaseMultilingual) {
		this.yogaDiseaseMultilingual = yogaDiseaseMultilingual;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	
}
