package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class YogaDisease extends GenericCollection {

	private String id;
	
	private List<YogaDiseaseMultilingual> yogaDiseaseMultilingual;
	
	private Boolean discarded=false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
