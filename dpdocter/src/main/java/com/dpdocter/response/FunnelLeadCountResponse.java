package com.dpdocter.response;

public class FunnelLeadCountResponse {
	private Integer totalLeads = 0;
	private Integer hot = 0;
	private Integer genuine = 0;
	private Integer converted = 0;
	private Integer clinicVisit = 0;
	public Integer getTotalLeads() {
		return totalLeads;
	}
	public void setTotalLeads(Integer totalLeads) {
		this.totalLeads = totalLeads;
	}
	public Integer getHot() {
		return hot;
	}
	public void setHot(Integer hot) {
		this.hot = hot;
	}
	public Integer getGenuine() {
		return genuine;
	}
	public void setGenuine(Integer genuine) {
		this.genuine = genuine;
	}
	public Integer getConverted() {
		return converted;
	}
	public void setConverted(Integer converted) {
		this.converted = converted;
	}
	public Integer getClinicVisit() {
		return clinicVisit;
	}
	public void setClinicVisit(Integer clinicVisit) {
		this.clinicVisit = clinicVisit;
	}

}
