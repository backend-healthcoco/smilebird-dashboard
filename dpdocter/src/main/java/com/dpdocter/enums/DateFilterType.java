package com.dpdocter.enums;

public enum DateFilterType {

	FOLLOWUP("FOLLOWUP"), CREATEDTIME("CREATEDTIME"), UPDATEDTIME("UPDATEDTIME"),
	REGISTRATIONDATE("REGISTRATIONDATE");

	private String filter;

	private DateFilterType(String filter) {
		this.filter = filter;
	}

	public String getFilter() {
		return filter;
	}
}
