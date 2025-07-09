package com.dpdocter.request;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.CampUser;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.BroadcastTo;
import com.dpdocter.enums.BroadcastType;
import com.dpdocter.enums.SmsRoute;

import common.util.web.DPDoctorUtils;

public class DentalCampBroadcastTemplateRequest extends GenericCollection {
	private SmsRoute route;
	private String templateId;
	private List<String> doctorId;
	private String locationId;
	private String smileBuddyId;
	private String templateName;
	private BroadcastTo broadcastTo = BroadcastTo.LEADS;
	private String locality;
	private String language;
	private String salaryRange;
	private String city;
	private String leadType;
	private String leadStage;
	private String profession;
	private Boolean isPatientCreated;
	private Boolean isMobileNumberPresent;
	private String isPhotoUpload;
	private String followupType;
	private String gender;
	private String age;
	private String dateFilterType;
	private String searchTerm;
	private int page;
	private Boolean isDiscarded = false;
	private int size;
	private List<String> roles;
	private BroadcastType broadcastType;
	private String message;
	private String campName;
	private String complaint;
	private String fromDate;
	private String toDate;
	private List<CampUser> campUsers;
	private String languageCode;
	private String imageUrl;
	private List<String> headerValues;
	private List<String> bodyValues;
	private String headerFileName;
	private String footer;
	private List<String> buttonValues;
	private List<String> treatmentId;
	private List<String> campaignId;
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public BroadcastType getBroadcastType() {
		return broadcastType;
	}

	public void setBroadcastType(BroadcastType broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCampName() {
		return campName;
	}

	public void setCampName(String campName) {
		this.campName = campName;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public List<CampUser> getCampUsers() {
		return campUsers;
	}

	public void setCampUsers(List<CampUser> campUsers) {
		this.campUsers = campUsers;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getHeaderFileName() {
		return headerFileName;
	}

	public void setHeaderFileName(String headerFileName) {
		this.headerFileName = headerFileName;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public List<String> getButtonValues() {
		return buttonValues;
	}

	public void setButtonValues(List<String> buttonValues) {
		this.buttonValues = buttonValues;
	}

	public List<String> getHeaderValues() {
		return headerValues;
	}

	public void setHeaderValues(List<String> headerValues) {
		this.headerValues = headerValues;
	}

	public List<String> getBodyValues() {
		return bodyValues;
	}

	public BroadcastTo getBroadcastTo() {
		return broadcastTo;
	}

	public void setBroadcastTo(BroadcastTo broadcastTo) {
		this.broadcastTo = broadcastTo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public void setBodyValues(List<String> bodyValues) {
		this.bodyValues = bodyValues;
	}

	public List<String> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<String> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public SmsRoute getRoute() {
		return route;
	}

	public void setRoute(SmsRoute route) {
		this.route = route;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSalaryRange() {
		return salaryRange;
	}

	public void setSalaryRange(String salaryRange) {
		this.salaryRange = salaryRange;
	}

	public String getLeadType() {
		return leadType;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public String getLeadStage() {
		return leadStage;
	}

	public void setLeadStage(String leadStage) {
		this.leadStage = leadStage;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public Boolean getIsPatientCreated() {
		return isPatientCreated;
	}

	public void setIsPatientCreated(Boolean isPatientCreated) {
		this.isPatientCreated = isPatientCreated;
	}

	public Boolean getIsMobileNumberPresent() {
		return isMobileNumberPresent;
	}

	public void setIsMobileNumberPresent(Boolean isMobileNumberPresent) {
		this.isMobileNumberPresent = isMobileNumberPresent;
	}

	public String getIsPhotoUpload() {
		return isPhotoUpload;
	}

	public void setIsPhotoUpload(String isPhotoUpload) {
		this.isPhotoUpload = isPhotoUpload;
	}

	public String getFollowupType() {
		return followupType;
	}

	public void setFollowupType(String followupType) {
		this.followupType = followupType;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getDateFilterType() {
		return dateFilterType;
	}

	public void setDateFilterType(String dateFilterType) {
		this.dateFilterType = dateFilterType;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<String> getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(List<String> doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(String smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public List<String> getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(List<String> campaignId) {
		this.campaignId = campaignId;
	}

}
