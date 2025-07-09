package com.dpdocter.request;

import com.dpdocter.collections.GenericCollection;

public class AlignerPackageRequest extends GenericCollection{
	private String id;

	private String packageName;

	private String description;

	private Boolean isDiscarded = false;

	private String duration;

	private String clinicVisits;

	private Double amount = 0.0;

	private Double emiAmount = 0.0;

	private Double discountedAmount = 0.0;

	private String imageUrl;
	
	private Integer noOfAligners;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
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

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getClinicVisits() {
		return clinicVisits;
	}

	public void setClinicVisits(String clinicVisits) {
		this.clinicVisits = clinicVisits;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(Double emiAmount) {
		this.emiAmount = emiAmount;
	}

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getNoOfAligners() {
		return noOfAligners;
	}

	public void setNoOfAligners(Integer noOfAligners) {
		this.noOfAligners = noOfAligners;
	}

}
