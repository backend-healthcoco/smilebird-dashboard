package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.enums.RoleEnum;

@Document(collection = "school_branch_cl")
public class SchoolBranchCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String branchName;
	@Field
	private String emailAddress;
	@Field
	private String userName;
	@Field
	private Address Address;
	@Field
	private String principalName;
	@Field
	private String contactNumber;
	@Field
	private Boolean isActivated = Boolean.FALSE;
	@Field
	private Boolean isVerified = Boolean.FALSE;
	@Field
	private Long fromDate;
	@Field
	private Long toDate;
	@Field
	private ObjectId schoolId;
	@Field
	private String userType = RoleEnum.SCHOOL_BRANCH.getRole();
	@Field
	private Boolean discarded = Boolean.FALSE;
	
	@Field
	private Boolean isShowAssesmentDetails=Boolean.FALSE;
	
	@Field
	private Boolean isShowAnalyticsDetails=Boolean.FALSE;
	
	

	public Boolean getIsShowAssesmentDetails() {
		return isShowAssesmentDetails;
	}

	public void setIsShowAssesmentDetails(Boolean isShowAssesmentDetails) {
		this.isShowAssesmentDetails = isShowAssesmentDetails;
	}

	public Boolean getIsShowAnalyticsDetails() {
		return isShowAnalyticsDetails;
	}

	public void setIsShowAnalyticsDetails(Boolean isShowAnalyticsDetails) {
		this.isShowAnalyticsDetails = isShowAnalyticsDetails;
	}


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Address getAddress() {
		return Address;
	}

	public void setAddress(Address address) {
		Address = address;
	}

	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Boolean getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Long getFromDate() {
		return fromDate;
	}

	public void setFromDate(Long fromDate) {
		this.fromDate = fromDate;
	}

	public Long getToDate() {
		return toDate;
	}

	public void setToDate(Long toDate) {
		this.toDate = toDate;
	}

	public ObjectId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(ObjectId schoolId) {
		this.schoolId = schoolId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
