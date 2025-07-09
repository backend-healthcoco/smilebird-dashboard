package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.HealthPackagesPlanType;

public class HealthPlanTypeResponse extends GenericCollection {

	private String id;

	private String title;

	private HealthPackagesPlanType type;

	private String shortPlanDescription;
	
	private List<AmountObject> amount;
		
	private String slugUrl;
	
	private int rank;
	
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
