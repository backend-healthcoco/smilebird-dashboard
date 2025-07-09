package com.dpdocter.request;

import com.dpdocter.beans.PlanPriceDescription;

public class PlanPricesPutRemoveRequest {

	private String planId;
	private String countryCode;
	private PlanPriceDescription planPriceDescription;
	private Boolean isRemove = false;

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public PlanPriceDescription getPlanPriceDescription() {
		return planPriceDescription;
	}

	public void setPlanPriceDescription(PlanPriceDescription planPriceDescription) {
		this.planPriceDescription = planPriceDescription;
	}

	public Boolean getIsRemove() {
		return isRemove;
	}

	public void setIsRemove(Boolean isRemove) {
		this.isRemove = isRemove;
	}

}
