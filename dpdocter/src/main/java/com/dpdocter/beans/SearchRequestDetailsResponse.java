package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.request.PrescriptionRequest;

public class SearchRequestDetailsResponse extends GenericCollection {

	private User user;

	private String userId;

	private long noOfYesReplies = 0;

	private long noOfNoReplies = 0;

	private String uniqueRequestId;

	private PrescriptionRequest prescriptionRequest;

	private String localeId;

	private Locale locale;

	private String location;

	private Double latitude = 0.0;

	private Double longitude = 0.0;

	private Boolean isBlockForHour = false;

	private Boolean isBlockForDay = false;

	public Boolean getIsBlockForHour() {
		return isBlockForHour;
	}

	public void setIsBlockForHour(Boolean isBlockForHour) {
		this.isBlockForHour = isBlockForHour;
	}

	public Boolean getIsBlockForDay() {
		return isBlockForDay;
	}

	public void setIsBlockForDay(Boolean isBlockForDay) {
		this.isBlockForDay = isBlockForDay;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getNoOfYesReplies() {
		return noOfYesReplies;
	}

	public void setNoOfYesReplies(long noOfYesReplies) {
		this.noOfYesReplies = noOfYesReplies;
	}

	public long getNoOfNoReplies() {
		return noOfNoReplies;
	}

	public void setNoOfNoReplies(long noOfNoReplies) {
		this.noOfNoReplies = noOfNoReplies;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public PrescriptionRequest getPrescriptionRequest() {
		return prescriptionRequest;
	}

	public void setPrescriptionRequest(PrescriptionRequest prescriptionRequest) {
		this.prescriptionRequest = prescriptionRequest;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
