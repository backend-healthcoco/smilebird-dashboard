package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.enums.FollowupType;
import com.dpdocter.enums.LeadStage;
import com.dpdocter.enums.LeadType;
import com.dpdocter.request.DentalCampImages;

@Document(collection = "dental_camp_cl")
public class DentalCampCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String campName;

	@Field
	private ObjectId campNameId;

	@Field
	private Boolean isDiscarded = false;

	@Field
	private String language;

	@Field
	private String userName;

	@Field
	private String emailAddress;

	@Field
	private String age;

	@Field
	private String gender;

	@Field
	private Address userAddress;

	@Field
	private String mobileNumber;

	@Field
	private String whatsAppNumber;

	@Field
	private List<String> addiction;

	@Field
	private Boolean isBPPatient = false;

	@Field
	private Boolean isDiabetesPatient = false;

	@Field
	private String bloodGlucose;

	@Field
	private Float systolic;

	@Field
	private Float diastolic;

	@Field
	private String pulse;

	@Field
	private Boolean isDentalAdviceNeeded = false;

	@Field
	private String complaint;

	@Field
	private String oralHygiene;

	@Field
	private String toothExamination;

	@Field
	private String gumProblem;

	@Field
	private Date followUp;

	@Field
	private String followUpReason;

	@Field
	private String address;

	@Field
	private List<String> imageUrls;

	@Field
	private Date registrationDate;

	@Field
	private Boolean isPhotoUpload = false;

	@Field
	private Boolean isPatientCreated = false;

	@Field
	private String city;

	@Field
	private String locality;

	@Field
	private LeadType leadType;

	@Field
	private LeadStage leadStage;

	@Field
	private String profession;

	@Field
	private Boolean isVisitBeforeTretment = false;

	@Field
	private Boolean isVisitBeforeBraces = false;

	@Field
	private FollowupType followupType;

	@Field
	private Boolean isSendWelcomeSms = false;

	@Field
	private Boolean isSendWelcomeWhatsapp = false;

	@Field
	private String teethDiagnostic;

	@Field
	private Boolean isCamp = false;

	@Field
	private List<ObjectId> treatmentId;

	@Field
	private String salaryRange;

	@Field
	private ObjectId smileBuddyId;

	@Field
	private Date convertedDate;

	@Field
	private ObjectId campaignId;

	@Field
	private List<ObjectId> reasonIds;

	@Field
	private ObjectId dentalStudioId;

	@Field
	private ObjectId referredBy;

	@Field
	private List<ObjectId> associateDoctorIds;
	@Field
	private Boolean isRemindYouForTreatment = false;

	@Field
	private Boolean isSendYouInformativeContentOnWhatsapp = false;

	@Field
	private List<DentalCampImages> dentalImages;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCampName() {
		return campName;
	}

	public void setCampName(String campName) {
		this.campName = campName;
	}

	public ObjectId getCampNameId() {
		return campNameId;
	}

	public void setCampNameId(ObjectId campNameId) {
		this.campNameId = campNameId;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Address getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(Address userAddress) {
		this.userAddress = userAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getWhatsAppNumber() {
		return whatsAppNumber;
	}

	public void setWhatsAppNumber(String whatsAppNumber) {
		this.whatsAppNumber = whatsAppNumber;
	}

	public List<String> getAddiction() {
		return addiction;
	}

	public void setAddiction(List<String> addiction) {
		this.addiction = addiction;
	}

	public Boolean getIsBPPatient() {
		return isBPPatient;
	}

	public void setIsBPPatient(Boolean isBPPatient) {
		this.isBPPatient = isBPPatient;
	}

	public Boolean getIsDiabetesPatient() {
		return isDiabetesPatient;
	}

	public void setIsDiabetesPatient(Boolean isDiabetesPatient) {
		this.isDiabetesPatient = isDiabetesPatient;
	}

	public String getBloodGlucose() {
		return bloodGlucose;
	}

	public void setBloodGlucose(String bloodGlucose) {
		this.bloodGlucose = bloodGlucose;
	}

	public Float getSystolic() {
		return systolic;
	}

	public void setSystolic(Float systolic) {
		this.systolic = systolic;
	}

	public Float getDiastolic() {
		return diastolic;
	}

	public void setDiastolic(Float diastolic) {
		this.diastolic = diastolic;
	}

	public String getPulse() {
		return pulse;
	}

	public void setPulse(String pulse) {
		this.pulse = pulse;
	}

	public Boolean getIsDentalAdviceNeeded() {
		return isDentalAdviceNeeded;
	}

	public void setIsDentalAdviceNeeded(Boolean isDentalAdviceNeeded) {
		this.isDentalAdviceNeeded = isDentalAdviceNeeded;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getOralHygiene() {
		return oralHygiene;
	}

	public void setOralHygiene(String oralHygiene) {
		this.oralHygiene = oralHygiene;
	}

	public String getToothExamination() {
		return toothExamination;
	}

	public void setToothExamination(String toothExamination) {
		this.toothExamination = toothExamination;
	}

	public String getGumProblem() {
		return gumProblem;
	}

	public void setGumProblem(String gumProblem) {
		this.gumProblem = gumProblem;
	}

	public Date getFollowUp() {
		return followUp;
	}

	public void setFollowUp(Date followUp) {
		this.followUp = followUp;
	}

	public String getFollowUpReason() {
		return followUpReason;
	}

	public void setFollowUpReason(String followUpReason) {
		this.followUpReason = followUpReason;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Boolean getIsPhotoUpload() {
		return isPhotoUpload;
	}

	public void setIsPhotoUpload(Boolean isPhotoUpload) {
		this.isPhotoUpload = isPhotoUpload;
	}

	public Boolean getIsPatientCreated() {
		return isPatientCreated;
	}

	public void setIsPatientCreated(Boolean isPatientCreated) {
		this.isPatientCreated = isPatientCreated;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public LeadType getLeadType() {
		return leadType;
	}

	public void setLeadType(LeadType leadType) {
		this.leadType = leadType;
	}

	public LeadStage getLeadStage() {
		return leadStage;
	}

	public void setLeadStage(LeadStage leadStage) {
		this.leadStage = leadStage;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public Boolean getIsVisitBeforeTretment() {
		return isVisitBeforeTretment;
	}

	public void setIsVisitBeforeTretment(Boolean isVisitBeforeTretment) {
		this.isVisitBeforeTretment = isVisitBeforeTretment;
	}

	public Boolean getIsVisitBeforeBraces() {
		return isVisitBeforeBraces;
	}

	public void setIsVisitBeforeBraces(Boolean isVisitBeforeBraces) {
		this.isVisitBeforeBraces = isVisitBeforeBraces;
	}

	public FollowupType getFollowupType() {
		return followupType;
	}

	public void setFollowupType(FollowupType followupType) {
		this.followupType = followupType;
	}

	public Boolean getIsSendWelcomeSms() {
		return isSendWelcomeSms;
	}

	public void setIsSendWelcomeSms(Boolean isSendWelcomeSms) {
		this.isSendWelcomeSms = isSendWelcomeSms;
	}

	public Boolean getIsSendWelcomeWhatsapp() {
		return isSendWelcomeWhatsapp;
	}

	public void setIsSendWelcomeWhatsapp(Boolean isSendWelcomeWhatsapp) {
		this.isSendWelcomeWhatsapp = isSendWelcomeWhatsapp;
	}

	public String getTeethDiagnostic() {
		return teethDiagnostic;
	}

	public void setTeethDiagnostic(String teethDiagnostic) {
		this.teethDiagnostic = teethDiagnostic;
	}

	public Boolean getIsCamp() {
		return isCamp;
	}

	public void setIsCamp(Boolean isCamp) {
		this.isCamp = isCamp;
	}

	public List<ObjectId> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<ObjectId> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getSalaryRange() {
		return salaryRange;
	}

	public void setSalaryRange(String salaryRange) {
		this.salaryRange = salaryRange;
	}

	public ObjectId getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(ObjectId smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public Date getConvertedDate() {
		return convertedDate;
	}

	public void setConvertedDate(Date convertedDate) {
		this.convertedDate = convertedDate;
	}

	public ObjectId getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(ObjectId campaignId) {
		this.campaignId = campaignId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<ObjectId> getReasonIds() {
		return reasonIds;
	}

	public void setReasonIds(List<ObjectId> reasonIds) {
		this.reasonIds = reasonIds;
	}

	public ObjectId getDentalStudioId() {
		return dentalStudioId;
	}

	public void setDentalStudioId(ObjectId dentalStudioId) {
		this.dentalStudioId = dentalStudioId;
	}

	public ObjectId getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(ObjectId referredBy) {
		this.referredBy = referredBy;
	}

	public List<ObjectId> getAssociateDoctorIds() {
		return associateDoctorIds;
	}

	public void setAssociateDoctorIds(List<ObjectId> associateDoctorIds) {
		this.associateDoctorIds = associateDoctorIds;
	}

	public Boolean getIsRemindYouForTreatment() {
		return isRemindYouForTreatment;
	}

	public void setIsRemindYouForTreatment(Boolean isRemindYouForTreatment) {
		this.isRemindYouForTreatment = isRemindYouForTreatment;
	}

	public Boolean getIsSendYouInformativeContentOnWhatsapp() {
		return isSendYouInformativeContentOnWhatsapp;
	}

	public void setIsSendYouInformativeContentOnWhatsapp(Boolean isSendYouInformativeContentOnWhatsapp) {
		this.isSendYouInformativeContentOnWhatsapp = isSendYouInformativeContentOnWhatsapp;
	}

	public List<DentalCampImages> getDentalImages() {
		return dentalImages;
	}

	public void setDentalImages(List<DentalCampImages> dentalImages) {
		this.dentalImages = dentalImages;
	}

}
