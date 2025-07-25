package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class AlignerPlanResponse extends GenericCollection {
	private String id;

	private String planName;

	private String description;

	private Boolean isDiscarded = false;
	
	private String packageId;
	
	private Boolean isIPRRequird = false;

	private Boolean isAttachment = false;

	private String duration;

	private Integer noOfAligners;

	private String iprVideoUrl;
	
	private String attachmentVideoUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
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
