package com.dpdocter.beans;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.CampaignDeviceType;

@Document(collection = "campaign_obejct_cl")
public class CampaignObjectCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId campaignId;

	@Field
	private Double messagingConversationStarted;

	@Field
	private Double costPerMessagingConversation;

	@Field
	private Double dailyBudget;

	@Field
	private Double amountSpend;

	@Field
	private Double reach;

	@Field
	private Double impression;

	@Field
	private Double qualityRanking;

	@Field
	private Double engagementRateRanking;

	@Field
	private Double conversionRateRanking;

	@Field
	private Double purchaseRoas;

	@Field
	private Double frequency;

	@Field
	private Double uniqueClicks;

	@Field
	private Double costPerLinkClick;

	@Field
	private Date fromDate;

	@Field
	private Date toDate;

	@Field
	private Boolean isDiscarded = false;

	@Field
	private Double facebookReach;

	@Field
	private Double instagramReach;

	@Field
	private Double ctr;

	@Field
	private Double validLead;

	@Field
	private Double numberOfLead;
	
	@Field
	private Double numberOfCalls;
	
	@Field
	private Double localActions;
	
	@Field
	private Double websiteVisits ;
	
	@Field
	private Double conversation;
	
	@Field
	private CampaignDeviceType device;
	
	@Field
	private Double avgCostPerClicks;

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(ObjectId campaignId) {
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

	public Double getNumberOfLead() {
		return numberOfLead;
	}

	public void setNumberOfLead(Double numberOfLead) {
		this.numberOfLead = numberOfLead;
	}

}
