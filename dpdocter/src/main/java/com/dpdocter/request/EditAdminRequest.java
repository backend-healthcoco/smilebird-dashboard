package com.dpdocter.request;

public class EditAdminRequest {
	private String id;
	private String firstName;
	private String emailAddress;
	private String adminType;
	private String city;
	private String notes;
	private Boolean isAnonymousAppointment = false;
	private Boolean isBuddy = false;
	private Boolean isAgent = false;
	private Boolean isCampDoctor = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getAdminType() {
		return adminType;
	}

	public void setAdminType(String adminType) {
		this.adminType = adminType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getIsAnonymousAppointment() {
		return isAnonymousAppointment;
	}

	public void setIsAnonymousAppointment(Boolean isAnonymousAppointment) {
		this.isAnonymousAppointment = isAnonymousAppointment;
	}

	public Boolean getIsBuddy() {
		return isBuddy;
	}

	public void setIsBuddy(Boolean isBuddy) {
		this.isBuddy = isBuddy;
	}

	public Boolean getIsAgent() {
		return isAgent;
	}

	public void setIsAgent(Boolean isAgent) {
		this.isAgent = isAgent;
	}

	public Boolean getIsCampDoctor() {
		return isCampDoctor;
	}

	public void setIsCampDoctor(Boolean isCampDoctor) {
		this.isCampDoctor = isCampDoctor;
	}

}
