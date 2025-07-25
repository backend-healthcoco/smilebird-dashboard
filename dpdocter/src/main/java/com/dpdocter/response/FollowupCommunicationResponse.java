package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.enums.FollowupStatus;
import com.dpdocter.enums.FollowupType;

public class FollowupCommunicationResponse {
	private String id;

	private String userName;

	private String mobileNumber;

	private String city;

	private String memberId;

	private String memberName;

	private Date dateTime;

	private String comment;

	private FollowupType followupType;

	private Boolean treatmentDone = false;

	private Boolean priceIssue = false;

	private Boolean distanceIssue = false;

	private Boolean isDropLead = false;

	private FollowupStatus followupStatus;

	private Double quotedAmount = 0.0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public FollowupType getFollowupType() {
		return followupType;
	}

	public void setFollowupType(FollowupType followupType) {
		this.followupType = followupType;
	}

	public Boolean getTreatmentDone() {
		return treatmentDone;
	}

	public void setTreatmentDone(Boolean treatmentDone) {
		this.treatmentDone = treatmentDone;
	}

	public Boolean getPriceIssue() {
		return priceIssue;
	}

	public void setPriceIssue(Boolean priceIssue) {
		this.priceIssue = priceIssue;
	}

	public Boolean getDistanceIssue() {
		return distanceIssue;
	}

	public void setDistanceIssue(Boolean distanceIssue) {
		this.distanceIssue = distanceIssue;
	}

	public Boolean getIsDropLead() {
		return isDropLead;
	}

	public void setIsDropLead(Boolean isDropLead) {
		this.isDropLead = isDropLead;
	}

	public FollowupStatus getFollowupStatus() {
		return followupStatus;
	}

	public void setFollowupStatus(FollowupStatus followupStatus) {
		this.followupStatus = followupStatus;
	}

	public Double getQuotedAmount() {
		return quotedAmount;
	}

	public void setQuotedAmount(Double quotedAmount) {
		this.quotedAmount = quotedAmount;
	}

}
