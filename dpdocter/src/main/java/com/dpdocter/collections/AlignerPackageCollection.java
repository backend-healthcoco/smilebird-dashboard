package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "aligner_package_cl")
public class AlignerPackageCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String packageName;

	@Field
	private String description;

	@Field
	private Boolean isDiscarded = false;

	@Field
	private String duration;

	@Field
	private String clinicVisits;

	@Field
	private Double amount = 0.0;

	@Field
	private Double emiAmount = 0.0;

	@Field
	private Double discountedAmount = 0.0;

	@Field
	private String imageUrl;
	
	@Field
	private Integer noOfAligners;


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
