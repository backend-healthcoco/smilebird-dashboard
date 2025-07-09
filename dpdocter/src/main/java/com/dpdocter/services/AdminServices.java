package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.DataCount;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientRecord;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SearchRequestDetailsResponse;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.User;
import com.dpdocter.request.DentalTreatmentDetailRequest;
import com.dpdocter.request.EmailList;
import com.dpdocter.request.LeadsTypeReasonsRequest;
import com.dpdocter.response.DoctorResponseNew;
import com.dpdocter.response.DentalAdminNameResponse;
import com.dpdocter.response.DentalTreatmentDetailResponse;
import com.dpdocter.response.DentalTreatmentNameResponse;
import com.dpdocter.response.DoctorResponse;
import com.dpdocter.response.DoctorSearchResponse;
import com.dpdocter.response.LeadsTypeReasonsResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;

import common.util.web.Response;

public interface AdminServices {

	List<User> getInactiveUsers(int page, int size);

	Integer countInactiveUsers();

	List<Hospital> getHospitals(int page, int size);

	/*
	 * List<Location> getClinics(int page, int size, String hospitalId, Boolean
	 * isClinic, Boolean isLab, String searchTerm);
	 */

	Resume addResumes(Resume request);

	List<Resume> getResumes(int page, int size, String type, String searchTerm);

	void importDrug();

	void importCity();

	void importDiagnosticTest();

	void importEducationInstitute();

	void importEducationQualification();

	/*
	 * List<DoctorResponse> getDoctors(int page, int size, String locationId, String
	 * state, String searchTerm, Boolean islisted, Boolean isNutritionist);
	 */
	public Integer countDoctors(String locationId, String state, String searchTerm, Boolean islisted,Boolean isNutritionist);

	List<Location> getLabs(int page, int size, String hospitalId);

	Integer countLabs(String hospitalId);

	ContactUs addContactUs(ContactUs request);

	//List<ContactUs> getContactUs(int page, int size);

	List<Speciality> getUniqueSpecialities(String searchTerm, String updatedTime, int page, int size);

	void importProfessionalMembership();

	void importMedicalCouncil();

	Boolean sendLink(SendAppLink request);

	public List<PatientRecord> getPatientRecord(int page, int size, int max, int min, String searchTerm, String type);

	Boolean updateDoctorClinicIsListed(String locationId, String doctorId, Boolean isListed);

	Boolean updateDoctorClinicRankingCount(String locationId, String doctorId, long rankingCount);

	List<User> getAdmin(int size, int page, String searchTerm);

	Integer countAdmin(String searchTerm);

	/*
	 * List<Location> getClinics(int page, int size, String hospitalId, Boolean
	 * isClinic, Boolean isLab, Boolean isParent, String searchTerm, Boolean
	 * islisted);
	 */
	/*
	 * public Integer countClinics(String hospitalId, Boolean isClinic, Boolean
	 * isLab, Boolean isParent, String searchTerm, Boolean islisted);
	 */

	public List<SearchRequestDetailsResponse> getUserSearchRequestdetails(int page, int size, Long fromDate,
			Long toDate, Boolean isdirectRequest);

	public List<SearchRequestToPharmacyResponse> getPharmacyResponse(String uniqueRequestId, String replyType);

	public Boolean blockUser(String userId, Boolean blockForDay, Boolean blockForHour);

	public Boolean updateDoctorData();

	public Boolean updateLocation();

	public Boolean updatePharmacy();

	List<Location> getClinics(int page, int size, String hospitalId, Boolean isClinic, Boolean isLab, Boolean isParent,
			Boolean isDentalWorkLab, Boolean isDentalImagingLab,Boolean isDentalChain,String searchTerm, Boolean islisted,Boolean isActivate,String city,String toDate,String fromDate);

	Integer countClinics(String hospitalId, Boolean isClinic, Boolean isLab, Boolean isParent, Boolean isDentalWorkLab,
			Boolean isDentalImagingLab,Boolean isDentalChain, String searchTerm, Boolean islisted,Boolean isActivate,String city,String toDate,String fromDate);

	Boolean addCertificateTemplates(CertificateTemplate request);

	List<CertificateTemplate> getCertificateTemplates(int page, int size, Boolean discarded, List<String> specialities);

	Boolean discardCertificateTemplates(String templateId, Boolean discarded);

	CertificateTemplate getCertificateTemplateById(String templateId);

	public List<DoctorSearchResponse> searchDoctor(int size, int page, String searchTerm, String speciality,
			String city);

	public DataCount getDoctorCount();

	public DataCount getLabsCount();

	public DataCount getclinicsCount();

	Response<Object> getDoctors(int page, int size, String locationId, String state, String searchTerm,
			Boolean islisted, Boolean isNutritionist, Boolean isAdminNutritionist,Boolean isActive,Boolean isRegistrationDetailsVerified,Boolean isPhotoIdVerified,Boolean isOnlineConsultationAvailable,String city,
			String specialitiesIds,String toDate,String fromDate,Boolean isHealthcocoDoctor);

	List<DoctorResponseNew> getDoctorsList(int page, int size, String locationId, String state, String searchTerm,
			Boolean isListed, Boolean isNutritionist, Boolean isAdminNutritionist, Boolean isActive, String city);

	List<ContactUs> getContactUs(int page, int size, String searchTerm);

	
	Response<Object> getUserAppUsers(int size, int page, String searchTerm, String mobileNumber);

	Boolean updateDentalChainStateOfClinic(String locationId, Boolean isDentalChain);
	
	Boolean updatePatientIsDentalChain(String patientId, Boolean isDentalChainVerified);

	DentalTreatmentDetailResponse addEditDentalTreatmentDetail(DentalTreatmentDetailRequest request);

	Boolean deleteDentalTreatmentDetailById(String id, Boolean isDiscarded);

	List<DentalTreatmentDetailResponse> getDentalTreatmentDetail(int page, int size, String searchTerm);

	List<DentalTreatmentNameResponse> getDentalTreatmentNames(int page, int size, String searchTerm);

	List<DentalAdminNameResponse> getDentalAdminNames(Boolean isBuddy, Boolean isAgent);

	Boolean editLocationSlugUrl(String locationId, String locationSlugUrl);

	LeadsTypeReasonsResponse addEditLeadsTypeReasons(LeadsTypeReasonsRequest request);

	Boolean deleteDentalReasonsById(String reasonId, Boolean isDiscarded);

	Response<Object> getDentalReasonsList(int page, int size, String searchTerm);

	Response<Object> getUserProfile(String userId);

	Boolean updateClinicSlugURL(String locationId,String slugURL);

	Boolean addEditClinicChargeCode(String locationId, String chargeCode);

	EmailList addEmailList(EmailList request);
	
	public Boolean getDailyReportAnalyticstoDoctor();
	
	public Boolean getWeeklyReportAnalyticstoDoctor();
}
