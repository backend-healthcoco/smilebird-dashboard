package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.MonthlyData;

public class MonthlyAnalyticsResponse {
	private String type;
	private List<MonthlyData> monthlydata;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<MonthlyData> getMonthlydata() {
		return monthlydata;
	}

	public void setMonthlydata(List<MonthlyData> monthlydata) {
		this.monthlydata = monthlydata;
	}

}
