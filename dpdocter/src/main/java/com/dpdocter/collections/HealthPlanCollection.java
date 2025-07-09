package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AmountObject;
import com.dpdocter.enums.HealthPackagesPlanType;

@Document(collection = "health_therapy_plan_cl")
public class HealthPlanCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private HealthPackagesPlanType type;

	@Field
	private String planDescription;

	@Field
	private String shortPlanDescription;
	
	@Field
	private List<AmountObject> amount;
	
	@Field
	private String imageWeb;
	
	@Field
	private String imageMobile;
	
	@Field
	private String slugUrl;
	
	@Field
	private int rank;
	
	@Field
	private Boolean isDiscarded = false;
	@Field
	private Boolean isApproved = false;
	
	@Field
	private String planUId;
	
	@Field
	private String country ;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPlanUId() {
		return planUId;
	}
	public void setPlanUId(String planUId) {
		this.planUId = planUId;
	}
	
	
	
}
