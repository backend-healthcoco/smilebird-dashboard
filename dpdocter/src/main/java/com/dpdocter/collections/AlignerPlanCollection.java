package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "aligner_plan_cl")
public class AlignerPlanCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String planName;

	@Field
	private String description;

	@Field
	private Boolean isDiscarded = false;
	
	@Field
	private ObjectId packageId;
	
	@Field
	private Boolean isIPRRequird = false;

	@Field
	private Boolean isAttachment = false;

	@Field
	private String duration;

	@Field
	private Integer noOfAligners;

	@Field
	private String iprVideoUrl;
	
	@Field
	private String attachmentVideoUrl;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public ObjectId getPackageId() {
		return packageId;
	}

	public void setPackageId(ObjectId packageId) {
		this.packageId = packageId;
	}

	public Boolean getIsIPRRequird() {
		return isIPRRequird;
	}

	public void setIsIPRRequird(Boolean isIPRRequird) {
		this.isIPRRequird = isIPRRequird;
	}

	public Boolean getIsAttachment() {
		return isAttachment;
	}

	public void setIsAttachment(Boolean isAttachment) {
		this.isAttachment = isAttachment;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getNoOfAligners() {
		return noOfAligners;
	}

	public void setNoOfAligners(Integer noOfAligners) {
		this.noOfAligners = noOfAligners;
	}

	public String getIprVideoUrl() {
		return iprVideoUrl;
	}

	public void setIprVideoUrl(String iprVideoUrl) {
		this.iprVideoUrl = iprVideoUrl;
	}

	public String getAttachmentVideoUrl() {
		return attachmentVideoUrl;
	}

	public void setAttachmentVideoUrl(String attachmentVideoUrl) {
		this.attachmentVideoUrl = attachmentVideoUrl;
	}
	
}
