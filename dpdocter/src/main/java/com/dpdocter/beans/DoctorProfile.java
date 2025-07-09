package com.dpdocter.beans;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Field;



public class DoctorProfile {
	private String id;

	private String doctorId;

	private String userId;

	private String title;

	private String firstName;

	private String emailAddress;

	private String mobileNumber;
	
	private String countryCode;

	private String gender;

	private String imageUrl;

	private String thumbnailUrl;

	private DOB dob;

	private String colorCode;

	private String coverImageUrl;

	private String coverThumbnailImageUrl;

	private List<String> additionalNumbers;

	private List<String> otherEmailAddresses;

	private DoctorExperience experience;

	private List<Education> education;

	private List<String> specialities;

	private List<String> services;
	
	private List<Achievement> achievements;

	private String professionalStatement;

	private List<DoctorRegistrationDetail> registrationDetails;

	private List<DoctorExperienceDetail> experienceDetails;

	private List<String> professionalMemberships;

	private List<DoctorClinicProfile> clinicProfile;

	private Boolean isVerified;

	private String metaTitle;

	private String metaDesccription;

	private String metaKeyword;

	private String slugUrl;
	
	private Subscription subscriptionDetail;
	
	
	private String RegistrationImageUrl;

	private String RegistrationThumbnailUrl;
	
	private String photoIdImageUrl;
	
    private Boolean isRegistrationDetailsVerified =false;
	
	private Boolean isPhotoIdVerified =false;
	
	private BulkSmsCredits bulkSmsCredit;
	
	private Date activationDate;	//to set the activation date 

	private Boolean isHealthcocoDoctor=false;
	


	private Boolean isTransactionalSms=true;
	
	
//	private List<WorkingSchedule> onlineWorkingSchedules;
	
//	private Map<DoctorConsultation, String> onlineConsultationFees;
	
//	private List<DoctorConsultation> onlineConsultationType;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public List<String> getAdditionalNumbers() {
		return additionalNumbers;
	}

	public void setAdditionalNumbers(List<String> additionalNumbers) {
		this.additionalNumbers = additionalNumbers;
	}

	public List<String> getOtherEmailAddresses() {
		return otherEmailAddresses;
	}

	public void setOtherEmailAddresses(List<String> otherEmailAddresses) {
		this.otherEmailAddresses = otherEmailAddresses;
	}

	public DoctorExperience getExperience() {
		return experience;
	}

	public void setExperience(DoctorExperience experience) {
		this.experience = experience;
	}

	public List<Education> getEducation() {
		return education;
	}

	public void setEducation(List<Education> education) {
		this.education = education;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	public String getProfessionalStatement() {
		return professionalStatement;
	}

	public void setProfessionalStatement(String professionalStatement) {
		this.professionalStatement = professionalStatement;
	}

	public List<DoctorRegistrationDetail> getRegistrationDetails() {
		return registrationDetails;
	}

	public void setRegistrationDetails(List<DoctorRegistrationDetail> registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	public List<DoctorExperienceDetail> getExperienceDetails() {
		return experienceDetails;
	}

	public void setExperienceDetails(List<DoctorExperienceDetail> experienceDetails) {
		this.experienceDetails = experienceDetails;
	}

	public List<String> getProfessionalMemberships() {
		return professionalMemberships;
	}

	public void setProfessionalMemberships(List<String> professionalMemberships) {
		this.professionalMemberships = professionalMemberships;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public List<DoctorClinicProfile> getClinicProfile() {
		return clinicProfile;
	}

	public void setClinicProfile(List<DoctorClinicProfile> clinicProfile) {
		this.clinicProfile = clinicProfile;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getCoverThumbnailImageUrl() {
		return coverThumbnailImageUrl;
	}

	public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getMetaDesccription() {
		return metaDesccription;
	}

	public void setMetaDesccription(String metaDesccription) {
		this.metaDesccription = metaDesccription;
	}

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public String getSlugUrl() {
		return slugUrl;
	}

	public void setSlugUrl(String slugUrl) {
		this.slugUrl = slugUrl;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}
	
	

//	public List<WorkingSchedule> getOnlineWorkingSchedules() {
//		return onlineWorkingSchedules;
//	}
//
//	public void setOnlineWorkingSchedules(List<WorkingSchedule> onlineWorkingSchedules) {
//		this.onlineWorkingSchedules = onlineWorkingSchedules;
//	}
//
//	
//
//	public Map<DoctorConsultation, String> getOnlineConsultationFees() {
//		return onlineConsultationFees;
//	}
//
//	public void setOnlineConsultationFees(Map<DoctorConsultation, String> onlineConsultationFees) {
//		this.onlineConsultationFees = onlineConsultationFees;
//	}
//
//	public List<DoctorConsultation> getOnlineConsultationType() {
//		return onlineConsultationType;
//	}
//
//	public void setOnlineConsultationType(List<DoctorConsultation> onlineConsultationType) {
//		this.onlineConsultationType = onlineConsultationType;
//	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Subscription getSubscriptionDetail() {
		return subscriptionDetail;
	}

	public void setSubscriptionDetail(Subscription subscriptionDetail) {
		this.subscriptionDetail = subscriptionDetail;
	}
	
	

	public Boolean getIsRegistrationDetailsVerified() {
		return isRegistrationDetailsVerified;
	}

	public void setIsRegistrationDetailsVerified(Boolean isRegistrationDetailsVerified) {
		this.isRegistrationDetailsVerified = isRegistrationDetailsVerified;
	}

	public Boolean getIsPhotoIdVerified() {
		return isPhotoIdVerified;
	}

	public void setIsPhotoIdVerified(Boolean isPhotoIdVerified) {
		this.isPhotoIdVerified = isPhotoIdVerified;
	}

	public String getRegistrationImageUrl() {
		return RegistrationImageUrl;
	}

	public void setRegistrationImageUrl(String registrationImageUrl) {
		RegistrationImageUrl = registrationImageUrl;
	}

	public String getRegistrationThumbnailUrl() {
		return RegistrationThumbnailUrl;
	}

	public void setRegistrationThumbnailUrl(String registrationThumbnailUrl) {
		RegistrationThumbnailUrl = registrationThumbnailUrl;
	}

	public String getPhotoIdImageUrl() {
		return photoIdImageUrl;
	}

	public void setPhotoIdImageUrl(String photoIdImageUrl) {
		this.photoIdImageUrl = photoIdImageUrl;
	}
	
	
	

	public BulkSmsCredits getBulkSmsCredit() {
		return bulkSmsCredit;
	}

	public void setBulkSmsCredit(BulkSmsCredits bulkSmsCredit) {
		this.bulkSmsCredit = bulkSmsCredit;
	}

	
	
	public Boolean getIsHealthcocoDoctor() {
		return isHealthcocoDoctor;
	}

	public void setIsHealthcocoDoctor(Boolean isHealthcocoDoctor) {
		this.isHealthcocoDoctor = isHealthcocoDoctor;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}
	
	

	public Boolean getIsTransactionalSms() {
		return isTransactionalSms;
	}

	public void setIsTransactionalSms(Boolean isTransactionalSms) {
		this.isTransactionalSms = isTransactionalSms;
	}

	@Override
	public String toString() {
		return "DoctorProfile [id=" + id + ", doctorId=" + doctorId + ", userId=" + userId + ", title=" + title
				+ ", firstName=" + firstName + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
				+ ", countryCode=" + countryCode + ", gender=" + gender + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", dob=" + dob + ", colorCode=" + colorCode + ", coverImageUrl=" + coverImageUrl
				+ ", coverThumbnailImageUrl=" + coverThumbnailImageUrl + ", additionalNumbers=" + additionalNumbers
				+ ", otherEmailAddresses=" + otherEmailAddresses + ", experience=" + experience + ", education="
				+ education + ", specialities=" + specialities + ", services=" + services + ", achievements="
				+ achievements + ", professionalStatement=" + professionalStatement + ", registrationDetails="
				+ registrationDetails + ", experienceDetails=" + experienceDetails + ", professionalMemberships="
				+ professionalMemberships + ", clinicProfile=" + clinicProfile + ", isVerified=" + isVerified
				+ ", metaTitle=" + metaTitle + ", metaDesccription=" + metaDesccription + ", metaKeyword=" + metaKeyword
				+ ", slugUrl=" + slugUrl + ", subscriptionDetail=" + subscriptionDetail + "]";
	}

	

}
