package com.dpdocter.response;

public class GenderCountResponse {
	private Integer female = 0;
	private Integer male = 0;
	private Integer other = 0;
	private Integer noValues = 0;

	public Integer getNoValues() {
		return noValues;
	}

	public void setNoValues(Integer noValues) {
		this.noValues = noValues;
	}
	
	public Integer getFemale() {
		return female;
	}

	public void setFemale(Integer female) {
		this.female = female;
	}

	public Integer getMale() {
		return male;
	}

	public void setMale(Integer male) {
		this.male = male;
	}

	public Integer getOther() {
		return other;
	}

	public void setOther(Integer other) {
		this.other = other;
	}

}
