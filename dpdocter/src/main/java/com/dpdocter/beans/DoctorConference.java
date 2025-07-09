package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.OrganizingCommitteeResponse;

public class DoctorConference extends GenericCollection {

	private String id;

	private String title;

	private String titleImage;

	private String description;

	private List<String> specialities;
	
	private List<String> mealType;
	
	private String smsHeader;

	private Date fromDate;

	private Date toDate;

	private Address address;

	private List<OrganizingCommitteeResponse> commiteeMember;

	private List<OrganizingCommitteeResponse> speakers;

	private Boolean discarded = false;

	private List<ConfexUser> admin;

	private String uIdPrefix;

	private String status;

	private String website;

	private Boolean allowRegistration = true;

	public String getuIdPrefix() {
		return uIdPrefix;
	}

	public void setuIdPrefix(String uIdPrefix) {
		this.uIdPrefix = uIdPrefix;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<OrganizingCommitteeResponse> getCommiteeMember() {
		return commiteeMember;
	}

	public void setCommiteeMember(List<OrganizingCommitteeResponse> commiteeMember) {
		this.commiteeMember = commiteeMember;
	}

	public List<OrganizingCommitteeResponse> getSpeakers() {
		return speakers;
	}

	public void setSpeakers(List<OrganizingCommitteeResponse> speakers) {
		this.speakers = speakers;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ConfexUser> getAdmin() {
		return admin;
	}

	public void setAdmin(List<ConfexUser> admin) {
		this.admin = admin;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Boolean getAllowRegistration() {
		return allowRegistration;
	}

	public void setAllowRegistration(Boolean allowRegistration) {
		this.allowRegistration = allowRegistration;
	}

	public List<String> getMealType() {
		return mealType;
	}

	public void setMealType(List<String> mealType) {
		this.mealType = mealType;
	}

	public String getSmsHeader() {
		return smsHeader;
	}

	public void setSmsHeader(String smsHeader) {
		this.smsHeader = smsHeader;
	}

}
