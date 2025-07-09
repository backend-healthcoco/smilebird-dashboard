package com.dpdocter.request;

import com.dpdocter.beans.PlanDuration;
import com.dpdocter.enums.PaymentMode;

public class AlignerUserPlanRequest {
	private String id;

	private String userId;

	private String planId;

	private String title;
	
	private Integer noOfAligners;
	
	private PlanDuration duration;
	
	private Double discount = 0.0;
	
	private Double amount = 0.0;
	
	private PaymentMode mode = PaymentMode.ONLINE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getNoOfAligners() {
		return noOfAligners;
	}

	public void setNoOfAligners(Integer noOfAligners) {
		this.noOfAligners = noOfAligners;
	}

	public PlanDuration getDuration() {
		return duration;
	}

	public void setDuration(PlanDuration duration) {
		this.duration = duration;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}
	
}
