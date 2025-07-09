package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.CampaignRunningAt;
import com.dpdocter.enums.CampaignStatus;

@Document(collection = "campaign_cl")
public class CampaignNameCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String campaignName;

	@Field
	private Boolean isDiscarded = false;

	@Field
	private Date fromDate;

	@Field
	private Date toDate;

	@Field
	private List<ObjectId> city;

	@Field
	private CampaignStatus campaignStatus;
	
	@Field
	private CampaignRunningAt campaignRunningAt;

	@Field
	private List<ObjectId> campaignObjectIds;

	@Field
	private String objective;

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<ObjectId> getCity() {
		return city;
	}

	public void setCity(List<ObjectId> city) {
		this.city = city;
	}

	public CampaignStatus getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(CampaignStatus campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public List<ObjectId> getCampaignObjectIds() {
		return campaignObjectIds;
	}

	public void setCampaignObjectIds(List<ObjectId> campaignObjectIds) {
		this.campaignObjectIds = campaignObjectIds;
	}

	public CampaignRunningAt getCampaignRunningAt() {
		return campaignRunningAt;
	}

	public void setCampaignRunningAt(CampaignRunningAt campaignRunningAt) {
		this.campaignRunningAt = campaignRunningAt;
	}

}
