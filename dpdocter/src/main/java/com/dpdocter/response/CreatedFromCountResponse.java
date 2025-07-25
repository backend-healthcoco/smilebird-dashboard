package com.dpdocter.response;

public class CreatedFromCountResponse {

	private Integer createdFromAdmin = 0;
	private Integer createdFromWebsite = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}

	public Integer getCreatedFromAdmin() {
		return createdFromAdmin;
	}

	public void setCreatedFromAdmin(Integer createdFromAdmin) {
		this.createdFromAdmin = createdFromAdmin;
	}

	public Integer getCreatedFromWebsite() {
		return createdFromWebsite;
	}

	public void setCreatedFromWebsite(Integer createdFromWebsite) {
		this.createdFromWebsite = createdFromWebsite;
	}

}
