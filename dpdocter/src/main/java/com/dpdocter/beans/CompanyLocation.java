package com.dpdocter.beans;

public class CompanyLocation {
	private String id;

	private String locationName;

	private String companyName;

	private String companyId;

	private String timezone;

	private Boolean isDiscarded = false;

	public String getId() {
		return id;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@Override
	public String toString() {
		return "CompanyLocation [id=" + id + ", locationName=" + locationName + ", companyName=" + companyName
				+ ", companyId=" + companyId + ", timezone=" + timezone + ", isDiscarded=" + isDiscarded + "]";
	}

}
