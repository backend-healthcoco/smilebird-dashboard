package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class YogaTeacher extends GenericCollection{

	private String id;
	
	private List<YogaMultilingual> multilingualYogaTeacher;
	
	private String imageUrl;
	
	
	private Boolean discarded=false;

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
		return "YogaTeacher [id=" + id +  ", imageUrl=" + imageUrl + ", discarded=" + discarded + "]";
	}
	
	
	
	
}
