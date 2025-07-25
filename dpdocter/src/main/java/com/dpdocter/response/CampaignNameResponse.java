package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.CampaignRunningAt;
import com.dpdocter.enums.CampaignStatus;

public class CampaignNameResponse extends GenericCollection{
	private String id;

	private String campaignName;

	private Boolean isDiscarded = false;

	private Date fromDate;

	private Date toDate;

	private List<String> city;

	private List<String> cityNames;

	private CampaignStatus campaignStatus;
	
	private CampaignRunningAt campaignRunningAt;
	
	private List<CampaignObjectResponse> objectResponses;
	
	private String objective;


	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

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

	public List<CampaignObjectResponse> getObjectResponses() {
		return objectResponses;
	}

	public void setObjectResponses(List<CampaignObjectResponse> objectResponses) {
		this.objectResponses = objectResponses;
	}

	public List<String> getCityNames() {
		return cityNames;
	}

	public void setCityNames(List<String> cityNames) {
		this.cityNames = cityNames;
	}

	public CampaignRunningAt getCampaignRunningAt() {
		return campaignRunningAt;
	}

	public void setCampaignRunningAt(CampaignRunningAt campaignRunningAt) {
		this.campaignRunningAt = campaignRunningAt;
	}
	
}
