package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.DoctorClinicImage;
import com.dpdocter.beans.DoctorDetails;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorOnlineConsultationAddEditRequest;
import com.dpdocter.beans.DoctorOnlineGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.DoctorRegistrationDetails;
import com.dpdocter.beans.DoctorSlugUrlRequest;
import com.dpdocter.beans.DoctorTrainingAddEditRequest;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Services;
import com.dpdocter.beans.Speciality;
import com.dpdocter.enums.RegistrationType;
import com.dpdocter.request.AddEditMRCodeRequest;
import com.dpdocter.request.AddEditNutritionistRequest;
import com.dpdocter.request.AddEditSEORequest;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAddEditCityRequest;
import com.dpdocter.request.DoctorAddEditFacilityRequest;
import com.dpdocter.request.DoctorAppointmentNumbersAddEditRequest;
import com.dpdocter.request.DoctorAppointmentSlotAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorDOBAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorExperienceDetailAddEditRequest;
import com.dpdocter.request.DoctorGenderAddEditRequest;
import com.dpdocter.request.DoctorMultipleDataAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorOnlineWorkingTimeRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorServicesAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface DoctorProfileService {

	Boolean addEditName(DoctorNameAddEditRequest request);

	DoctorExperience addEditExperience(DoctorExperienceAddEditRequest request);

	Boolean addEditContact(DoctorContactAddEditRequest request);

	Boolean addEditEducation(DoctorEducationAddEditRequest request);

//	List<MedicalCouncil> getMedicalCouncils(int page, int size, String updatedTime);

	List<String> addEditSpeciality(DoctorSpecialityAddEditRequest request);

	Boolean addEditAchievement(DoctorAchievementAddEditRequest request);

	Boolean addEditProfessionalStatement(DoctorProfessionalStatementAddEditRequest request);

	Boolean addEditRegistrationDetail(DoctorRegistrationAddEditRequest request);

	Boolean addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request);

	String addEditProfilePicture(DoctorProfilePictureAddEditRequest request);

	String addEditCoverPicture(DoctorProfilePictureAddEditRequest request);

	DoctorProfile getDoctorProfile(String doctorId, String locationId, String hospitalId, Boolean isAdmin);

	List<ProfessionalMembership> getProfessionalMemberships(int page, int size, String updatedTime);

	Boolean addEditProfessionalMembership(DoctorProfessionalAddEditRequest request);

	Boolean addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request);

	Boolean addEditVisitingTime(DoctorVisitingTimeAddEditRequest request);

	Boolean addEditConsultationFee(DoctorConsultationFeeAddEditRequest request);

	Boolean addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request);

	Boolean addEditGeneralInfo(DoctorGeneralInfo request);

	List<Speciality> getSpecialities(int page, int size, String updatedTime);

//	List<EducationInstitute> getEducationInstitutes(int page, int size, String updatedTime);

	List<EducationQualification> getEducationQualifications(int page, int size, String updatedTime);

	DoctorMultipleDataAddEditResponse addEditMultipleData(DoctorMultipleDataAddEditRequest request);

	Boolean addEditFacility(DoctorAddEditFacilityRequest request);

	Boolean addEditGender(DoctorGenderAddEditRequest request);

	Boolean addEditDOB(DoctorDOBAddEditRequest request);

	Boolean addEditVerification(String doctorId, Boolean isVerified);

	Boolean uploadVerificationDocuments(String doctorId, FormDataBodyPart file);

	AddEditSEORequest addEditSEO(AddEditSEORequest request);

	public Boolean deleteDoctorProfilePicture(String doctorId);

	public MedicalCouncil addMedicalCouncils(MedicalCouncil request);

	public EducationInstitute addEditEducationInstitutes(EducationInstitute request);

	public EducationQualification addEditEducationQualification(EducationQualification request);

	public Speciality addeditSpecialities(Speciality request);

	public ProfessionalMembership addeditProfessionalMembership(ProfessionalMembership request);

	Boolean updatePackageType(String doctorId, String locationId, String packageType);

	public Boolean updateKiosk(String doctorId, String locationId);

	DoctorServicesAddEditRequest addEditServices(DoctorServicesAddEditRequest request);

	Boolean addEditCity(DoctorAddEditCityRequest request);

	List<Services> getServices(int page, int size, String updatedTime, String searchTerm);

	Services addEditServices(Services request);
	Boolean addEditMRCode(AddEditMRCodeRequest request);

	Boolean updateVaccinationModule(String doctorId, String locationId, Boolean vaccinationModule);

	Boolean addEditFeedbackURL(DoctorMultipleDataAddEditRequest request);

	Boolean addEditNutritionistRequest(AddEditNutritionistRequest request);
	
	//public Boolean updateExperience(String id);
	
	public Boolean updateExperience();
	
	Boolean addEditOnlineWorkingTime(DoctorOnlineWorkingTimeRequest request);
	
	DoctorOnlineGeneralInfo addEditDoctorOnlineInfo(DoctorOnlineGeneralInfo request);
	
	
	
	//String uploadRegistrationDetails(DoctorRegistrationDetails request,MultipartFile file);

	DoctorProfile checkMedicalDocument(String doctorId, Boolean isRegistrationDetailsVerified,
			Boolean isPhotoIdVerified);

	Boolean verifyClinicDocument(String doctorId, Boolean isClinicOwnershipVerified, String locationId);

//	String uploadRegistrationDetails(String doctorId, String locationId, RegistrationType type, MultipartFile file);

	String uploadRegistrationDetails(DoctorRegistrationDetails request);
	
	Boolean editDoctorSlugUrl(DoctorSlugUrlRequest request);

	List<EducationInstitute> getEducationInstitutes(int page, int size, String updatedTime, String searchTerm,Boolean discarded);

	List<MedicalCouncil> getMedicalCouncils(int page, int size, String updatedTime, String searchTerm,Boolean discarded);
	
	MedicalCouncil deleteMedicalCouncil(String MedicalCouncilId,Boolean discarded);
	
	EducationInstitute deleteEducationInstitute(String MedicalCouncilId,Boolean discarded);

	Boolean checkBirthdaySms(String doctorId, String locationId, Boolean isSendBirthdaySMS);
	
	 Boolean updateBirthdaySms( Boolean isSendBirthdaySMS,String doctorId,String locationId);

	
	Boolean addEditOnlineConsultationSlot(DoctorOnlineConsultationAddEditRequest request);

	Boolean addEditDoctorDetails(DoctorDetails request);

	Boolean updateSuperAdmin(Boolean isSuperAdmin, String doctorId, String locationId);

	Boolean updateDoctorTransactionSms(Boolean isTransactionalSms, String doctorId, String locationId);

	Boolean updateClinicImage(DoctorClinicImage request);

	Boolean checkMedicalDocuments();
	Boolean addEditTraining(DoctorTrainingAddEditRequest doctorexpd);

	Boolean updateWelcomePatientSMS(Boolean isWelcomePatientSMS, String doctorId, String locationId);

}
