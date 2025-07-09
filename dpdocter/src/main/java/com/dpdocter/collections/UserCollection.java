package com.dpdocter.collections;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.AdminType;
import com.dpdocter.enums.ContactState;
import com.dpdocter.enums.FollowupType;
import com.dpdocter.enums.UserState;
import com.mongodb.DBObject;

@Document(collection = "user_cl")
public class UserCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private String firstName;

	@Field
	private String lastName;

	@Field
	private String middleName;

	@Indexed(unique = true)
	private String userName;

	@Field
	private char[] password;

	@Field
	private char[] salt;

	@Indexed
	private String emailAddress;

	@Field
	private String countryCode;

	@Indexed
	private String mobileNumber;
	
	@Field
	private String whatsAppNumber;

	@Field
	private String imageUrl;

	@Field
	private String thumbnailUrl;

	@Field
	private Boolean isActive = false;

	@Field
	private Boolean isVerified = false;

	@Field
	private Boolean isDentalChainVerified = false;

	@Field
	private Boolean isDentalChainPatient = false;

	@Field
	private String coverImageUrl;

	@Field
	private String coverThumbnailImageUrl;

	@Field
	private String colorCode;

	@Field
	private UserState userState = UserState.USERSTATECOMPLETE;

	@Field
	private ContactState contactState = ContactState.FIRSTCALL;

	@Field
	private Date lastSession;

	@Field
	private boolean signedUp = false;

	@Field
	private String userUId;

	@Field
	private Integer regularCheckUpMonths;

	@Field
	private Boolean isMedicalStudent = false;

	// this two field used only at ADMIN side
	@Field
	private AdminType adminType = AdminType.ADMIN;

	@Field
	private String city;

	@Field
	private String notes;

	@Field
	private Boolean isPasswordSet = false;

	@Field
	private String registrationNumber;

	@Field
	private String qualification;

	@Field
	private Boolean isDoctorListed = false;

	@Field
	private Boolean isAnonymousAppointment = false;

	@Field
	private Date activationDate; // to set the activation date

	@Field
	private String dentalTreatment;

	@Field
	private Date followUp;

	@Field
	private List<String> platform;

	@Field
	private String followUpReason;
	@Field
	private Boolean isBuddy = false;
	@Field
	private Boolean isAgent = false;
	@Field
	private Boolean isCampDoctor = false;
	@Field
	private List<ObjectId> treatmentId;

	@Field
	private String gender;
	
	@Field
	private Integer age;

	@Field
	private FollowupType followupType;
	
	@Field
	private ObjectId smileBuddyId;
	
	@Field
	private String language;
	
	@Field
	private String createdFrom;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public AdminType getAdminType() {
		return adminType;
	}

	public void setAdminType(AdminType adminType) {
		this.adminType = adminType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
//		if (firstName != null)
//			this.firstName = WordUtils.capitalize(firstName.toLowerCase());
//		else
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	// public Boolean getIsTempPassword() {
	// return isTempPassword;
	// }
	//
	// public void setIsTempPassword(Boolean isTempPassword) {
	// this.isTempPassword = isTempPassword;
	// }

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getCoverThumbnailImageUrl() {
		return coverThumbnailImageUrl;
	}

	public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public Date getLastSession() {
		return lastSession;
	}

	public void setLastSession(Date lastSession) {
		this.lastSession = lastSession;
	}

	public boolean isSignedUp() {
		return signedUp;
	}

	public void setSignedUp(boolean signedUp) {
		this.signedUp = signedUp;
	}

	public char[] getSalt() {
		return salt;
	}

	public void setSalt(char[] salt) {
		this.salt = salt;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public Boolean getIsDoctorListed() {
		return isDoctorListed;
	}

	public void setIsDoctorListed(Boolean isDoctorListed) {
		this.isDoctorListed = isDoctorListed;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public Boolean getIsAnonymousAppointment() {
		return isAnonymousAppointment;
	}

	public void setIsAnonymousAppointment(Boolean isAnonymousAppointment) {
		this.isAnonymousAppointment = isAnonymousAppointment;
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

	public ContactState getContactState() {
		return contactState;
	}

	public void setContactState(ContactState contactState) {
		this.contactState = contactState;
	}

	public String getCreatedFrom() {
		return createdFrom;
	}

	public void setCreatedFrom(String createdFrom) {
		this.createdFrom = createdFrom;
	}

	public UserCollection() {
		super();
	}

	public UserCollection(ObjectId id, String title, String firstName, String lastName, String middleName,
			String userName, char[] password, char[] salt, String emailAddress, String mobileNumber, String imageUrl,
			String thumbnailUrl, Boolean isActive, Boolean isVerified, String coverImageUrl,
			String coverThumbnailImageUrl, String colorCode, UserState userState, Date lastSession, boolean signedUp,
			String userUId, String countryCode) {
		super();
		this.id = id;
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.userName = userName;
		this.password = password;
		this.salt = salt;
		this.emailAddress = emailAddress;
		this.mobileNumber = mobileNumber;
		this.countryCode = countryCode;
		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.isActive = isActive;
		this.isVerified = isVerified;
		this.coverImageUrl = coverImageUrl;
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
		this.colorCode = colorCode;
		this.userState = userState;
		this.lastSession = lastSession;
		this.signedUp = signedUp;
		this.userUId = userUId;
	}

	public UserCollection(DBObject dbObject) {
		this.title = (String) dbObject.get("title");
		;
		this.firstName = (String) dbObject.get("firstName");
		this.lastName = (String) dbObject.get("lastName");
		this.middleName = (String) dbObject.get("middleName");
		this.userName = (String) dbObject.get("userName");
		this.password = (char[]) dbObject.get("password");
		this.emailAddress = (String) dbObject.get("emailAddress");
		this.isActive = (Boolean) dbObject.get("emailAddress");
		this.userUId = (String) dbObject.get("userUID");
	}

	@Override
	public String toString() {
		return "UserCollection [id=" + id + ", title=" + title + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", middleName=" + middleName + ", userName=" + userName + ", password=" + Arrays.toString(password)
				+ ", salt=" + Arrays.toString(salt) + ", emailAddress=" + emailAddress + ", countryCode=" + countryCode
				+ ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl
				+ ", isActive=" + isActive + ", isVerified=" + isVerified + ", coverImageUrl=" + coverImageUrl
				+ ", coverThumbnailImageUrl=" + coverThumbnailImageUrl + ", colorCode=" + colorCode + ", userState="
				+ userState + ", lastSession=" + lastSession + ", signedUp=" + signedUp + ", userUId=" + userUId
				+ ", regularCheckUpMonths=" + regularCheckUpMonths + ", isMedicalStudent=" + isMedicalStudent
				+ ", adminType=" + adminType + ", city=" + city + ", notes=" + notes + ", isPasswordSet="
				+ isPasswordSet + ", registrationNumber=" + registrationNumber + ", qualification=" + qualification
				+ ", isDoctorListed=" + isDoctorListed + "]";
	}

	public Boolean getIsPasswordSet() {
		return isPasswordSet;
	}

	public void setIsPasswordSet(Boolean isPasswordSet) {
		this.isPasswordSet = isPasswordSet;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getRegularCheckUpMonths() {
		return regularCheckUpMonths;
	}

	public void setRegularCheckUpMonths(Integer regularCheckUpMonths) {
		this.regularCheckUpMonths = regularCheckUpMonths;
	}

	public Boolean getIsMedicalStudent() {
		return isMedicalStudent;
	}

	public void setIsMedicalStudent(Boolean isMedicalStudent) {
		this.isMedicalStudent = isMedicalStudent;
	}

	public Date getFollowUp() {
		return followUp;
	}

	public void setFollowUp(Date followUp) {
		this.followUp = followUp;
	}

	public List<String> getPlatform() {
		return platform;
	}

	public void setPlatform(List<String> platform) {
		this.platform = platform;
	}

	public String getFollowUpReason() {
		return followUpReason;
	}

	public void setFollowUpReason(String followUpReason) {
		this.followUpReason = followUpReason;
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

	public List<ObjectId> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<ObjectId> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public FollowupType getFollowupType() {
		return followupType;
	}

	public void setFollowupType(FollowupType followupType) {
		this.followupType = followupType;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public ObjectId getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(ObjectId smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getWhatsAppNumber() {
		return whatsAppNumber;
	}

	public void setWhatsAppNumber(String whatsAppNumber) {
		this.whatsAppNumber = whatsAppNumber;
	}

	public Boolean getIsCampDoctor() {
		return isCampDoctor;
	}

	public void setIsCampDoctor(Boolean isCampDoctor) {
		this.isCampDoctor = isCampDoctor;
	}
	
}
