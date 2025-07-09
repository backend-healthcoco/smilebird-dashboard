package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.CampaignDeviceType;

public class CampaignObjectRequest extends GenericCollection {
	private String id;

	private String campaignId;

	private Double messagingConversationStarted;

	private Double costPerMessagingConversation;

	private Double dailyBudget;

	private Double amountSpend;

	private Double reach;

	private Double impression;

	private Double qualityRanking;

	private Double engagementRateRanking;

	private Double conversionRateRanking;

	private Double purchaseRoas;

	private Double frequency;

	private Double uniqueClicks;

	private Double costPerLinkClick;

	private Date fromDate;

	private Date toDate;
	
	private Double facebookReach;
	
	private Double instagramReach;
	
	private Double ctr;
	
	private Double validLead;
	
	private Double numberOfLead;
	
	private Double numberOfCalls;
	
	private Double localActions;
	
	private Double websiteVisits ;
	
	private Double conversation;
	
	private CampaignDeviceType device;
	
	private Double avgCostPerClicks;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public Double getMessagingConversationStarted() {
		return messagingConversationStarted;
	}

	public void setMessagingConversationStarted(Double messagingConversationStarted) {
		this.messagingConversationStarted = messagingConversationStarted;
	}

	public Double getCostPerMessagingConversation() {
		return costPerMessagingConversation;
	}

	public void setCostPerMessagingConversation(Double costPerMessagingConversation) {
		this.costPerMessagingConversation = costPerMessagingConversation;
	}

	public Double getDailyBudget() {
		return dailyBudget;
	}

	public void setDailyBudget(Double dailyBudget) {
		this.dailyBudget = dailyBudget;
	}

	public Double getAmountSpend() {
		return amountSpend;
	}

	public void setAmountSpend(Double amountSpend) {
		this.amountSpend = amountSpend;
	}

	public Double getReach() {
		return reach;
	}

	public void setReach(Double reach) {
		this.reach = reach;
	}

	public Double getImpression() {
		return impression;
	}

	public void setImpression(Double impression) {
		this.impression = impression;
	}

	public Double getQualityRanking() {
		return qualityRanking;
	}

	public void setQualityRanking(Double qualityRanking) {
		this.qualityRanking = qualityRanking;
	}

	public Double getEngagementRateRanking() {
		return engagementRateRanking;
	}

	public void setEngagementRateRanking(Double engagementRateRanking) {
		this.engagementRateRanking = engagementRateRanking;
	}

	public Double getConversionRateRanking() {
		return conversionRateRanking;
	}

	public void setConversionRateRanking(Double conversionRateRanking) {
		this.conversionRateRanking = conversionRateRanking;
	}

	public Double getPurchaseRoas() {
		return purchaseRoas;
	}

	public void setPurchaseRoas(Double purchaseRoas) {
		this.purchaseRoas = purchaseRoas;
	}

	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	public Double getUniqueClicks() {
		return uniqueClicks;
	}

	public void setUniqueClicks(Double uniqueClicks) {
		this.uniqueClicks = uniqueClicks;
	}

	public Double getCostPerLinkClick() {
		return costPerLinkClick;
	}

	public void setCostPerLinkClick(Double costPerLinkClick) {
		this.costPerLinkClick = costPerLinkClick;
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

	public Double getFacebookReach() {
		return facebookReach;
	}

	public void setFacebookReach(Double facebookReach) {
		this.facebookReach = facebookReach;
	}

	public Double getInstagramReach() {
		return instagramReach;
	}

	public void setInstagramReach(Double instagramReach) {
		this.instagramReach = instagramReach;
	}

	public Double getCtr() {
		return ctr;
	}

	public void setCtr(Double ctr) {
		this.ctr = ctr;
	}

	public Double getValidLead() {
		return validLead;
	}

	public void setValidLead(Double validLead) {
		this.validLead = validLead;
	}

	public Double getNumberOfLead() {
		return numberOfLead;
	}

	public void setNumberOfLead(Double numberOfLead) {
		this.numberOfLead = numberOfLead;
	}

	public Double getNumberOfCalls() {
		return numberOfCalls;
	}

	public void setNumberOfCalls(Double numberOfCalls) {
		this.numberOfCalls = numberOfCalls;
	}

	public Double getLocalActions() {
		return localActions;
	}

	public void setLocalActions(Double localActions) {
		this.localActions = localActions;
	}

	public Double getWebsiteVisits() {
		return websiteVisits;
	}

	public void setWebsiteVisits(Double websiteVisits) {
		this.websiteVisits = websiteVisits;
	}

	public Double getConversation() {
		return conversation;
	}

	public void setConversation(Double conversation) {
		this.conversation = conversation;
	}

	public CampaignDeviceType getDevice() {
		return device;
	}

	public void setDevice(CampaignDeviceType device) {
		this.device = device;
	}

	public Double getAvgCostPerClicks() {
		return avgCostPerClicks;
	}

	public void setAvgCostPerClicks(Double avgCostPerClicks) {
		this.avgCostPerClicks = avgCostPerClicks;
	}

}
