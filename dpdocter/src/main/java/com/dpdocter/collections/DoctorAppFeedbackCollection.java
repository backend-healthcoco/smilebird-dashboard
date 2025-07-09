package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_app_feedback_cl")
public class DoctorAppFeedbackCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private Integer interconnectednessOfPlatform;
	@Field
	private Integer usefulnessOFPrescription;
	@Field
	private Integer usefulOfListing;
	@Field
	private Integer usefulOfAppointment;
	@Field
	private Integer valueAdditionToPractice;
	@Field
	private Integer timeSavingInPrescription;
	@Field
	private Integer timeSavingInAppointments;
	@Field
	private Integer timeSavingInReports;
	@Field
	private Integer legalSafetyInPrescription;
	@Field
	private Integer legalSafetyInDrugsInformation;
	@Field
	private Integer legalSafetyInDrugsInteraction;
	@Field
	private Integer legalSafetyInProvisionalDiagnosis;
	@Field
	private Integer legalSafetyInEMR;
	@Field
	private Integer legalSafetyInMCICompliance;
	@Field
	private Integer recordKeeping;
	@Field
	private Integer presentationSkillOverall;
	@Field
	private Integer presentationSkillExample;
	@Field
	private Integer pricing;
	@Field
	private String customizationNeeded;
	@Field
	private String commentOnVirtualReality;
	@Field
	private String suggesstions;
	@Field
	private String doctorName;
	@Field
	private String presentedBy;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getInterconnectednessOfPlatform() {
		return interconnectednessOfPlatform;
	}

	public void setInterconnectednessOfPlatform(Integer interconnectednessOfPlatform) {
		this.interconnectednessOfPlatform = interconnectednessOfPlatform;
	}

	public Integer getUsefulnessOFPrescription() {
		return usefulnessOFPrescription;
	}

	public void setUsefulnessOFPrescription(Integer usefulnessOFPrescription) {
		this.usefulnessOFPrescription = usefulnessOFPrescription;
	}

	public Integer getUsefulOfListing() {
		return usefulOfListing;
	}

	public void setUsefulOfListing(Integer usefulOfListing) {
		this.usefulOfListing = usefulOfListing;
	}

	public Integer getUsefulOfAppointment() {
		return usefulOfAppointment;
	}

	public void setUsefulOfAppointment(Integer usefulOfAppointment) {
		this.usefulOfAppointment = usefulOfAppointment;
	}

	public Integer getValueAdditionToPractice() {
		return valueAdditionToPractice;
	}

	public void setValueAdditionToPractice(Integer valueAdditionToPractice) {
		this.valueAdditionToPractice = valueAdditionToPractice;
	}

	public Integer getTimeSavingInPrescription() {
		return timeSavingInPrescription;
	}

	public void setTimeSavingInPrescription(Integer timeSavingInPrescription) {
		this.timeSavingInPrescription = timeSavingInPrescription;
	}

	public Integer getTimeSavingInAppointments() {
		return timeSavingInAppointments;
	}

	public void setTimeSavingInAppointments(Integer timeSavingInAppointments) {
		this.timeSavingInAppointments = timeSavingInAppointments;
	}

	public Integer getTimeSavingInReports() {
		return timeSavingInReports;
	}

	public void setTimeSavingInReports(Integer timeSavingInReports) {
		this.timeSavingInReports = timeSavingInReports;
	}

	public Integer getLegalSafetyInPrescription() {
		return legalSafetyInPrescription;
	}

	public void setLegalSafetyInPrescription(Integer legalSafetyInPrescription) {
		this.legalSafetyInPrescription = legalSafetyInPrescription;
	}

	public Integer getLegalSafetyInDrugsInformation() {
		return legalSafetyInDrugsInformation;
	}

	public void setLegalSafetyInDrugsInformation(Integer legalSafetyInDrugsInformation) {
		this.legalSafetyInDrugsInformation = legalSafetyInDrugsInformation;
	}

	public Integer getLegalSafetyInDrugsInteraction() {
		return legalSafetyInDrugsInteraction;
	}

	public void setLegalSafetyInDrugsInteraction(Integer legalSafetyInDrugsInteraction) {
		this.legalSafetyInDrugsInteraction = legalSafetyInDrugsInteraction;
	}

	public Integer getLegalSafetyInProvisionalDiagnosis() {
		return legalSafetyInProvisionalDiagnosis;
	}

	public void setLegalSafetyInProvisionalDiagnosis(Integer legalSafetyInProvisionalDiagnosis) {
		this.legalSafetyInProvisionalDiagnosis = legalSafetyInProvisionalDiagnosis;
	}

	public Integer getLegalSafetyInEMR() {
		return legalSafetyInEMR;
	}

	public void setLegalSafetyInEMR(Integer legalSafetyInEMR) {
		this.legalSafetyInEMR = legalSafetyInEMR;
	}

	public Integer getLegalSafetyInMCICompliance() {
		return legalSafetyInMCICompliance;
	}

	public void setLegalSafetyInMCICompliance(Integer legalSafetyInMCICompliance) {
		this.legalSafetyInMCICompliance = legalSafetyInMCICompliance;
	}

	public Integer getRecordKeeping() {
		return recordKeeping;
	}

	public void setRecordKeeping(Integer recordKeeping) {
		this.recordKeeping = recordKeeping;
	}

	public Integer getPresentationSkillOverall() {
		return presentationSkillOverall;
	}

	public void setPresentationSkillOverall(Integer presentationSkillOverall) {
		this.presentationSkillOverall = presentationSkillOverall;
	}

	public Integer getPresentationSkillExample() {
		return presentationSkillExample;
	}

	public void setPresentationSkillExample(Integer presentationSkillExample) {
		this.presentationSkillExample = presentationSkillExample;
	}

	public Integer getPricing() {
		return pricing;
	}

	public void setPricing(Integer pricing) {
		this.pricing = pricing;
	}

	public String getCustomizationNeeded() {
		return customizationNeeded;
	}

	public void setCustomizationNeeded(String customizationNeeded) {
		this.customizationNeeded = customizationNeeded;
	}

	public String getCommentOnVirtualReality() {
		return commentOnVirtualReality;
	}

	public void setCommentOnVirtualReality(String commentOnVirtualReality) {
		this.commentOnVirtualReality = commentOnVirtualReality;
	}

	public String getSuggesstions() {
		return suggesstions;
	}

	public void setSuggesstions(String suggesstions) {
		this.suggesstions = suggesstions;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPresentedBy() {
		return presentedBy;
	}

	public void setPresentedBy(String presentedBy) {
		this.presentedBy = presentedBy;
	}

}
