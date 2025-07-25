package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ContactState;
import com.dpdocter.enums.FollowupType;
import com.dpdocter.enums.UserState;

public class PatientAppUsersResponse  extends GenericCollection{

	private String id;

	private String title;

	private String firstName;

	private String lastName;

	private String middleName;

	private String emailAddress;

	private String countryCode;

	private String mobileNumber;
	
	private String whatsAppNumber;

	private String colorCode;

	private UserState userState = UserState.USERSTATECOMPLETE;

	private boolean signedUp = false;
	
	private Boolean isDentalChainVerified ;
	
	private Boolean isDentalChainPatient ;
	
	private String dentalTreatment;
	
	private String city;

	private ContactState contactState;
	
	private List<String> treatmentNames;
	
	private List<String> treatmentId;
	
	private Date followUp;

	private FollowupType followupType;
	
	private String gender;
	
	private String smileBuddyId;

	private String smileBuddyName;

	public ContactState getContactState() {
		return contactState;
	}

	public void setContactState(ContactState contactState) {
		this.contactState = contactState;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public boolean isSignedUp() {
		return signedUp;
	}

	public void setSignedUp(boolean signedUp) {
		this.signedUp = signedUp;
	}

	public Boolean getIsDentalChainVerified() {
		return isDentalChainVerified;
	}

	public void setIsDentalChainVerified(Boolean isDentalChainVerified) {
		this.isDentalChainVerified = isDentalChainVerified;
	}

	public Boolean getIsDentalChainPatient() {
		return isDentalChainPatient;
	}

	public void setIsDentalChainPatient(Boolean isDentalChainPatient) {
		this.isDentalChainPatient = isDentalChainPatient;
	}

	public String getDentalTreatment() {
		return dentalTreatment;
	}

	public void setDentalTreatment(String dentalTreatment) {
		this.dentalTreatment = dentalTreatment;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<String> getTreatmentNames() {
		return treatmentNames;
	}

	public void setTreatmentNames(List<String> treatmentNames) {
		this.treatmentNames = treatmentNames;
	}

	public List<String> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<String> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public Date getFollowUp() {
		return followUp;
	}

	public void setFollowUp(Date followUp) {
		this.followUp = followUp;
	}

	public FollowupType getFollowupType() {
		return followupType;
	}

	public void setFollowupType(FollowupType followupType) {
		this.followupType = followupType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(String smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public String getSmileBuddyName() {
		return smileBuddyName;
	}

	public void setSmileBuddyName(String smileBuddyName) {
		this.smileBuddyName = smileBuddyName;
	}

	public String getWhatsAppNumber() {
		return whatsAppNumber;
	}

	public void setWhatsAppNumber(String whatsAppNumber) {
		this.whatsAppNumber = whatsAppNumber;
	}

}
