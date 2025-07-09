package com.dpdocter.collections;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Achievement;
import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorExperienceDetail;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.Education;

@Document(collection = "docter_cl")
public class DoctorCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private List<String> additionalNumbers;

	@Field
	private List<String> otherEmailAddresses;

	@Indexed
	private ObjectId userId;

	@Field
	private DoctorExperience experience;

	@Field
	private List<Education> education;

	@Indexed
	private List<ObjectId> specialities;

	@Indexed
	private Set<ObjectId> services;

	@Field
	private List<Achievement> achievements;
	
	@Field
	private List<String> trainingsCertifications;

	@Field
	private String professionalStatement;

	@Field
	private List<DoctorRegistrationDetail> registrationDetails;

	@Field
	private List<DoctorExperienceDetail> experienceDetails;

	@Field
	private List<ObjectId> professionalMemberships;

	@Field
	private String registerNumber;

	@Field
	private String gender;

	@Field
	private DOB dob;

	@Field
	private Boolean isVerified;

	@Field
	private List<ClinicImage> verificationDocuments;

	@Field
	private String metaTitle;

	@Field
	private String metaDesccription;

	@Field
	private String metaKeyword;

	@Field
	private String slugUrl;

	@Field
	private String mrCode;

	@Field
	private List<ObjectId> divisionIds;

	@Field
	private ObjectId cityId;
	
	@Field
	private String RegistrationImageUrl;
	@Field
	private String RegistrationThumbnailUrl;
	
	@Field
	private String photoIdImageUrl;
	
	@Field
	private Boolean isRegistrationDetailsVerified =false;
	
	@Field
	private Boolean isPhotoIdVerified =false;
	
	@Field
	private BulkSmsCredits bulkSmsCredit;
	
	@Field
	private Boolean isHealthcocoDoctor=false;
	
	@Field
	private Boolean isExpUpdated=false;
	
	@Field
	private Boolean isTransactionalSms=true;


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
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

	public List<ObjectId> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<ObjectId> specialities) {
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

	public List<ObjectId> getProfessionalMemberships() {
		return professionalMemberships;
	}

	public void setProfessionalMemberships(List<ObjectId> professionalMemberships) {
		this.professionalMemberships = professionalMemberships;
	}

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
		if (this.dob != null)
			dob.getAge();
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public List<ClinicImage> getVerificationDocuments() {
		return verificationDocuments;
	}

	public void setVerificationDocuments(List<ClinicImage> verificationDocuments) {
		this.verificationDocuments = verificationDocuments;
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

	public Set<ObjectId> getServices() {
		return services;
	}

	public void setServices(Set<ObjectId> servicess) {
		this.services = servicess;
	}

	public String getMrCode() {
		return mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}

	public List<ObjectId> getDivisionIds() {
		return divisionIds;
	}

	public void setDivisionIds(List<ObjectId> divisionIds) {
		this.divisionIds = divisionIds;
	}

	public ObjectId getCityId() {
		return cityId;
	}

	public void setCityId(ObjectId cityId) {
		this.cityId = cityId;
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
	
	

	public Boolean getIsExpUpdated() {
		return isExpUpdated;
	}

	public void setIsExpUpdated(Boolean isExpUpdated) {
		this.isExpUpdated = isExpUpdated;
	}
	
	

	public List<String> getTrainingsCertifications() {
		return trainingsCertifications;
	}

	public void setTrainingsCertifications(List<String> trainingsCertifications) {
		this.trainingsCertifications = trainingsCertifications;
	}

	public Boolean getIsTransactionalSms() {
		return isTransactionalSms;
	}

	public void setIsTransactionalSms(Boolean isTransactionalSms) {
		this.isTransactionalSms = isTransactionalSms;
	}

	@Override
	public String toString() {
		return "DoctorCollection [id=" + id + ", additionalNumbers=" + additionalNumbers + ", otherEmailAddresses="
				+ otherEmailAddresses + ", userId=" + userId + ", experience=" + experience + ", education=" + education
				+ ", specialities=" + specialities + ", services=" + services + ", achievements=" + achievements
				+ ", professionalStatement=" + professionalStatement + ", registrationDetails=" + registrationDetails
				+ ", experienceDetails=" + experienceDetails + ", professionalMemberships=" + professionalMemberships
				+ ", registerNumber=" + registerNumber + ", gender=" + gender + ", dob=" + dob + ", isVerified="
				+ isVerified + ", verificationDocuments=" + verificationDocuments + ", metaTitle=" + metaTitle
				+ ", metaDesccription=" + metaDesccription + ", metaKeyword=" + metaKeyword + ", slugUrl=" + slugUrl
				+ "]";
	}
}
