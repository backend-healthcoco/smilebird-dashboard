package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "research_paper_subcategory_cl")
public class ResearchPaperSubCategoryCollection  extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private ObjectId conferenceId;
	@Field
	private String subCategory;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(ObjectId conferenceId) {
		this.conferenceId = conferenceId;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
