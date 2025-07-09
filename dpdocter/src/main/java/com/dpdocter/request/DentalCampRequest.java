package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FollowupType;
import com.dpdocter.enums.LeadStage;
import com.dpdocter.enums.LeadType;

public class DentalCampRequest extends GenericCollection {
	private String id;

	private String campName;

	private String campNameId;

	private Boolean isDiscarded = false;

	private String language;

	private String userName;
	
	private String emailAddress;

	private String age;

	private String gender;

	private Address userAddress;

	private String mobileNumber;

	private String whatsAppNumber;

	private List<String> addiction;

	private Boolean isBPPatient = false;

	private Boolean isDiabetesPatient = false;

	private String bloodGlucose;

	private Float systolic;

	private Float diastolic;

	private String pulse;

	private Boolean isDentalAdviceNeeded = false;

	private String complaint;

	private String oralHygiene;

	private String toothExamination;

	private String gumProblem;

	private String address;

	private List<String> imageUrls;

	private Date registrationDate;

	private Boolean isPhotoUpload = false;

	private Boolean isPatientCreated = false;

	private String city;

	private String locality;

	private String profession;

	private LeadType leadType;

	private LeadStage leadStage;

	private Boolean isVisitBeforeTretment = false;

	private Boolean isVisitBeforeBraces = false;

	private Boolean isSendWelcomeSms = false;

	private Boolean isSendWelcomeWhatsapp = false;

	private Boolean isCamp = false;

	private String teethDiagnostic;

	private List<String> treatmentId;
	
	private List<String> reasonIds;

	private String salaryRange;
	
	private Date followUp;
	
	private FollowupType followupType;
	
	private String smileBuddyId;

	private String campaignId;
	
	private Date convertedDate;

	private String dentalStudioId;
	
	private String referredBy;
	
	private List<String> associateDoctorIds;

	private Boolean isRemindYouForTreatment = false;

	private Boolean isSendYouInformativeContentOnWhatsapp = false;

	private List<DentalCampImages> dentalImages;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCampNameId() {
		return campNameId;
	}

	public void setCampNameId(String campNameId) {
		this.campNameId = campNameId;
	}

	public String getCampName() {
		return campName;
	}

	public void setCampName(String campName) {
		this.campName = campName;
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

	public List<String> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<String> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getSalaryRange() {
		return salaryRange;
	}

	public void setSalaryRange(String salaryRange) {
		this.salaryRange = salaryRange;
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

	public String getSmileBuddyId() {
		return smileBuddyId;
	}

	public void setSmileBuddyId(String smileBuddyId) {
		this.smileBuddyId = smileBuddyId;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public Date getConvertedDate() {
		return convertedDate;
	}

	public void setConvertedDate(Date convertedDate) {
		this.convertedDate = convertedDate;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<String> getReasonIds() {
		return reasonIds;
	}

	public void setReasonIds(List<String> reasonIds) {
		this.reasonIds = reasonIds;
	}

	public String getDentalStudioId() {
		return dentalStudioId;
	}

	public void setDentalStudioId(String dentalStudioId) {
		this.dentalStudioId = dentalStudioId;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public List<String> getAssociateDoctorIds() {
		return associateDoctorIds;
	}

	public void setAssociateDoctorIds(List<String> associateDoctorIds) {
		this.associateDoctorIds = associateDoctorIds;
	}

	public Boolean getIsRemindYouForTreatment() {
		return isRemindYouForTreatment;
	}

	public void setIsRemindYouForTreatment(Boolean isRemindYouForTreatment) {
		this.isRemindYouForTreatment = isRemindYouForTreatment;
	}

	public List<DentalCampImages> getDentalImages() {
		return dentalImages;
	}

	public void setDentalImages(List<DentalCampImages> dentalImages) {
		this.dentalImages = dentalImages;
	}

	public Boolean getIsSendYouInformativeContentOnWhatsapp() {
		return isSendYouInformativeContentOnWhatsapp;
	}

	public void setIsSendYouInformativeContentOnWhatsapp(Boolean isSendYouInformativeContentOnWhatsapp) {
		this.isSendYouInformativeContentOnWhatsapp = isSendYouInformativeContentOnWhatsapp;
	}

}
