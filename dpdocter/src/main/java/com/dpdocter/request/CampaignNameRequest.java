package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.CampaignRunningAt;
import com.dpdocter.enums.CampaignStatus;

public class CampaignNameRequest extends GenericCollection {
	private String id;

	private String campaignName;
	
	private String objective;

	private Boolean isDiscarded = false;

	private Date fromDate;

	private Date toDate;

	private List<String> city;
	
	private CampaignRunningAt campaignRunningAt;

	private CampaignStatus campaignStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public List<String> getCity() {
		return city;
	}

	public void setCity(List<String> city) {
		this.city = city;
	}

	public CampaignStatus getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(CampaignStatus campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public CampaignRunningAt getCampaignRunningAt() {
		return campaignRunningAt;
	}

	public void setCampaignRunningAt(CampaignRunningAt campaignRunningAt) {
		this.campaignRunningAt = campaignRunningAt;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

}
