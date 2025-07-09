package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.enums.ConfexUserState;
import com.dpdocter.enums.EntityType;

@Document(collection = "confex_user_cl")
public class ConfexUserCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private String firstName;

	@Field
	private String userName;

	@Field
	private char[] password;

	@Field
	private char[] salt;

	@Indexed
	private String emailAddress;

	@Indexed
	private String mobileNumber;

	@Field
	private String imageUrl;

	@Field
	private String thumbnailUrl;

	@Field
	private Boolean isActive = false;

	@Field
	private Boolean isVerified = false;

	@Field
	private Boolean setPassword = false;

	@Field
	private String colorCode;

	@Field
	private ConfexUserState userType = ConfexUserState.USER;

	@Field
	private String userUId;

	@Field
	private ObjectId conferenceId;

	@Field
	private Address address;

	@Field
	private DOB dob;

	@Field
	private String designation;

	@Field
	private String organization;

	@Field
	private String registrationType;

	@Field
	private List<ObjectId> specialityids;
	
	@Field
	private ObjectId companyId;

	@Field
	private boolean paymentStatus;
	
	@Field
	private Boolean isDiscarded = false;
	
	@Field
	private String countryCode;
	
	@Field
	private EntityType entity;


	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public char[] getSalt() {
		return salt;
	}

	public void setSalt(char[] salt) {
		this.salt = salt;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public ConfexUserState getUserType() {
		return userType;
	}

	public void setUserType(ConfexUserState userType) {
		this.userType = userType;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public ObjectId getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(ObjectId conferenceId) {
		this.conferenceId = conferenceId;
	}

	public Boolean getSetPassword() {
		return setPassword;
	}

	public void setSetPassword(Boolean setPassword) {
		this.setPassword = setPassword;
	}

	public List<ObjectId> getSpecialityids() {
		return specialityids;
	}

	public void setSpecialityids(List<ObjectId> specialityids) {
		this.specialityids = specialityids;
	}

	public boolean isPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}


	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}
	public ObjectId getCompanyId() {
		return companyId;
	}

	public void setCompanyId(ObjectId companyId) {
		this.companyId = companyId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;

	}

	public EntityType getEntity() {
		return entity;
	}

	public void setEntity(EntityType entity) {
		this.entity = entity;
	}
	
	

}
