package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.HealthPackagesPlanType;

public class HealthPlanResponse extends GenericCollection {

	private String id;

	private String title;

	private HealthPackagesPlanType type;

	private String planDescription;

	private String shortPlanDescription;
	
	private List<AmountObject> amount;
	
	private String imageWeb;
	
	private String imageMobile;
	
	private String slugUrl;
	
	private int rank;
	
	private Boolean isDiscarded = false;

	private Boolean isApproved = false;
	
	private String planUId;

	private String country ;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HealthPackagesPlanType getType() {
		return type;
	}

	public void setType(HealthPackagesPlanType type) {
		this.type = type;
	}

	public String getPlanDescription() {
		return planDescription;
	}

	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}

	public String getShortPlanDescription() {
		return shortPlanDescription;
	}

	public void setShortPlanDescription(String shortPlanDescription) {
		this.shortPlanDescription = shortPlanDescription;
	}

	public List<AmountObject> getAmount() {
		return amount;
	}

	public void setAmount(List<AmountObject> amount) {
		this.amount = amount;
	}

	public String getImageWeb() {
		return imageWeb;
	}

	public void setImageWeb(String imageWeb) {
		this.imageWeb = imageWeb;
	}

	public String getImageMobile() {
		return imageMobile;
	}

	public void setImageMobile(String imageMobile) {
		this.imageMobile = imageMobile;
	}

	public String getSlugUrl() {
		return slugUrl;
	}

	public void setSlugUrl(String slugUrl) {
		this.slugUrl = slugUrl;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getPlanUId() {
		return planUId;
	}

	public void setPlanUId(String planUId) {
		this.planUId = planUId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	
	
	

}
