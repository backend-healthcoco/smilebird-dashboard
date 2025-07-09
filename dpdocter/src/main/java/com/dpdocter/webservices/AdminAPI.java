package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Advice;
import com.dpdocter.beans.Blog;
import com.dpdocter.beans.BlogImageUploadReqest;
import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Country;
import com.dpdocter.beans.DataCount;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.ECGDetails;
import com.dpdocter.beans.EarsExamination;
import com.dpdocter.beans.Echo;
import com.dpdocter.beans.ExpenseType;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.GenericCodesAndReaction;
import com.dpdocter.beans.Holter;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.IndirectLarygoscopyExamination;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.LocaleContactUs;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.NeckExamination;
import com.dpdocter.beans.NoseExamination;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.OralCavityAndThroatExamination;
import com.dpdocter.beans.PA;
import com.dpdocter.beans.PS;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PatientRecord;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.PresentingComplaintEars;
import com.dpdocter.beans.PresentingComplaintNose;
import com.dpdocter.beans.PresentingComplaintOralCavity;
import com.dpdocter.beans.PresentingComplaintThroat;
import com.dpdocter.beans.ProcedureNote;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SearchRequestDetailsResponse;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.beans.Suggestion;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.VerifiedDocuments;
import com.dpdocter.beans.XRayDetails;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;
import com.dpdocter.response.DoctorResponseNew;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.LocaleContactStateType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.SuggestionState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.request.EmailList;
import com.dpdocter.request.VerifiedDocumentRequest;
import com.dpdocter.response.BlogResponse;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.DoctorResponse;
import com.dpdocter.response.DoctorSearchResponse;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;
import com.dpdocter.response.TagResponse;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.BlogService;
import com.dpdocter.services.ClinicContactUsService;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.ExpenseTypeService;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.LocaleContactUsService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.SubscriptionDetailServices;
import com.dpdocter.services.SuggestionService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.services.VerifiedDocumentsService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.ADMIN_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "Endpoint for admin")
public class AdminAPI {

	private static Logger logger = LogManager.getLogger(AdminAPI.class.getName());

	@Autowired
	AdminServices adminServices;

	@Autowired
	BlogService blogService;

	@Autowired
	ClinicalNotesService clinicalNotesService;

	@Autowired
	HistoryServices historyServices;

	@Autowired
	private SuggestionService suggestionService;

	@Autowired
	ESMasterService esMasterService;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private DoctorContactUsService doctorContactUsService;

	@Autowired
	private DoctorProfileService doctorProfileService;

	@Autowired
	private ClinicContactUsService clinicContactUsService;

	@Autowired
	private VerifiedDocumentsService verifiedDocumentsService;

	@Autowired
	private SubscriptionDetailServices subscriptionDetailServices;

	@Autowired
	private LocaleContactUsService localeContactUsService;

	@Autowired
	private ExpenseTypeService expenseTypeService;

	@Value(value = "${image.path}")
	private String imagePath;

	TokenEndpoint as;

	@GetMapping(value = PathProxy.AdminUrls.GET_HOSPITALS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_HOSPITALS, notes = PathProxy.AdminUrls.GET_HOSPITALS)
	public Response<Hospital> getHospitals(@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {

		List<Hospital> hospitals = adminServices.getHospitals(page, size);
		Response<Hospital> response = new Response<Hospital>();
		response.setDataList(hospitals);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_CLINICS_AND_LABS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_CLINICS_AND_LABS, notes = PathProxy.AdminUrls.GET_CLINICS_AND_LABS)
	public Response<Object> getClinics(@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "isClinic") Boolean isClinic,
			@RequestParam(required = false, value = "isLab") Boolean isLab,
			@RequestParam(required = false, value = "isParent") Boolean isParent,
			@RequestParam(required = false, value = "isDentalWorksLab") Boolean isDentalWorkLab,
			@RequestParam(required = false, value = "isDentalImagingLab") Boolean isDentalImagingLab,
			@RequestParam(required = false, value = "isDentalChain") Boolean isDentalChain,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "isListed") Boolean isListed,
			@RequestParam(required = false, value = "isActivate") Boolean isActivate,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "fromDate") String fromDate) {

		List<Location> locations = adminServices.getClinics(page, size, hospitalId, isClinic, isLab, isParent,
				isDentalWorkLab, isDentalImagingLab, isDentalChain, searchTerm, isListed, isActivate, city, toDate,
				fromDate);
		Response<Object> response = new Response<Object>();
		response.setCount(adminServices.countClinics(hospitalId, isClinic, isLab, isParent, isDentalWorkLab,
				isDentalImagingLab, isDentalChain, searchTerm, isListed, isActivate, city, toDate, fromDate));
		response.setDataList(locations);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DOCTORS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DOCTORS, notes = PathProxy.AdminUrls.GET_DOCTORS)
	public Response<Object> getDoctors(@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "state") String state,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "isListed") Boolean isListed,
			@RequestParam(required = false, value = "isNutritionist") Boolean isNutritionist,
			@RequestParam(required = false, value = "isAdminNutritionist") Boolean isAdminNutritionist,
			@RequestParam(required = false, value = "isActive") Boolean isActive,
			@RequestParam(required = false, value = "isRegistrationDetailsVerified") Boolean isRegistrationDetailsVerified,
			@RequestParam(required = false, value = "isPhotoIdVerified") Boolean isPhotoIdVerified,
			@RequestParam(required = false, value = "isOnlineConsultationAvailable") Boolean isOnlineConsultationAvailable,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "specialitiesIds") String specialitiesIds,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "isHealthcocoDoctor") Boolean isHealthcocoDoctor) {

//		List<DoctorResponse> doctorResponses = adminServices.getDoctors(page, size, locationId, state, searchTerm,
//				isListed, isNutritionist , isAdminNutritionist,isActive,city);

		Response<Object> response = adminServices.getDoctors(page, size, locationId, state, searchTerm, isListed,
				isNutritionist, isAdminNutritionist, isActive, isRegistrationDetailsVerified, isPhotoIdVerified,
				isOnlineConsultationAvailable, city, specialitiesIds, toDate, fromDate, isHealthcocoDoctor);
//		response.setData(adminServices.countDoctors(locationId, state, searchTerm, isListed, isNutritionist));
//		response.setDataList(doctorResponses);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DOCTORS_LIST)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DOCTORS_LIST, notes = PathProxy.AdminUrls.GET_DOCTORS_LIST)
	public Response<Object> getDoctorsList(@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "state") String state,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "isListed") Boolean isListed,
			@RequestParam(required = false, value = "isNutritionist") Boolean isNutritionist,
			@RequestParam(required = false, value = "isAdminNutritionist") Boolean isAdminNutritionist,
			@RequestParam(required = false, value = "isActive") Boolean isActive,
			@RequestParam(required = false, value = "city") String city) {

		List<DoctorResponseNew> doctorResponses = adminServices.getDoctorsList(page, size, locationId, state,
				searchTerm, isListed, isNutritionist, isAdminNutritionist, isActive, city);
		Response<Object> response = new Response<Object>();
		response.setData(adminServices.countDoctors(locationId, state, searchTerm, isListed, isNutritionist));
		response.setDataList(doctorResponses);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_RESUMES)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_RESUMES, notes = PathProxy.AdminUrls.ADD_RESUMES)
	public Response<Resume> addResumes(@RequestBody Resume request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getName(), request.getMobileNumber())
				|| request.getFile() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Resume resume = adminServices.addResumes(request);
		resume.setPath(getFinalImageURL(resume.getPath()));
		Response<Resume> response = new Response<Resume>();
		response.setData(resume);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_RESUMES)
	@ApiOperation(value = PathProxy.AdminUrls.GET_RESUMES, notes = PathProxy.AdminUrls.GET_RESUMES)
	public Response<Resume> getResumes(@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "type") String type,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {

		List<Resume> resumes = adminServices.getResumes(page, size, type, searchTerm);

		if (resumes != null) {
			for (Resume resume : resumes) {
				if (resume.getPath() != null) {
					resume.setPath(getFinalImageURL(resume.getPath()));
				}
			}
		}

		Response<Resume> response = new Response<Resume>();
		response.setDataList(resumes);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_CONTACT_US)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_CONTACT_US, notes = PathProxy.AdminUrls.ADD_CONTACT_US)
	public Response<ContactUs> addContactUs(@RequestBody ContactUs request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getName(),
				request.getMobileNumber(), request.getMessage())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ContactUs contactUs = adminServices.addContactUs(request);
		Response<ContactUs> response = new Response<ContactUs>();
		response.setData(contactUs);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_ADVICE)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_ADVICE, notes = PathProxy.AdminUrls.ADD_ADVICE)
	public Response<Advice> addAdvice(@RequestBody Advice request) {

		Advice advice = prescriptionServices.addAdvice(request);
		Response<Advice> response = new Response<Advice>();
		response.setData(advice);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_ADVICE)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_ADVICE, notes = PathProxy.AdminUrls.DELETE_ADVICE)
	public Response<Advice> deleteAdvice(@PathVariable("adviceId") String adviceId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {

		Response<Advice> response = new Response<Advice>();
		response.setData(prescriptionServices.deleteAdvice(adviceId, null, null, null, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_CONTACT_US)
	@ApiOperation(value = PathProxy.AdminUrls.GET_CONTACT_US, notes = PathProxy.AdminUrls.GET_CONTACT_US)
	public Response<ContactUs> getContactUs(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {

		List<ContactUs> contactUs = adminServices.getContactUs(page, size, searchTerm);
		if (contactUs != null) {
			List<String> paths = new ArrayList<String>();
			for (ContactUs contact : contactUs) {
				if (contact.getPath() != null) {

					String image = getFinalImageURL(contact.getPath());

					contact.setPath(image);

				}
			}
		}
		Response<ContactUs> response = new Response<ContactUs>();
		response.setDataList(contactUs);
		return response;
	}

	// @Path(value = PathProxy.AdminUrls.GET_PATIENT)
	// @GET
	// public Response<Resume> getResumes(@RequestParam(required = false, value =
	// "page", defaultValue="0") int page,
	// @RequestParam(required = false, value = "size", defaultValue="0") int size,
	// @RequestParam(required = false, value = "type") String
	// type){
	//
	// List<Resume> resumes = adminServices.getResumes(page, size, type);
	// Response<Resume> response = new Response<Resume>();
	// response.setDataList(resumes);
	// return response;
	// }

	@GetMapping(value = PathProxy.AdminUrls.IMPORT_DRUG)
	public Response<Boolean> importDrug() {
		adminServices.importDrug();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.IMPORT_CITY)
	public Response<Boolean> importCity() {
		adminServices.importCity();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.IMPORT_DIAGNOSTIC_TEST)
	public Response<Boolean> importDiagnosticTest() {
		adminServices.importDiagnosticTest();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.IMPORT_EDUCATION_INSTITUTE)
	public Response<Boolean> importEducationInstitute() {
		adminServices.importEducationInstitute();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.IMPORT_EDUCATION_QUALIFICATION)
	public Response<Boolean> importEducationQualification() {
		adminServices.importEducationQualification();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.IMPORT_PROFESSIONAL_MEMBERSHIP)
	public Response<Boolean> importProfessionalMembership() {
		adminServices.importProfessionalMembership();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.IMPORT_MEDICAL_COUNCIL)
	public Response<Boolean> importMedicalCouncil() {
		adminServices.importMedicalCouncil();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY)
	@ApiOperation(value = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY, notes = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY)
	public Response<Speciality> getUniqueSpecialities(
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {

		List<Speciality> searchResonse = adminServices.getUniqueSpecialities(searchTerm, updatedTime, page, size);
		Response<Speciality> response = new Response<Speciality>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_CINICAL_ITEMS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_CINICAL_ITEMS, notes = PathProxy.AdminUrls.GET_CINICAL_ITEMS)
	public Response<Object> getClinicalItems(@PathVariable("type") String type, @PathVariable("range") String range,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "speciality") String speciality) {

		if (DPDoctorUtils.anyStringEmpty(type, range)) {
			logger.warn("Invalid Input. Type or Range Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Type or Range Cannot Be Empty");
		}
		List<?> clinicalItems = clinicalNotesService.getClinicalItems(type, range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, true, searchTerm, speciality);
		if (clinicalItems != null && !clinicalItems.isEmpty()
				&& ClinicalItems.DIAGRAMS.getType().equalsIgnoreCase(type)) {
			for (Object clinicalItem : clinicalItems) {
				((Diagram) clinicalItem).setDiagramUrl(getFinalImageURL(((Diagram) clinicalItem).getDiagramUrl()));
			}
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(clinicalItems);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_COMPLAINT)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_COMPLAINT, notes = PathProxy.AdminUrls.ADD_COMPLAINT)
	public Response<Complaint> addComplaint(@RequestBody Complaint request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getComplaint())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Complaint complaint = clinicalNotesService.addEditComplaint(request);

		transactionalManagementService.addResource(new ObjectId(complaint.getId()), Resource.COMPLAINT, false);
		ESComplaintsDocument esComplaints = new ESComplaintsDocument();
		BeanUtil.map(complaint, esComplaints);
		esClinicalNotesService.addComplaints(esComplaints);

		Response<Complaint> response = new Response<Complaint>();
		response.setData(complaint);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_OBSERVATION)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_OBSERVATION, notes = PathProxy.AdminUrls.ADD_OBSERVATION)
	public Response<Observation> addObservation(@RequestBody Observation request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getObservation())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Observation observation = clinicalNotesService.addEditObservation(request);

		transactionalManagementService.addResource(new ObjectId(observation.getId()), Resource.OBSERVATION, false);
		ESObservationsDocument esObservations = new ESObservationsDocument();
		BeanUtil.map(observation, esObservations);
		esClinicalNotesService.addObservations(esObservations);
		Response<Observation> response = new Response<Observation>();
		response.setData(observation);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_INVESTIGATION)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_INVESTIGATION, notes = PathProxy.AdminUrls.ADD_INVESTIGATION)
	public Response<Investigation> addInvestigation(@RequestBody Investigation request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getInvestigation())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Investigation investigation = clinicalNotesService.addEditInvestigation(request);

		transactionalManagementService.addResource(new ObjectId(investigation.getId()), Resource.INVESTIGATION, false);
		ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
		BeanUtil.map(investigation, esInvestigations);
		esClinicalNotesService.addInvestigations(esInvestigations);

		Response<Investigation> response = new Response<Investigation>();
		response.setData(investigation);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DIAGNOSIS)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DIAGNOSIS, notes = PathProxy.AdminUrls.ADD_DIAGNOSIS)
	public Response<Diagnoses> addDiagnosis(@RequestBody Diagnoses request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDiagnosis())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Diagnoses diagnosis = clinicalNotesService.addEditDiagnosis(request);

		transactionalManagementService.addResource(new ObjectId(diagnosis.getId()), Resource.DIAGNOSIS, false);
		ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
		BeanUtil.map(diagnosis, esDiagnoses);
		esClinicalNotesService.addDiagnoses(esDiagnoses);

		Response<Diagnoses> response = new Response<Diagnoses>();
		response.setData(diagnosis);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_NOTES)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_NOTES, notes = PathProxy.AdminUrls.ADD_NOTES)
	public Response<Notes> addNotes(@RequestBody Notes request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getNote())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Notes notes = clinicalNotesService.addEditNotes(request);

		transactionalManagementService.addResource(new ObjectId(notes.getId()), Resource.NOTES, false);
		ESNotesDocument esNotes = new ESNotesDocument();
		BeanUtil.map(notes, esNotes);
		esClinicalNotesService.addNotes(esNotes);

		Response<Notes> response = new Response<Notes>();
		response.setData(notes);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DIAGRAM)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DIAGRAM, notes = PathProxy.AdminUrls.ADD_DIAGRAM)
	public Response<Diagram> addDiagram(@RequestBody Diagram request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Diagram diagram = clinicalNotesService.addEditDiagram(request);
		transactionalManagementService.addResource(new ObjectId(diagram.getId()), Resource.DIAGRAM, false);
		ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
		BeanUtil.map(diagram, esDiagrams);
		esClinicalNotesService.addDiagrams(esDiagrams);

		if (diagram.getDiagramUrl() != null) {
			diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
		}
		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_PRESENT_COMPLAINT)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_PRESENT_COMPLAINT, notes = PathProxy.AdminUrls.ADD_PRESENT_COMPLAINT)
	public Response<PresentComplaint> addPresentComplaints(@RequestBody PresentComplaint request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPresentComplaint())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentComplaint presentComplaint = clinicalNotesService.addEditPresentComplaint(request);

		transactionalManagementService.addResource(new ObjectId(presentComplaint.getId()), Resource.PRESENT_COMPLAINT,
				false);
		ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
		BeanUtil.map(presentComplaint, esPresentComplaint);
		esClinicalNotesService.addPresentComplaint(esPresentComplaint);
		Response<PresentComplaint> response = new Response<PresentComplaint>();
		response.setData(presentComplaint);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_PRESENT_COMPLAINT_HISTORY)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.AdminUrls.ADD_PRESENT_COMPLAINT_HISTORY)
	public Response<PresentComplaintHistory> addPresentComplaintsHistory(@RequestBody PresentComplaintHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPresentComplaintHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentComplaintHistory presentComplaintHistory = clinicalNotesService.addEditPresentComplaintHistory(request);

		transactionalManagementService.addResource(new ObjectId(presentComplaintHistory.getId()),
				Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
		ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
		BeanUtil.map(presentComplaintHistory, esPresentComplaintHistory);
		esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
		Response<PresentComplaintHistory> response = new Response<PresentComplaintHistory>();
		response.setData(presentComplaintHistory);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_PROVISIONAL_DIAGNOSIS)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_PROVISIONAL_DIAGNOSIS, notes = PathProxy.AdminUrls.ADD_PROVISIONAL_DIAGNOSIS)
	public Response<ProvisionalDiagnosis> addProvisionalDiagnosis(@RequestBody ProvisionalDiagnosis request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getProvisionalDiagnosis())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ProvisionalDiagnosis provisionalDiagnosis = clinicalNotesService.addEditProvisionalDiagnosis(request);

		transactionalManagementService.addResource(new ObjectId(provisionalDiagnosis.getId()),
				Resource.PROVISIONAL_DIAGNOSIS, false);
		ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
		BeanUtil.map(provisionalDiagnosis, esProvisionalDiagnosis);
		esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
		Response<ProvisionalDiagnosis> response = new Response<ProvisionalDiagnosis>();
		response.setData(provisionalDiagnosis);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_SYSTEM_EXAM)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_SYSTEM_EXAM, notes = PathProxy.AdminUrls.ADD_SYSTEM_EXAM)
	public Response<SystemExam> addSystemExam(@RequestBody SystemExam request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getSystemExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		SystemExam systemExam = clinicalNotesService.addEditSystemExam(request);

		transactionalManagementService.addResource(new ObjectId(systemExam.getId()), Resource.SYSTEMIC_EXAMINATION,
				false);
		ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
		BeanUtil.map(systemExam, esSystemExam);
		esClinicalNotesService.addSystemExam(esSystemExam);
		Response<SystemExam> response = new Response<SystemExam>();
		response.setData(systemExam);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_GENERAL_EXAM)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_GENERAL_EXAM, notes = PathProxy.AdminUrls.ADD_GENERAL_EXAM)
	public Response<GeneralExam> addGeneralExam(@RequestBody GeneralExam request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getGeneralExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		GeneralExam generalExam = clinicalNotesService.addEditGeneralExam(request);

		transactionalManagementService.addResource(new ObjectId(generalExam.getId()), Resource.GENERAL_EXAMINATION,
				false);
		ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
		BeanUtil.map(generalExam, esGeneralExam);
		esClinicalNotesService.addGeneralExam(esGeneralExam);
		Response<GeneralExam> response = new Response<GeneralExam>();
		response.setData(generalExam);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_MENSTRUAL_HISTORY)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_MENSTRUAL_HISTORY, notes = PathProxy.AdminUrls.ADD_MENSTRUAL_HISTORY)
	public Response<MenstrualHistory> addMenstrualHistory(@RequestBody MenstrualHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getMenstrualHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		MenstrualHistory menstrualHistory = clinicalNotesService.addEditMenstrualHistory(request);

		transactionalManagementService.addResource(new ObjectId(menstrualHistory.getId()), Resource.MENSTRUAL_HISTORY,
				false);
		ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
		BeanUtil.map(menstrualHistory, esMenstrualHistory);
		esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
		Response<MenstrualHistory> response = new Response<MenstrualHistory>();
		response.setData(menstrualHistory);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_OBSTETRICS_HISTORY)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_OBSTETRICS_HISTORY, notes = PathProxy.AdminUrls.ADD_OBSTETRICS_HISTORY)
	public Response<ObstetricHistory> addObstetricHistory(@RequestBody ObstetricHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getObstetricHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ObstetricHistory obstetricHistory = clinicalNotesService.addEditObstetricHistory(request);

		transactionalManagementService.addResource(new ObjectId(obstetricHistory.getId()), Resource.OBSTETRIC_HISTORY,
				false);
		ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
		BeanUtil.map(obstetricHistory, esObstetricHistory);
		esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
		Response<ObstetricHistory> response = new Response<ObstetricHistory>();
		response.setData(obstetricHistory);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_INDICATION_OF_USG)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_INDICATION_OF_USG, notes = PathProxy.AdminUrls.ADD_INDICATION_OF_USG)
	public Response<IndicationOfUSG> addIndicationOfUSG(@RequestBody IndicationOfUSG request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getIndicationOfUSG())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		IndicationOfUSG indicationOfUSG = clinicalNotesService.addEditIndicationOfUSG(request);

		transactionalManagementService.addResource(new ObjectId(indicationOfUSG.getId()), Resource.INDICATION_OF_USG,
				false);
		ESIndicationOfUSGDocument esIndicationOfUSG = new ESIndicationOfUSGDocument();
		BeanUtil.map(indicationOfUSG, esIndicationOfUSG);
		esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSG);
		Response<IndicationOfUSG> response = new Response<IndicationOfUSG>();
		response.setData(indicationOfUSG);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_PA)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_PA, notes = PathProxy.AdminUrls.ADD_PA)
	public Response<PA> addEditPA(@RequestBody PA request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPa())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PA pa = clinicalNotesService.addEditPA(request);

		transactionalManagementService.addResource(new ObjectId(pa.getId()), Resource.PA, false);
		ESPADocument espa = new ESPADocument();
		BeanUtil.map(pa, espa);
		esClinicalNotesService.addPA(espa);
		Response<PA> response = new Response<PA>();
		response.setData(pa);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_PV)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_PV, notes = PathProxy.AdminUrls.ADD_PV)
	public Response<PV> addEditPV(@RequestBody PV request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPv())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PV pv = clinicalNotesService.addEditPV(request);

		transactionalManagementService.addResource(new ObjectId(pv.getId()), Resource.PV, false);
		ESPVDocument espv = new ESPVDocument();
		BeanUtil.map(pv, espv);
		esClinicalNotesService.addPV(espv);
		Response<PV> response = new Response<PV>();
		response.setData(pv);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_PS)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_PS, notes = PathProxy.AdminUrls.ADD_PS)
	public Response<PS> addEditPS(@RequestBody PS request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPs())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PS ps = clinicalNotesService.addEditPS(request);

		transactionalManagementService.addResource(new ObjectId(ps.getId()), Resource.PS, false);
		ESPSDocument esps = new ESPSDocument();
		BeanUtil.map(ps, esps);
		esClinicalNotesService.addPS(esps);
		Response<PS> response = new Response<PS>();
		response.setData(ps);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_X_RAY_DETAILS)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_X_RAY_DETAILS, notes = PathProxy.AdminUrls.ADD_X_RAY_DETAILS)
	public Response<XRayDetails> addEditXRayDetails(@RequestBody XRayDetails request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getxRayDetails())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		XRayDetails xRayDetails = clinicalNotesService.addEditXRayDetails(request);

		transactionalManagementService.addResource(new ObjectId(xRayDetails.getId()), Resource.XRAY, false);
		ESXRayDetailsDocument esxRayDetails = new ESXRayDetailsDocument();
		BeanUtil.map(xRayDetails, esxRayDetails);
		esClinicalNotesService.addXRayDetails(esxRayDetails);
		Response<XRayDetails> response = new Response<XRayDetails>();
		response.setData(xRayDetails);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_ECG_DETAILS)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_ECG_DETAILS, notes = PathProxy.AdminUrls.ADD_ECG_DETAILS)
	public Response<ECGDetails> addEditECGDetails(@RequestBody ECGDetails request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getEcgDetails())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ECGDetails ecgDetails = clinicalNotesService.addEditECGDetails(request);

		transactionalManagementService.addResource(new ObjectId(ecgDetails.getId()), Resource.ECG, false);
		ESECGDetailsDocument esecgDetails = new ESECGDetailsDocument();
		BeanUtil.map(ecgDetails, esecgDetails);
		esClinicalNotesService.addECGDetails(esecgDetails);
		Response<ECGDetails> response = new Response<ECGDetails>();
		response.setData(ecgDetails);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_ECHO)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_ECHO, notes = PathProxy.AdminUrls.ADD_ECHO)
	public Response<Echo> addEditEcho(@RequestBody Echo request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getEcho())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Echo echo = clinicalNotesService.addEditEcho(request);

		transactionalManagementService.addResource(new ObjectId(echo.getId()), Resource.ECHO, false);
		ESEchoDocument esEcho = new ESEchoDocument();
		BeanUtil.map(echo, esEcho);
		esClinicalNotesService.addEcho(esEcho);
		Response<Echo> response = new Response<Echo>();
		response.setData(echo);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_HOLTER)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_HOLTER, notes = PathProxy.AdminUrls.ADD_HOLTER)
	public Response<Holter> addEditHolter(@RequestBody Holter request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getHolter())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Holter holter = clinicalNotesService.addEditHolter(request);

		transactionalManagementService.addResource(new ObjectId(holter.getId()), Resource.HOLTER, false);
		ESHolterDocument esHolter = new ESHolterDocument();
		BeanUtil.map(holter, esHolter);
		esClinicalNotesService.addHolter(esHolter);
		Response<Holter> response = new Response<Holter>();
		response.setData(holter);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_PROCEDURE_NOTE)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_PROCEDURE_NOTE, notes = PathProxy.AdminUrls.ADD_PROCEDURE_NOTE)
	public Response<ProcedureNote> addEditProcedureNote(@RequestBody ProcedureNote request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getProcedureNote())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureNote procedureNote = clinicalNotesService.addEditProcedureNote(request);
		transactionalManagementService.addResource(new ObjectId(procedureNote.getId()), Resource.PROCEDURE_NOTE, false);
		ESProcedureNoteDocument esProcedureNoteDocument = new ESProcedureNoteDocument();
		BeanUtil.map(procedureNote, esProcedureNoteDocument);
		esClinicalNotesService.addProcedureNote(esProcedureNoteDocument);
		Response<ProcedureNote> response = new Response<ProcedureNote>();
		response.setData(procedureNote);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_PROCEDURE_NOTE)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_PROCEDURE_NOTE, notes = PathProxy.AdminUrls.DELETE_PROCEDURE_NOTE)
	public Response<ProcedureNote> deleteProcedureNote(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {

		ProcedureNote procedureNote = clinicalNotesService.deleteProcedureNote(id, doctorId, locationId, hospitalId,
				discarded);

		if (procedureNote != null) {
			transactionalManagementService.addResource(new ObjectId(procedureNote.getId()), Resource.PROCEDURE_NOTE,
					false);
			ESProcedureNoteDocument esProcedureNoteDocument = new ESProcedureNoteDocument();
			BeanUtil.map(procedureNote, esProcedureNoteDocument);
			esClinicalNotesService.addProcedureNote(esProcedureNoteDocument);
		}
		Response<ProcedureNote> response = new Response<ProcedureNote>();
		response.setData(procedureNote);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_COMPLAINT)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_COMPLAINT, notes = PathProxy.AdminUrls.DELETE_COMPLAINT)
	public Response<Complaint> deleteComplaint(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {

		Complaint complaint = clinicalNotesService.deleteComplaint(id, doctorId, locationId, hospitalId, discarded);

		if (complaint != null) {
			transactionalManagementService.addResource(new ObjectId(complaint.getId()), Resource.COMPLAINT, false);
			ESComplaintsDocument esComplaints = new ESComplaintsDocument();
			BeanUtil.map(complaint, esComplaints);
			esClinicalNotesService.addComplaints(esComplaints);
		}
		Response<Complaint> response = new Response<Complaint>();
		response.setData(complaint);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_OBSERVATION)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_OBSERVATION, notes = PathProxy.AdminUrls.DELETE_OBSERVATION)
	public Response<Observation> deleteObservation(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		Observation observation = clinicalNotesService.deleteObservation(id, doctorId, locationId, hospitalId,
				discarded);
		if (observation != null) {
			transactionalManagementService.addResource(new ObjectId(observation.getId()), Resource.OBSERVATION, false);
			ESObservationsDocument esObservations = new ESObservationsDocument();
			BeanUtil.map(observation, esObservations);
			esClinicalNotesService.addObservations(esObservations);
		}

		Response<Observation> response = new Response<Observation>();
		response.setData(observation);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_INVESTIGATION)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_INVESTIGATION, notes = PathProxy.AdminUrls.DELETE_INVESTIGATION)
	public Response<Investigation> deleteInvestigation(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		Investigation investigation = clinicalNotesService.deleteInvestigation(id, doctorId, locationId, hospitalId,
				discarded);
		if (investigation != null) {
			transactionalManagementService.addResource(new ObjectId(investigation.getId()), Resource.INVESTIGATION,
					false);
			ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
			BeanUtil.map(investigation, esInvestigations);
			esClinicalNotesService.addInvestigations(esInvestigations);
		}

		Response<Investigation> response = new Response<Investigation>();
		response.setData(investigation);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DIAGNOSIS)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DIAGNOSIS, notes = PathProxy.AdminUrls.DELETE_DIAGNOSIS)
	public Response<Diagnoses> deleteDiagnosis(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		Diagnoses diagnoses = clinicalNotesService.deleteDiagnosis(id, doctorId, locationId, hospitalId, discarded);
		if (diagnoses != null) {
			transactionalManagementService.addResource(new ObjectId(diagnoses.getId()), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnoses, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
		}
		Response<Diagnoses> response = new Response<Diagnoses>();
		response.setData(diagnoses);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_NOTE)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_NOTE, notes = PathProxy.AdminUrls.DELETE_NOTE)
	public Response<Notes> deleteNote(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		Notes notes = clinicalNotesService.deleteNotes(id, doctorId, locationId, hospitalId, discarded);
		if (notes != null) {
			transactionalManagementService.addResource(new ObjectId(notes.getId()), Resource.NOTES, false);
			ESNotesDocument esNotes = new ESNotesDocument();
			BeanUtil.map(notes, esNotes);
			esClinicalNotesService.addNotes(esNotes);
		}
		Response<Notes> response = new Response<Notes>();
		response.setData(notes);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DIAGRAM)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DIAGRAM, notes = PathProxy.AdminUrls.DELETE_DIAGRAM)
	public Response<Diagram> deleteDiagram(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		Diagram diagram = clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId, discarded);
		if (diagram != null) {
			transactionalManagementService.addResource(new ObjectId(diagram.getId()), Resource.DIAGRAM, false);
			ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
			BeanUtil.map(diagram, esDiagrams);
			esClinicalNotesService.addDiagrams(esDiagrams);
		}
		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_PROVISIONAL_DIAGNOSIS)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_PROVISIONAL_DIAGNOSIS, notes = PathProxy.AdminUrls.DELETE_PROVISIONAL_DIAGNOSIS)
	public Response<ProvisionalDiagnosis> deleteProvisionalDiagnosis(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ProvisionalDiagnosis provisionalDiagnosis = clinicalNotesService.deleteProvisionalDiagnosis(id, doctorId,
				locationId, hospitalId, discarded);

		if (provisionalDiagnosis != null) {
			transactionalManagementService.addResource(new ObjectId(provisionalDiagnosis.getId()),
					Resource.PROVISIONAL_DIAGNOSIS, false);
			ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
			BeanUtil.map(provisionalDiagnosis, esProvisionalDiagnosis);
			esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
		}
		Response<ProvisionalDiagnosis> response = new Response<ProvisionalDiagnosis>();
		response.setData(provisionalDiagnosis);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_OBSTETRIC_HISTORY)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_OBSTETRIC_HISTORY, notes = PathProxy.AdminUrls.DELETE_OBSTETRIC_HISTORY)
	public Response<ObstetricHistory> deleteObstetricHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ObstetricHistory obstetricHistory = clinicalNotesService.deleteObstetricHistory(id, doctorId, locationId,
				hospitalId, discarded);

		if (obstetricHistory != null) {
			transactionalManagementService.addResource(new ObjectId(obstetricHistory.getId()),
					Resource.OBSTETRIC_HISTORY, false);
			ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
			BeanUtil.map(obstetricHistory, esObstetricHistory);
			esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
		}
		Response<ObstetricHistory> response = new Response<ObstetricHistory>();
		response.setData(obstetricHistory);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_PRESENT_COMPLAINT)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_PRESENT_COMPLAINT, notes = PathProxy.AdminUrls.DELETE_PRESENT_COMPLAINT)
	public Response<PresentComplaint> deletePresentComplaint(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentComplaint presentComplaint = clinicalNotesService.deletePresentComplaint(id, doctorId, locationId,
				hospitalId, discarded);

		if (presentComplaint != null) {
			transactionalManagementService.addResource(new ObjectId(presentComplaint.getId()),
					Resource.PRESENT_COMPLAINT, false);
			ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
			BeanUtil.map(presentComplaint, esPresentComplaint);
			esClinicalNotesService.addPresentComplaint(esPresentComplaint);
		}
		Response<PresentComplaint> response = new Response<PresentComplaint>();
		response.setData(presentComplaint);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_PRESENT_COMPLAINT_HISTORY)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.AdminUrls.DELETE_PRESENT_COMPLAINT_HISTORY)
	public Response<PresentComplaintHistory> deletePresentComplaintHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentComplaintHistory presentComplaintHistory = clinicalNotesService.deletePresentComplaintHistory(id,
				doctorId, locationId, hospitalId, discarded);

		if (presentComplaintHistory != null) {
			transactionalManagementService.addResource(new ObjectId(presentComplaintHistory.getId()),
					Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
			ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
			BeanUtil.map(presentComplaintHistory, esPresentComplaintHistory);
			esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
		}
		Response<PresentComplaintHistory> response = new Response<PresentComplaintHistory>();
		response.setData(presentComplaintHistory);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_GENERAL_EXAM)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_GENERAL_EXAM, notes = PathProxy.AdminUrls.DELETE_GENERAL_EXAM)
	public Response<GeneralExam> deleteGeneralExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		GeneralExam generalExam = clinicalNotesService.deleteGeneralExam(id, doctorId, locationId, hospitalId,
				discarded);

		if (generalExam != null) {
			transactionalManagementService.addResource(new ObjectId(generalExam.getId()), Resource.GENERAL_EXAMINATION,
					false);
			ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
			BeanUtil.map(generalExam, esGeneralExam);
			esClinicalNotesService.addGeneralExam(esGeneralExam);
		}
		Response<GeneralExam> response = new Response<GeneralExam>();
		response.setData(generalExam);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_SYSTEM_EXAM)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_SYSTEM_EXAM, notes = PathProxy.AdminUrls.DELETE_SYSTEM_EXAM)
	public Response<SystemExam> deleteSystemExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		SystemExam systemExam = clinicalNotesService.deleteSystemExam(id, doctorId, locationId, hospitalId, discarded);

		if (systemExam != null) {
			transactionalManagementService.addResource(new ObjectId(systemExam.getId()), Resource.SYSTEMIC_EXAMINATION,
					false);
			ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
			BeanUtil.map(systemExam, esSystemExam);
			esClinicalNotesService.addSystemExam(esSystemExam);
		}
		Response<SystemExam> response = new Response<SystemExam>();
		response.setData(systemExam);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_MENSTRUAL_HISTORY)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_MENSTRUAL_HISTORY, notes = PathProxy.AdminUrls.DELETE_MENSTRUAL_HISTORY)
	public Response<MenstrualHistory> deleteMenstrualHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		MenstrualHistory menstrualHistory = clinicalNotesService.deleteMenstrualHistory(id, doctorId, locationId,
				hospitalId, discarded);

		if (menstrualHistory != null) {
			transactionalManagementService.addResource(new ObjectId(menstrualHistory.getId()),
					Resource.MENSTRUAL_HISTORY, false);
			ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
			BeanUtil.map(menstrualHistory, esMenstrualHistory);
			esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
		}
		Response<MenstrualHistory> response = new Response<MenstrualHistory>();
		response.setData(menstrualHistory);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_INDICATION_OF_USG)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_INDICATION_OF_USG, notes = PathProxy.AdminUrls.DELETE_INDICATION_OF_USG)
	public Response<IndicationOfUSG> deleteIndicationOfUSG(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		IndicationOfUSG indicationOfUSG = clinicalNotesService.deleteIndicationOfUSG(id, doctorId, locationId,
				hospitalId, discarded);

		if (indicationOfUSG != null) {
			transactionalManagementService.addResource(new ObjectId(indicationOfUSG.getId()),
					Resource.INDICATION_OF_USG, false);
			ESIndicationOfUSGDocument esIndicationOfUSG = new ESIndicationOfUSGDocument();
			BeanUtil.map(indicationOfUSG, esIndicationOfUSG);
			esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSG);
		}
		Response<IndicationOfUSG> response = new Response<IndicationOfUSG>();
		response.setData(indicationOfUSG);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_PA)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_PA, notes = PathProxy.AdminUrls.DELETE_PA)
	public Response<PA> deletePA(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PA pa = clinicalNotesService.deletePA(id, doctorId, locationId, hospitalId, discarded);

		if (pa != null) {
			transactionalManagementService.addResource(new ObjectId(pa.getId()), Resource.PA, false);
			ESPADocument espa = new ESPADocument();
			BeanUtil.map(pa, espa);
			esClinicalNotesService.addPA(espa);
		}
		Response<PA> response = new Response<PA>();
		response.setData(pa);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_PV)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_PV, notes = PathProxy.AdminUrls.DELETE_PV)
	public Response<PV> deletePV(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PV pv = clinicalNotesService.deletePV(id, doctorId, locationId, hospitalId, discarded);

		if (pv != null) {
			transactionalManagementService.addResource(new ObjectId(pv.getId()), Resource.PV, false);
			ESPVDocument espv = new ESPVDocument();
			BeanUtil.map(pv, espv);
			esClinicalNotesService.addPV(espv);
		}
		Response<PV> response = new Response<PV>();
		response.setData(pv);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_PS)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_PS, notes = PathProxy.AdminUrls.DELETE_PS)
	public Response<PS> deletePS(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PS ps = clinicalNotesService.deletePS(id, doctorId, locationId, hospitalId, discarded);

		if (ps != null) {
			transactionalManagementService.addResource(new ObjectId(ps.getId()), Resource.PS, false);
			ESPSDocument esps = new ESPSDocument();
			BeanUtil.map(ps, esps);
			esClinicalNotesService.addPS(esps);
		}
		Response<PS> response = new Response<PS>();
		response.setData(ps);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_X_RAY_DETAILS)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_X_RAY_DETAILS, notes = PathProxy.AdminUrls.DELETE_X_RAY_DETAILS)
	public Response<XRayDetails> deleteXRayDetails(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		XRayDetails xRayDetails = clinicalNotesService.deleteXRayDetails(id, doctorId, locationId, hospitalId,
				discarded);

		if (xRayDetails != null) {
			transactionalManagementService.addResource(new ObjectId(xRayDetails.getId()), Resource.XRAY, false);
			ESXRayDetailsDocument esxRayDetails = new ESXRayDetailsDocument();
			BeanUtil.map(xRayDetails, esxRayDetails);
			esClinicalNotesService.addXRayDetails(esxRayDetails);
		}
		Response<XRayDetails> response = new Response<XRayDetails>();
		response.setData(xRayDetails);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_ECG_DETAILS)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_ECG_DETAILS, notes = PathProxy.AdminUrls.DELETE_ECG_DETAILS)
	public Response<ECGDetails> deleteECGDetails(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ECGDetails ecgDetails = clinicalNotesService.deleteECGDetails(id, doctorId, locationId, hospitalId, discarded);

		if (ecgDetails != null) {
			transactionalManagementService.addResource(new ObjectId(ecgDetails.getId()), Resource.XRAY, false);
			ESECGDetailsDocument esecgDetails = new ESECGDetailsDocument();
			BeanUtil.map(ecgDetails, esecgDetails);
			esClinicalNotesService.addECGDetails(esecgDetails);
		}
		Response<ECGDetails> response = new Response<ECGDetails>();
		response.setData(ecgDetails);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_ECHO)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_ECHO, notes = PathProxy.AdminUrls.DELETE_ECHO)
	public Response<Echo> deleteEcho(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Echo echo = clinicalNotesService.deleteEcho(id, doctorId, locationId, hospitalId, discarded);

		if (echo != null) {
			transactionalManagementService.addResource(new ObjectId(echo.getId()), Resource.ECHO, false);
			ESEchoDocument esEcho = new ESEchoDocument();
			BeanUtil.map(echo, esEcho);
			esClinicalNotesService.addEcho(esEcho);
		}
		Response<Echo> response = new Response<Echo>();
		response.setData(echo);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_HOLTER)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_HOLTER, notes = PathProxy.AdminUrls.DELETE_HOLTER)
	public Response<Holter> deleteHolter(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Holter holter = clinicalNotesService.deleteHolter(id, doctorId, locationId, hospitalId, discarded);

		if (holter != null) {
			transactionalManagementService.addResource(new ObjectId(holter.getId()), Resource.HOLTER, false);
			ESHolterDocument esHolter = new ESHolterDocument();
			BeanUtil.map(holter, esHolter);
			esClinicalNotesService.addHolter(esHolter);
		}
		Response<Holter> response = new Response<Holter>();
		response.setData(holter);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DISEASE)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DISEASE, notes = PathProxy.AdminUrls.ADD_DISEASE)
	public Response<DiseaseAddEditResponse> addDiseases(@RequestBody List<DiseaseAddEditRequest> request) {
		if (request == null) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		List<DiseaseAddEditResponse> diseases = historyServices.addDiseases(request);
		for (DiseaseAddEditResponse addEditResponse : diseases) {
			transactionalManagementService.addResource(new ObjectId(addEditResponse.getId()), Resource.DISEASE, false);
			ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
			BeanUtil.map(addEditResponse, esDiseasesDocument);
			esMasterService.addEditDisease(esDiseasesDocument);
		}
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setDataList(diseases);
		return response;
	}

	@PutMapping(value = PathProxy.AdminUrls.EDIT_DISEASE)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_DISEASE, notes = PathProxy.AdminUrls.EDIT_DISEASE)
	public Response<DiseaseAddEditResponse> editDisease(@PathVariable(value = "diseaseId") String diseaseId,
			@RequestBody DiseaseAddEditRequest request) {
		if (request == null) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		request.setId(diseaseId);
		DiseaseAddEditResponse diseases = historyServices.editDiseases(request);
		transactionalManagementService.addResource(new ObjectId(diseases.getId()), Resource.DISEASE, false);
		ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
		BeanUtil.map(diseases, esDiseasesDocument);
		esMasterService.addEditDisease(esDiseasesDocument);
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setData(diseases);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DISEASE)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DISEASE, notes = PathProxy.AdminUrls.DELETE_DISEASE)
	public Response<DiseaseAddEditResponse> deleteDisease(@PathVariable(value = "diseaseId") String diseaseId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(diseaseId)) {
			logger.warn("Disease Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Disease Id Cannot Be Empty");
		}
		DiseaseAddEditResponse diseaseDeleteResponse = historyServices.deleteDisease(diseaseId, doctorId, hospitalId,
				locationId, discarded);
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setData(diseaseDeleteResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DISEASES)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DISEASES, notes = PathProxy.AdminUrls.GET_DISEASES)
	public Response<DiseaseListResponse> getDiseases(@PathVariable("range") String range,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range)) {
			logger.warn("Range Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Range Cannot Be Empty");
		}
		List<DiseaseListResponse> diseaseListResponse = historyServices.getDiseases(range, page, size, doctorId,
				hospitalId, locationId, updatedTime, discarded, true, searchTerm);
		Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
		response.setDataList(diseaseListResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DRUG)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG, notes = PathProxy.AdminUrls.ADD_DRUG)
	public Response<DrugAddEditResponse> addDrug(@RequestBody DrugAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDrugName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugAddEditResponse drugAddEditResponse = prescriptionServices.addDrug(request);

		transactionalManagementService.addResource(new ObjectId(drugAddEditResponse.getId()), Resource.DRUG, false);
		if (drugAddEditResponse != null) {
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugAddEditResponse, esDrugDocument);
			if (drugAddEditResponse.getDrugType() != null) {
				esDrugDocument.setDrugTypeId(drugAddEditResponse.getDrugType().getId());
				esDrugDocument.setDrugType(drugAddEditResponse.getDrugType().getType());
			}
			esPrescriptionService.addDrug(esDrugDocument);
		}

		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.MAKE_CUSTOM_DRUG_GLOBAL)
	@ApiOperation(value = PathProxy.AdminUrls.MAKE_CUSTOM_DRUG_GLOBAL, notes = PathProxy.AdminUrls.MAKE_CUSTOM_DRUG_GLOBAL)
	public Response<DrugAddEditResponse> makeCustomDrugGlobal(@RequestBody DrugAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId()) || request.getDrugCode() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugAddEditResponse drugAddEditResponse = prescriptionServices.makeCustomDrugGlobal(request);

		transactionalManagementService.addResource(new ObjectId(drugAddEditResponse.getId()), Resource.DRUG, false);
		if (drugAddEditResponse != null) {
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugAddEditResponse, esDrugDocument);
			if (drugAddEditResponse.getDrugType() != null) {
				esDrugDocument.setDrugTypeId(drugAddEditResponse.getDrugType().getId());
				esDrugDocument.setDrugType(drugAddEditResponse.getDrugType().getType());
			}
			esPrescriptionService.addDrug(esDrugDocument);
		}

		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@PutMapping(value = PathProxy.AdminUrls.EDIT_DRUG)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG, notes = PathProxy.AdminUrls.EDIT_DRUG)
	public Response<DrugAddEditResponse> editDrug(@PathVariable(value = "drugId") String drugId,
			@RequestBody DrugAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(drugId, request.getDrugName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(drugId);
		DrugAddEditResponse drugAddEditResponse = prescriptionServices.editDrug(request);

		transactionalManagementService.addResource(new ObjectId(drugAddEditResponse.getId()), Resource.DRUG, false);
		if (drugAddEditResponse != null) {
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugAddEditResponse, esDrugDocument);
			if (drugAddEditResponse.getDrugType() != null) {
				esDrugDocument.setDrugTypeId(drugAddEditResponse.getDrugType().getId());
				esDrugDocument.setDrugType(drugAddEditResponse.getDrugType().getType());
			}
			esPrescriptionService.addDrug(esDrugDocument);
		}
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_GLOBAL_DRUG)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_GLOBAL_DRUG, notes = PathProxy.AdminUrls.DELETE_GLOBAL_DRUG)
	public Response<Drug> deleteDrug(@PathVariable(value = "drugId") String drugId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (StringUtils.isEmpty(drugId)) {
			logger.warn("Drug Id, Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id Cannot Be Empty");
		}
		Drug drugDeleteResponse = prescriptionServices.deleteDrug(drugId, discarded);
		transactionalManagementService.addResource(new ObjectId(drugId), Resource.DRUG, false);
		if (drugDeleteResponse != null) {
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugDeleteResponse, esDrugDocument);
			if (drugDeleteResponse.getDrugType() != null) {
				esDrugDocument.setDrugTypeId(drugDeleteResponse.getDrugType().getId());
				esDrugDocument.setDrugType(drugDeleteResponse.getDrugType().getType());
			}
			esPrescriptionService.addDrug(esDrugDocument);
		}
		Response<Drug> response = new Response<Drug>();
		response.setData(drugDeleteResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_LAB_TEST)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_LAB_TEST, notes = PathProxy.AdminUrls.ADD_LAB_TEST)
	public Response<LabTest> addLabTest(@RequestBody LabTest request) {
		if (request == null || request.getTest() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LabTest labTestResponse = prescriptionServices.addLabTest(request);
		transactionalManagementService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestResponse, esLabTestDocument);
		if (labTestResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestResponse.getTest().getId());
		esPrescriptionService.addLabTest(esLabTestDocument);
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestResponse);
		return response;
	}

	@PutMapping(value = PathProxy.AdminUrls.EDIT_LAB_TEST)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_LAB_TEST, notes = PathProxy.AdminUrls.EDIT_LAB_TEST)
	public Response<LabTest> editLabTest(@PathVariable(value = "labTestId") String labTestId,
			@RequestBody LabTest request) {
		if (request == null || request.getTest() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(labTestId);
		LabTest labTestResponse = prescriptionServices.editLabTest(request);
		transactionalManagementService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestResponse, esLabTestDocument);
		if (labTestResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestResponse.getTest().getId());
		esPrescriptionService.addLabTest(esLabTestDocument);
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_GLOBAL_LAB_TEST)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_GLOBAL_LAB_TEST, notes = PathProxy.AdminUrls.DELETE_GLOBAL_LAB_TEST)
	public Response<LabTest> deleteLabTest(@PathVariable(value = "labTestId") String labTestId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (StringUtils.isEmpty(labTestId)) {
			logger.warn("Lab Test Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Lab Test Id Cannot Be Empty");
		}
		LabTest labTestDeleteResponse = prescriptionServices.deleteLabTest(labTestId, discarded);
		transactionalManagementService.addResource(new ObjectId(labTestDeleteResponse.getId()), Resource.LABTEST,
				false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestDeleteResponse, esLabTestDocument);
		if (labTestDeleteResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestDeleteResponse.getTest().getId());
		esPrescriptionService.addLabTest(esLabTestDocument);

		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestDeleteResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DRUG_TYPE)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_TYPE, notes = PathProxy.AdminUrls.ADD_DRUG_TYPE)
	public Response<DrugTypeAddEditResponse> addDrugType(@RequestBody DrugTypeAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getType())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugTypeAddEditResponse drugTypeAddEditResponse = prescriptionServices.addDrugType(request);

		Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
		response.setData(drugTypeAddEditResponse);
		return response;
	}

	@PutMapping(value = PathProxy.AdminUrls.EDIT_DRUG_TYPE)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_TYPE, notes = PathProxy.AdminUrls.EDIT_DRUG_TYPE)
	public Response<DrugTypeAddEditResponse> editDrugType(@PathVariable(value = "drugTypeId") String drugTypeId,
			@RequestBody DrugTypeAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getType())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(drugTypeId);
		DrugTypeAddEditResponse drugTypeAddEditResponse = prescriptionServices.editDrugType(request);

		transactionalManagementService.addResource(new ObjectId(drugTypeId), Resource.DRUGSDRUGTYPE, false);
		if (drugTypeAddEditResponse != null) {
			esPrescriptionService.editDrugTypeInDrugs(drugTypeId);
		}
		Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
		response.setData(drugTypeAddEditResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DRUG_TYPE)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_TYPE, notes = PathProxy.AdminUrls.DELETE_DRUG_TYPE)
	public Response<DrugTypeAddEditResponse> deleteDrugType(@PathVariable(value = "drugTypeId") String drugTypeId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (StringUtils.isEmpty(drugTypeId)) {
			logger.warn("Drug Type Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Drug Type Id Cannot Be Empty");
		}
		DrugTypeAddEditResponse drugTypeDeleteResponse = prescriptionServices.deleteDrugType(drugTypeId, discarded);

		Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
		response.setData(drugTypeDeleteResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DRUG_DOSAGE)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_DOSAGE, notes = PathProxy.AdminUrls.ADD_DRUG_DOSAGE)
	public Response<DrugDosageAddEditResponse> addDrugDosage(@RequestBody DrugDosageAddEditRequest request) {
		if (request == null || request.getDosage() == null || DPDoctorUtils.anyStringEmpty(request.getDosage())) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.addDrugDosage(request);

		Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
		response.setData(drugDosageAddEditResponse);
		return response;
	}

	@PutMapping(value = PathProxy.AdminUrls.EDIT_DRUG_DOSAGE)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_DOSAGE, notes = PathProxy.AdminUrls.EDIT_DRUG_DOSAGE)
	public Response<DrugDosageAddEditResponse> editDrugDosage(@PathVariable(value = "drugDosageId") String drugDosageId,
			@RequestBody DrugDosageAddEditRequest request) {
		if (request == null || request.getDosage() == null
				|| DPDoctorUtils.anyStringEmpty(drugDosageId, request.getDosage())) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		request.setId(drugDosageId);
		DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.editDrugDosage(request);

		Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
		response.setData(drugDosageAddEditResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DRUG_DOSAGE)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_DOSAGE, notes = PathProxy.AdminUrls.DELETE_DRUG_DOSAGE)
	public Response<DrugDosageAddEditResponse> deleteDrugDosage(
			@PathVariable(value = "drugDosageId") String drugDosageId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(drugDosageId)) {
			logger.warn("Drug Dosage Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Drug Dosage Id Cannot Be Empty");
		}
		DrugDosageAddEditResponse drugDosageDeleteResponse = prescriptionServices.deleteDrugDosage(drugDosageId,
				discarded);

		Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
		response.setData(drugDosageDeleteResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DRUG_DIRECTION)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_DIRECTION, notes = PathProxy.AdminUrls.ADD_DRUG_DIRECTION)
	public Response<DrugDirectionAddEditResponse> addDrugDirection(@RequestBody DrugDirectionAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDirection())) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.addDrugDirection(request);

		Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
		response.setData(drugDirectionAddEditResponse);
		return response;
	}

	@PutMapping(value = PathProxy.AdminUrls.EDIT_DRUG_DIRECTION)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_DIRECTION, notes = PathProxy.AdminUrls.EDIT_DRUG_DIRECTION)
	public Response<DrugDirectionAddEditResponse> editDrugDirection(
			@PathVariable(value = "drugDirectionId") String drugDirectionId,
			@RequestBody DrugDirectionAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(drugDirectionId, request.getDirection())) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		request.setId(drugDirectionId);
		DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.editDrugDirection(request);

		Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
		response.setData(drugDirectionAddEditResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DRUG_DIRECTION)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_DIRECTION, notes = PathProxy.AdminUrls.DELETE_DRUG_DIRECTION)
	public Response<DrugDirectionAddEditResponse> deleteDrugDirection(
			@PathVariable(value = "drugDirectionId") String drugDirectionId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(drugDirectionId)) {
			logger.warn("Drug Direction Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Drug Direction Id Cannot Be Empty");
		}
		DrugDirectionAddEditResponse drugDirectionDeleteResponse = prescriptionServices
				.deleteDrugDirection(drugDirectionId, discarded);

		Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
		response.setData(drugDirectionDeleteResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_DRUG_DURATION_UNIT)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_DURATION_UNIT, notes = PathProxy.AdminUrls.ADD_DRUG_DURATION_UNIT)
	public Response<DrugDurationUnitAddEditResponse> addDrugDurationUnit(
			@RequestBody DrugDurationUnitAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getUnit())) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices
				.addDrugDurationUnit(request);

		Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
		response.setData(drugDurationUnitAddEditResponse);
		return response;
	}

	@PutMapping(value = PathProxy.AdminUrls.EDIT_DRUG_DURATION_UNIT)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_DURATION_UNIT, notes = PathProxy.AdminUrls.EDIT_DRUG_DURATION_UNIT)
	public Response<DrugDurationUnitAddEditResponse> editDrugDurationUnit(
			@PathVariable(value = "drugDurationUnitId") String drugDurationUnitId,
			@RequestBody DrugDurationUnitAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(drugDurationUnitId, request.getUnit())) {
			logger.warn("Request Sent Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		request.setId(drugDurationUnitId);
		DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices
				.editDrugDurationUnit(request);

		Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
		response.setData(drugDurationUnitAddEditResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DRUG_DURATION_UNIT)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_DURATION_UNIT, notes = PathProxy.AdminUrls.DELETE_DRUG_DURATION_UNIT)
	public Response<DrugDurationUnitAddEditResponse> deleteDrugDurationUnit(
			@PathVariable(value = "drugDurationUnitId") String drugDurationUnitId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (StringUtils.isEmpty(drugDurationUnitId)) {
			logger.warn("Drug Duration Unit Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Drug Duration Unit Id Cannot Be Empty");
		}
		DrugDurationUnitAddEditResponse drugDurationUnitDeleteResponse = prescriptionServices
				.deleteDrugDurationUnit(drugDurationUnitId, discarded);

		Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
		response.setData(drugDurationUnitDeleteResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_EDIT_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_EDIT_DIAGNOSTIC_TEST, notes = PathProxy.AdminUrls.ADD_EDIT_DIAGNOSTIC_TEST)
	public Response<DiagnosticTest> addEditDiagnosticTest(@RequestBody DiagnosticTest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getTestName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DiagnosticTest diagnosticTest = prescriptionServices.addEditDiagnosticTest(request);
		transactionalManagementService.addResource(new ObjectId(diagnosticTest.getId()), Resource.DIAGNOSTICTEST,
				false);

		ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
		BeanUtil.map(diagnosticTest, esDiagnosticTestDocument);
		esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
		Response<DiagnosticTest> response = new Response<DiagnosticTest>();
		response.setData(diagnosticTest);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_GLOBAL_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_GLOBAL_DIAGNOSTIC_TEST, notes = PathProxy.AdminUrls.DELETE_GLOBAL_DIAGNOSTIC_TEST)
	public Response<DiagnosticTest> deleteDiagnosticTest(
			@PathVariable(value = "diagnosticTestId") String diagnosticTestId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(diagnosticTestId)) {
			logger.warn("Diagnostic Test Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Diagnostic Test Id Cannot Be Empty");
		}
		DiagnosticTest testDeleteResponse = prescriptionServices.deleteDiagnosticTest(diagnosticTestId, discarded);
		ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
		BeanUtil.map(testDeleteResponse, esDiagnosticTestDocument);
		esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);

		Response<DiagnosticTest> response = new Response<DiagnosticTest>();
		response.setData(testDeleteResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_PRESCRIPTION_ITEMS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_PRESCRIPTION_ITEMS, notes = PathProxy.AdminUrls.GET_PRESCRIPTION_ITEMS)
	public Response<Object> getPrescriptionItems(@PathVariable("type") String type, @PathVariable("range") String range,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "category") String category,
			@RequestParam(required = false, value = "disease") String disease,
			@RequestParam(required = false, value = "speciality") String speciality) {

		if (DPDoctorUtils.anyStringEmpty(type, range)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<?> prescriptionList = prescriptionServices.getPrescriptionItems(type, range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, true, searchTerm, category, disease, speciality);

		Integer prescriptionCount = prescriptionServices.getPrescriptionItemsCount(type, range, doctorId, locationId,
				hospitalId, updatedTime, discarded, true, searchTerm, category, disease, speciality);

		Response<Object> response = new Response<Object>();
		response.setDataList(prescriptionList);
		response.setCount(prescriptionCount);

		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CONTACT_STATE)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CONTACT_STATE, notes = PathProxy.AdminUrls.UPDATE_DOCTOR_CONTACT_STATE)
	public Response<DoctorContactUs> updateDoctorContactList(@PathVariable(value = "contactId") String contactId,
			@PathVariable(value = "contactState") DoctorContactStateType contactState,
			@RequestParam(required = false, value = "contactLaterOnDate", defaultValue = "0") long contactLaterOnDate) {
		DoctorContactUs doctorContactUs = doctorContactUsService.updateDoctorContactState(contactId, contactState,
				contactLaterOnDate);
		Response<DoctorContactUs> response = new Response<DoctorContactUs>();
		response.setData(doctorContactUs);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_LOCALE_CONTACT_STATE)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_LOCALE_CONTACT_STATE, notes = PathProxy.AdminUrls.UPDATE_LOCALE_CONTACT_STATE)
	public Response<LocaleContactUs> updateLocaleContactList(@PathVariable(value = "contactId") String contactId,
			@PathVariable(value = "contactState") LocaleContactStateType contactState) {
		LocaleContactUs localeContactUs = localeContactUsService.updateLocaleContactState(contactId, contactState);
		Response<LocaleContactUs> response = new Response<LocaleContactUs>();
		response.setData(localeContactUs);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DOCTOR_PROFILE)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DOCTOR_PROFILE, notes = PathProxy.AdminUrls.GET_DOCTOR_PROFILE)
	public Response<DoctorProfile> getDoctorProfile(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(doctorId, locationId, hospitalId, true);
		if (doctorProfile != null) {
			if (doctorProfile.getImageUrl() != null) {
				doctorProfile.setImageUrl(getFinalImageURL(doctorProfile.getImageUrl()));
			}
			if (doctorProfile.getThumbnailUrl() != null) {
				doctorProfile.setThumbnailUrl(getFinalImageURL(doctorProfile.getThumbnailUrl()));
			}
			if (doctorProfile.getCoverImageUrl() != null) {
				doctorProfile.setCoverImageUrl(getFinalImageURL(doctorProfile.getCoverImageUrl()));
			}
			if (doctorProfile.getCoverThumbnailImageUrl() != null) {
				doctorProfile.setCoverThumbnailImageUrl(getFinalImageURL(doctorProfile.getCoverThumbnailImageUrl()));
			}
			if (doctorProfile.getClinicProfile() != null & !doctorProfile.getClinicProfile().isEmpty()) {
				for (DoctorClinicProfile clinicProfile : doctorProfile.getClinicProfile()) {
					if (clinicProfile.getImages() != null) {
						for (ClinicImage clinicImage : clinicProfile.getImages()) {
							if (clinicImage.getImageUrl() != null)
								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							if (clinicImage.getThumbnailUrl() != null)
								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
						}
					}
				}
			}
		}
		Response<DoctorProfile> response = new Response<DoctorProfile>();
		response.setData(doctorProfile);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_EDIT_GENERIC_CODE)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_EDIT_GENERIC_CODE, notes = PathProxy.AdminUrls.ADD_EDIT_GENERIC_CODE)
	public Response<GenericCode> addEditGenericCode(@RequestBody GenericCode request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getCode(), request.getName())) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		GenericCode genericCode = prescriptionServices.addEditGenericCode(request);

		Response<GenericCode> response = new Response<GenericCode>();
		response.setData(genericCode);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.SEND_APP_LINK)
	@ApiOperation(value = PathProxy.AdminUrls.SEND_APP_LINK, notes = PathProxy.AdminUrls.SEND_APP_LINK)
	public Response<Boolean> sendLink(@RequestBody SendAppLink request) {
		if (request == null || request.getAppType() == null || (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())
				&& DPDoctorUtils.anyStringEmpty(request.getMobileNumber()))) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Boolean sendLinkresponse = adminServices.sendLink(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(sendLinkresponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_COUNT_PATIENT_RECORD)
	@ApiOperation(value = PathProxy.AdminUrls.GET_COUNT_PATIENT_RECORD, notes = PathProxy.AdminUrls.GET_COUNT_PATIENT_RECORD)
	public Response<Object> getPatientRecord(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "max", defaultValue = "0") int max,
			@RequestParam(required = false, value = "min", defaultValue = "0") int min,
			@RequestParam(required = false, value = "type") String type) {
		List<PatientRecord> patientRecordList = adminServices.getPatientRecord(page, size, max, min, searchTerm, type);
		Response<Object> response = new Response<Object>();
		response.setDataList(patientRecordList);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_BLOG)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_BLOG, notes = PathProxy.AdminUrls.ADD_BLOG)
	public Response<Blog> addBlog(@RequestBody Blog request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Blog blogresponse = blogService.saveBlog(request);

		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);

		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_BLOG)
	@ApiOperation(value = PathProxy.AdminUrls.GET_BLOG, notes = PathProxy.AdminUrls.GET_BLOG)
	public Response<BlogResponse> getBlogs(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "userId") String userId,
			@RequestParam(required = false, value = "category") String category,
			@RequestParam(required = false, value = "title") String title,
			@RequestParam(required = false, value = "isSmilebirdBlog", defaultValue = "false") Boolean isSmilebirdBlog,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		BlogResponse blogresponse = blogService.getBlogs(size, page, category, userId, title, discarded,
				isSmilebirdBlog);
		Response<BlogResponse> response = new Response<BlogResponse>();
		response.setData(blogresponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_BLOG_BY_ID)
	@ApiOperation(value = PathProxy.AdminUrls.GET_BLOG_BY_ID, notes = PathProxy.AdminUrls.GET_BLOG_BY_ID)
	public Response<Blog> getBlog(@PathVariable("blogId") String blogId) {
		Blog blogresponse = blogService.getBlog(blogId, null);
		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_BLOG_BY_SLUG_URL)
	@ApiOperation(value = PathProxy.AdminUrls.GET_BLOG_BY_SLUG_URL, notes = PathProxy.AdminUrls.GET_BLOG_BY_SLUG_URL)
	public Response<Blog> getBlogByTitle(@PathVariable("slugURL") String slugURL) {
		Blog blogresponse = blogService.getBlog(null, slugURL);
		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_BLOG)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_BLOG, notes = PathProxy.AdminUrls.DELETE_BLOG)
	public Response<Blog> deleteBlog(@PathVariable("blogId") String blogId, @PathVariable("userId") String userId) {

		Blog blogResponse = blogService.deleteBlog(blogId, userId);
		Response<Blog> response = new Response<Blog>();
		response.setData(blogResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.UPLOAD_BLOG_IMAGES)
	@ApiOperation(value = PathProxy.AdminUrls.UPLOAD_BLOG_IMAGES, notes = PathProxy.AdminUrls.UPLOAD_BLOG_IMAGES)
	public Response<Object> uploadBlogImages(@RequestBody BlogImageUploadReqest request) {

		List<String> urlresponse = null;
		if (request.getImages() != null) {

			urlresponse = blogService.uploadBlogImages(request);
		}

		Response<Object> response = new Response<Object>();
		response.setDataList(urlresponse);

		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_BLOG_SUPER_CATEGORY)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_BLOG_SUPER_CATEGORY, notes = PathProxy.AdminUrls.UPDATE_BLOG_SUPER_CATEGORY)
	public Response<Boolean> updateBlogSuperCategory(@PathVariable("blogId") String blogId,
			@PathVariable("userId") String userId, @PathVariable("category") String category) {
		if (DPDoctorUtils.anyStringEmpty(blogId, userId, category)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean blogResponse = blogService.updateBlogSuperCategory(blogId, userId, category);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(blogResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_BLOG_IMAGES)
	@ApiOperation(value = PathProxy.AdminUrls.GET_BLOG_IMAGES, notes = PathProxy.AdminUrls.GET_BLOG_IMAGES)
	public Response<String> getBlogImages(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "category") String category,
			@RequestParam(required = false, value = "userId") String userId) {
		List<String> blogresponse = blogService.getBlogImagesUrl(size, page, category, userId);

		Response<String> response = new Response<String>();
		response.setDataList(blogresponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CLINIC_ISLISTED)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CLINIC_ISLISTED, notes = PathProxy.AdminUrls.UPDATE_DOCTOR_CLINIC_ISLISTED)
	public Response<Boolean> updateDoctorClinicIsListed(@PathVariable("locationId") String locationId,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "isListed", defaultValue = "true") Boolean isListed) {
		Boolean status = adminServices.updateDoctorClinicIsListed(locationId, doctorId, isListed);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_CLINIC_SLUG_URL)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_CLINIC_SLUG_URL, notes = PathProxy.AdminUrls.UPDATE_CLINIC_SLUG_URL)
	public Response<Boolean> updateClinicSlugURL(@PathVariable("locationId") String locationId,
			@PathVariable("slugURL") String slugURL) {
		Boolean status = adminServices.updateClinicSlugURL(locationId, slugURL);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CLINIC_RANKING_COUNT)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CLINIC_RANKING_COUNT, notes = PathProxy.AdminUrls.UPDATE_DOCTOR_CLINIC_RANKING_COUNT)
	public Response<Boolean> updateDoctorClinicRankingCount(
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "rankingCount", defaultValue = "0") long rankingCount) {
		Boolean count = adminServices.updateDoctorClinicRankingCount(locationId, doctorId, rankingCount);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(count);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_SUGGESTION)
	@ApiOperation(value = PathProxy.AdminUrls.GET_SUGGESTION, notes = PathProxy.AdminUrls.GET_SUGGESTION)
	public Response<?> getSuggestion(@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "userId") String userId,
			@RequestParam(required = false, value = "suggetionType") String suggetionType,
			@RequestParam(required = false, value = "state") String state,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {

		List<Suggestion> suggestions = suggestionService.getSuggestion(page, size, userId, suggetionType, state,
				searchTerm);
		Response<String> response = new Response<String>();
		response.setDataList(suggestions);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_SUGGESTION_STATE)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_SUGGESTION_STATE, notes = PathProxy.AdminUrls.UPDATE_SUGGESTION_STATE)
	public Response<Suggestion> addSuggestion(@PathVariable("suggestionId") String suggestionId,
			@RequestParam(required = false, value = "state") SuggestionState state) {

		Suggestion suggestion = suggestionService.updateSuggestionState(suggestionId, state);

		Response<Suggestion> response = new Response<Suggestion>();
		response.setData(suggestion);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_GENERIC_CODE_WITH_REACTION)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_GENERIC_CODE_WITH_REACTION, notes = PathProxy.AdminUrls.ADD_GENERIC_CODE_WITH_REACTION)
	public Response<Boolean> addGenericCodeWithReaction(@RequestBody GenericCodesAndReaction request) {

		Boolean codeWithReaction = prescriptionServices.addGenericCodeWithReaction(request);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(codeWithReaction);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_GENERIC_CODE_WITH_REACTION)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_GENERIC_CODE_WITH_REACTION, notes = PathProxy.AdminUrls.DELETE_GENERIC_CODE_WITH_REACTION)
	public Response<Boolean> deleteGenericCodeWithReaction(@RequestBody GenericCodesAndReaction request) {

		Boolean codeWithReaction = prescriptionServices.deleteGenericCodeWithReaction(request);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(codeWithReaction);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.UPLOAD_GENERIC_CODE_WITH_REACTION, consumes = {
			MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.AdminUrls.UPLOAD_GENERIC_CODE_WITH_REACTION, notes = PathProxy.AdminUrls.UPLOAD_GENERIC_CODE_WITH_REACTION)
	public Response<Boolean> uploadGenericCodeWithReaction(@FormDataParam("file") FormDataBodyPart file) {

		Boolean codeWithReaction = prescriptionServices.uploadGenericCodeWithReaction(file);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(codeWithReaction);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_CLINIC_CONTACT_STATE)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_CLINIC_CONTACT_STATE, notes = PathProxy.AdminUrls.UPDATE_CLINIC_CONTACT_STATE)
	public Response<ClinicContactUs> updateClinicContactList(@PathVariable(value = "contactId") String contactId,
			@PathVariable(value = "contactState") DoctorContactStateType contactState,
			@RequestParam(required = false, value = "contactLaterOnDate", defaultValue = "0") long contactLaterOnDate) {
		ClinicContactUs clinicContactUs = clinicContactUsService.updateClinicContactState(contactId, contactState,
				contactLaterOnDate);
		Response<ClinicContactUs> response = new Response<ClinicContactUs>();
		response.setData(clinicContactUs);

		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.UPLOAD_DOCUMENTS_FOR_VERIFICATIONS, consumes = {
			MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.AdminUrls.UPLOAD_DOCUMENTS_FOR_VERIFICATIONS, notes = PathProxy.AdminUrls.UPLOAD_DOCUMENTS_FOR_VERIFICATIONS)
	public Response<VerifiedDocuments> addDocumentsMultipart(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("data") FormDataBodyPart data) {
		data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		VerifiedDocumentRequest request = data.getValueAs(VerifiedDocumentRequest.class);

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		VerifiedDocuments documents = verifiedDocumentsService.addDocumentsMultipart(file, request);

		if (documents != null) {
			documents.setDocumentsUrl(getFinalImageURL(documents.getDocumentsUrl()));

		}
		Response<VerifiedDocuments> response = new Response<VerifiedDocuments>();
		response.setData(documents);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.VERIFY_DOCUMENTS)
	@ApiOperation(value = PathProxy.AdminUrls.VERIFY_DOCUMENTS, notes = PathProxy.AdminUrls.VERIFY_DOCUMENTS)
	public Response<Boolean> updateVerified(@PathVariable(value = "documentId") String documentId,
			@RequestParam(required = false, value = "isVerified") Boolean isVerified) {

		Response<Boolean> response = new Response<Boolean>();
		Boolean result = verifiedDocumentsService.updateVerified(documentId, isVerified);
		response.setData(result);

		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_BLOGS_CATEGORY)
	@ApiOperation(value = PathProxy.AdminUrls.GET_BLOGS_CATEGORY, notes = PathProxy.AdminUrls.GET_BLOGS_CATEGORY)
	public Response<Object> getBlogCategory() {

		Response<Object> response = new Response<Object>();
		response.setData(BlogCategoryType.values());
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL)
	@ApiOperation(value = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL, notes = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL)
	public Response<Object> addSubscriptionDetail() {
		List<SubscriptionDetail> reponseSubscriptionDetail = subscriptionDetailServices.addsubscriptionData();
		Response<Object> response = new Response<Object>();
		response.setDataList(reponseSubscriptionDetail);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_ADMIN)
	@ApiOperation(value = PathProxy.AdminUrls.GET_ADMIN, notes = PathProxy.AdminUrls.GET_ADMIN)
	public Response<Object> getAdmin(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Response<Object> response = new Response<Object>();
		response.setData(adminServices.countAdmin(searchTerm));
		response.setDataList(adminServices.getAdmin(size, page, searchTerm));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.MAIL_CUSTOM_DRUGS)
	@ApiOperation(value = PathProxy.AdminUrls.MAIL_CUSTOM_DRUGS, notes = PathProxy.AdminUrls.MAIL_CUSTOM_DRUGS)
	public Response<Boolean> mailDrugs() {
		Boolean mailDrugsResponse = transactionalManagementService.mailDrugs();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(mailDrugsResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.ADD_CSV_DATA)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_CSV_DATA, notes = PathProxy.AdminUrls.ADD_CSV_DATA)
	public Response<Integer> addCSVData(@RequestParam(required = false, value = "filePath") String filePath) {

		Response<Integer> response = new Response<Integer>();
		response.setData(prescriptionServices.addCSVData(filePath));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_PHARMACY_REQUEST_DETAILS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_PHARMACY_REQUEST_DETAILS, notes = PathProxy.AdminUrls.GET_PHARMACY_REQUEST_DETAILS)
	public Response<SearchRequestDetailsResponse> getPharamcyRequestDetails(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "fromDate", defaultValue = "0") Long fromDate,
			@RequestParam(required = false, value = "toDate", defaultValue = "0") Long toDate,
			@RequestParam(required = false, value = "isdirectRequest", defaultValue = "true") Boolean isdirectRequest) {

		Response<SearchRequestDetailsResponse> response = new Response<SearchRequestDetailsResponse>();
		response.setDataList(adminServices.getUserSearchRequestdetails(page, size, fromDate, toDate, isdirectRequest));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_PHARMACY_RESPONSE)
	@ApiOperation(value = PathProxy.AdminUrls.GET_PHARMACY_RESPONSE, notes = PathProxy.AdminUrls.GET_PHARMACY_RESPONSE)
	public Response<SearchRequestToPharmacyResponse> getPharmacyResponse(
			@PathVariable("uniqueRequestId") String uniqueRequestId,
			@RequestParam(required = false, value = "replyType") String replyType) {
		Response<SearchRequestToPharmacyResponse> response = new Response<SearchRequestToPharmacyResponse>();
		response.setDataList(adminServices.getPharmacyResponse(uniqueRequestId, replyType));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.BLOCK_USER)
	@ApiOperation(value = PathProxy.AdminUrls.BLOCK_USER, notes = PathProxy.AdminUrls.BLOCK_USER)
	public Response<Boolean> blockUser(@PathVariable("userId") String userId,
			@RequestParam(required = false, value = "blockForDay", defaultValue = "true") Boolean blockForDay,
			@RequestParam(required = false, value = "blockForHour", defaultValue = "false") Boolean blockForHour) {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.blockUser(userId, blockForDay, blockForHour));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_USER)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_USER, notes = PathProxy.AdminUrls.UPDATE_USER)
	public Response<Boolean> updateUser() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.updateDoctorData());
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_LOCATION)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_LOCATION, notes = PathProxy.AdminUrls.UPDATE_LOCATION)
	public Response<Boolean> updateLocation() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.updateLocation());
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_PHARMACY)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_PHARMACY, notes = PathProxy.AdminUrls.UPDATE_PHARMACY)
	public Response<Boolean> updatePharmacy() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.updatePharmacy());
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_NOSE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_NOSE, notes = PathProxy.ClinicalNotesUrls.ADD_PC_NOSE)
	public Response<PresentingComplaintNose> addEditPCNose(@RequestBody PresentingComplaintNose request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcNose())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintNose presentingComplaintNose = clinicalNotesService.addEditPCNose(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_NOSE, false);
		ESPresentingComplaintNoseDocument esPresentingComplaintNose = new ESPresentingComplaintNoseDocument();
		BeanUtil.map(presentingComplaintNose, esPresentingComplaintNose);
		esClinicalNotesService.addPCNose(esPresentingComplaintNose);
		Response<PresentingComplaintNose> response = new Response<PresentingComplaintNose>();
		response.setData(presentingComplaintNose);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_EARS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_EARS, notes = PathProxy.ClinicalNotesUrls.ADD_PC_EARS)
	public Response<PresentingComplaintEars> addEditPCEars(@RequestBody PresentingComplaintEars request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcEars())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintEars presentingComplaintEars = clinicalNotesService.addEditPCEars(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_EARS, false);
		ESPresentingComplaintEarsDocument esPresentingComplaintEars = new ESPresentingComplaintEarsDocument();
		BeanUtil.map(presentingComplaintEars, esPresentingComplaintEars);
		esClinicalNotesService.addPCEars(esPresentingComplaintEars);
		Response<PresentingComplaintEars> response = new Response<PresentingComplaintEars>();
		response.setData(presentingComplaintEars);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_THROAT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_THROAT, notes = PathProxy.ClinicalNotesUrls.ADD_PC_THROAT)
	public Response<PresentingComplaintThroat> addEditPCThroat(@RequestBody PresentingComplaintThroat request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcThroat())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintThroat presentingComplaintThroat = clinicalNotesService.addEditPCThroat(request);
		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_THROAT, false);
		ESPresentingComplaintThroatDocument esPresentingComplaintThroat = new ESPresentingComplaintThroatDocument();
		BeanUtil.map(presentingComplaintThroat, esPresentingComplaintThroat);
		esClinicalNotesService.addPCThroat(esPresentingComplaintThroat);
		Response<PresentingComplaintThroat> response = new Response<PresentingComplaintThroat>();
		response.setData(presentingComplaintThroat);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_ORAL_CAVITY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_ORAL_CAVITY, notes = PathProxy.ClinicalNotesUrls.ADD_PC_ORAL_CAVITY)
	public Response<PresentingComplaintOralCavity> addEditPCOralCavity(
			@RequestBody PresentingComplaintOralCavity request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcOralCavity())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintOralCavity presentingComplaintOralCavity = clinicalNotesService.addEditPCOralCavity(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_ORAL_CAVITY, false);
		ESPresentingComplaintOralCavityDocument esPresentingComplaintOralCavity = new ESPresentingComplaintOralCavityDocument();
		BeanUtil.map(presentingComplaintOralCavity, esPresentingComplaintOralCavity);
		esClinicalNotesService.addPCOralCavity(esPresentingComplaintOralCavity);
		Response<PresentingComplaintOralCavity> response = new Response<PresentingComplaintOralCavity>();
		response.setData(presentingComplaintOralCavity);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_NOSE_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_NOSE_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_NOSE_EXAM)
	public Response<NoseExamination> addEditNoseExam(@RequestBody NoseExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getNoseExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		NoseExamination noseExamination = clinicalNotesService.addEditNoseExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.NOSE_EXAM, false);
		ESNoseExaminationDocument esNoseExaminationDocument = new ESNoseExaminationDocument();
		BeanUtil.map(noseExamination, esNoseExaminationDocument);
		esClinicalNotesService.addNoseExam(esNoseExaminationDocument);
		Response<NoseExamination> response = new Response<NoseExamination>();
		response.setData(noseExamination);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_NECK_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_NECK_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_NECK_EXAM)
	public Response<NeckExamination> addEditNeckExam(@RequestBody NeckExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getNeckExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		NeckExamination neckExamination = clinicalNotesService.addEditNeckExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.NECK_EXAM, false);
		ESNeckExaminationDocument esNeckExaminationDocument = new ESNeckExaminationDocument();
		BeanUtil.map(neckExamination, esNeckExaminationDocument);
		esClinicalNotesService.addNeckExam(esNeckExaminationDocument);
		Response<NeckExamination> response = new Response<NeckExamination>();
		response.setData(neckExamination);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_EARS_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_EARS_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_EARS_EXAM)
	public Response<EarsExamination> addEditEarsExam(@RequestBody EarsExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getEarsExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		EarsExamination earsExamination = clinicalNotesService.addEditEarsExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.EARS_EXAM, false);
		ESEarsExaminationDocument esEarsExaminationDocument = new ESEarsExaminationDocument();
		BeanUtil.map(earsExamination, esEarsExaminationDocument);
		esClinicalNotesService.addEarsExam(esEarsExaminationDocument);
		Response<EarsExamination> response = new Response<EarsExamination>();
		response.setData(earsExamination);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_ORAL_CAVITY_THROAT_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_ORAL_CAVITY_THROAT_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_ORAL_CAVITY_THROAT_EXAM)
	public Response<OralCavityAndThroatExamination> addEditOralCavityThroatExam(
			@RequestBody OralCavityAndThroatExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getOralCavityThroatExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		OralCavityAndThroatExamination oralCavityAndThroatExamination = clinicalNotesService
				.addEditOralCavityThroatExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.ORAL_CAVITY_THROAT_EXAM,
				false);
		ESOralCavityAndThroatExaminationDocument esOralCavityAndThroatExaminationDocument = new ESOralCavityAndThroatExaminationDocument();
		BeanUtil.map(oralCavityAndThroatExamination, esOralCavityAndThroatExaminationDocument);
		esClinicalNotesService.addOralCavityThroatExam(esOralCavityAndThroatExaminationDocument);
		Response<OralCavityAndThroatExamination> response = new Response<OralCavityAndThroatExamination>();
		response.setData(oralCavityAndThroatExamination);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_INDIRECT_LARYGOSCOPY_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_INDIRECT_LARYGOSCOPY_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_INDIRECT_LARYGOSCOPY_EXAM)
	public Response<IndirectLarygoscopyExamination> addEditIndirectLarygosccopyExam(
			@RequestBody IndirectLarygoscopyExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getIndirectLarygoscopyExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		IndirectLarygoscopyExamination indirectLarygoscopyExamination = clinicalNotesService
				.addEditIndirectLarygoscopyExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.INDIRECT_LARYGOSCOPY_EXAM,
				false);
		ESIndirectLarygoscopyExaminationDocument esIndirectLarygoscopyExaminationDocument = new ESIndirectLarygoscopyExaminationDocument();
		BeanUtil.map(indirectLarygoscopyExamination, esIndirectLarygoscopyExaminationDocument);
		esClinicalNotesService.addIndirectLarygoscopyExam(esIndirectLarygoscopyExaminationDocument);
		Response<IndirectLarygoscopyExamination> response = new Response<IndirectLarygoscopyExamination>();
		response.setData(indirectLarygoscopyExamination);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_NOSE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_NOSE, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_NOSE)
	public Response<PresentingComplaintNose> deletePCNose(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintNose presentingComplaintNose = clinicalNotesService.deletePCNose(id, doctorId, locationId,
				hospitalId, discarded);

		if (presentingComplaintNose != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintNose.getId()), Resource.PC_NOSE,
					false);
			ESPresentingComplaintNoseDocument esPresentingComplaintNoseDocument = new ESPresentingComplaintNoseDocument();
			BeanUtil.map(presentingComplaintNose, esPresentingComplaintNoseDocument);
			esClinicalNotesService.addPCNose(esPresentingComplaintNoseDocument);
		}
		Response<PresentingComplaintNose> response = new Response<PresentingComplaintNose>();
		response.setData(presentingComplaintNose);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_EARS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_EARS, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_EARS)
	public Response<PresentingComplaintEars> deletePCEars(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintEars presentingComplaintEars = clinicalNotesService.deletePCEars(id, doctorId, locationId,
				hospitalId, discarded);

		if (presentingComplaintEars != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintEars.getId()), Resource.PC_EARS,
					false);
			ESPresentingComplaintEarsDocument esPresentingComplaintEarsDocument = new ESPresentingComplaintEarsDocument();
			BeanUtil.map(presentingComplaintEars, esPresentingComplaintEarsDocument);
			esClinicalNotesService.addPCEars(esPresentingComplaintEarsDocument);
		}
		Response<PresentingComplaintEars> response = new Response<PresentingComplaintEars>();
		response.setData(presentingComplaintEars);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_ORAL_CAVITY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_ORAL_CAVITY, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_ORAL_CAVITY)
	public Response<PresentingComplaintOralCavity> deletePCOralCavity(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintOralCavity presentingComplaintOralCavity = clinicalNotesService.deletePCOralCavity(id,
				doctorId, locationId, hospitalId, discarded);

		if (presentingComplaintOralCavity != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintOralCavity.getId()),
					Resource.PC_ORAL_CAVITY, false);
			ESPresentingComplaintOralCavityDocument esPresentingComplaintOralCavityDocument = new ESPresentingComplaintOralCavityDocument();
			BeanUtil.map(presentingComplaintOralCavity, esPresentingComplaintOralCavityDocument);
			esClinicalNotesService.addPCOralCavity(esPresentingComplaintOralCavityDocument);
		}
		Response<PresentingComplaintOralCavity> response = new Response<PresentingComplaintOralCavity>();
		response.setData(presentingComplaintOralCavity);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_NOSE_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_NOSE_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_NOSE_EXAM)
	public Response<NoseExamination> deleteNoseExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		NoseExamination noseExamination = clinicalNotesService.deleteNoseExam(id, doctorId, locationId, hospitalId,
				discarded);
		if (noseExamination != null) {
			transactionalManagementService.addResource(new ObjectId(noseExamination.getId()), Resource.NOSE_EXAM,
					false);
			ESNoseExaminationDocument esNoseExaminationDocument = new ESNoseExaminationDocument();
			BeanUtil.map(noseExamination, esNoseExaminationDocument);
			esClinicalNotesService.addNoseExam(esNoseExaminationDocument);
		}
		Response<NoseExamination> response = new Response<NoseExamination>();
		response.setData(noseExamination);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_NECK_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_NECK_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_NECK_EXAM)
	public Response<NeckExamination> deleteNeckExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		NeckExamination neckExamination = clinicalNotesService.deleteNeckExam(id, doctorId, locationId, hospitalId,
				discarded);
		if (neckExamination != null) {
			transactionalManagementService.addResource(new ObjectId(neckExamination.getId()), Resource.NECK_EXAM,
					false);
			ESNeckExaminationDocument esNeckExaminationDocument = new ESNeckExaminationDocument();
			BeanUtil.map(neckExamination, esNeckExaminationDocument);
			esClinicalNotesService.addNeckExam(esNeckExaminationDocument);
		}
		Response<NeckExamination> response = new Response<NeckExamination>();
		response.setData(neckExamination);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_EARS_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_EARS_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_EARS_EXAM)
	public Response<EarsExamination> deleteEarsExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		EarsExamination EarsExamination = clinicalNotesService.deleteEarsExam(id, doctorId, locationId, hospitalId,
				discarded);
		if (EarsExamination != null) {
			transactionalManagementService.addResource(new ObjectId(EarsExamination.getId()), Resource.EARS_EXAM,
					false);
			ESEarsExaminationDocument esEarsExaminationDocument = new ESEarsExaminationDocument();
			BeanUtil.map(EarsExamination, esEarsExaminationDocument);
			esClinicalNotesService.addEarsExam(esEarsExaminationDocument);
		}
		Response<EarsExamination> response = new Response<EarsExamination>();
		response.setData(EarsExamination);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_ORAL_CAVITY_THROAT_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_ORAL_CAVITY_THROAT_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_ORAL_CAVITY_THROAT_EXAM)
	public Response<OralCavityAndThroatExamination> deleteOralCavityThroatExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		OralCavityAndThroatExamination oralCavityAndThroatExamination = clinicalNotesService
				.deleteOralCavityThroatExam(id, doctorId, locationId, hospitalId, discarded);
		if (oralCavityAndThroatExamination != null) {
			transactionalManagementService.addResource(new ObjectId(oralCavityAndThroatExamination.getId()),
					Resource.ORAL_CAVITY_THROAT_EXAM, false);
			ESOralCavityAndThroatExaminationDocument esOralCavityAndThroatExaminationDocument = new ESOralCavityAndThroatExaminationDocument();
			BeanUtil.map(oralCavityAndThroatExamination, esOralCavityAndThroatExaminationDocument);
			esClinicalNotesService.addOralCavityThroatExam(esOralCavityAndThroatExaminationDocument);
		}
		Response<OralCavityAndThroatExamination> response = new Response<OralCavityAndThroatExamination>();
		response.setData(oralCavityAndThroatExamination);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_INDIRECT_LARYGOSCOPY_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_INDIRECT_LARYGOSCOPY_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_INDIRECT_LARYGOSCOPY_EXAM)
	public Response<IndirectLarygoscopyExamination> deleteIndirectlarygoscopyExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		IndirectLarygoscopyExamination indirectLarygoscopyExamination = clinicalNotesService
				.deleteIndirectLarygoscopyExam(id, doctorId, locationId, hospitalId, discarded);
		if (indirectLarygoscopyExamination != null) {
			transactionalManagementService.addResource(new ObjectId(indirectLarygoscopyExamination.getId()),
					Resource.INDIRECT_LARYGOSCOPY_EXAM, false);
			ESIndirectLarygoscopyExaminationDocument esIndirectLarygoscopyExaminationDocument = new ESIndirectLarygoscopyExaminationDocument();
			BeanUtil.map(indirectLarygoscopyExamination, esIndirectLarygoscopyExaminationDocument);
			esClinicalNotesService.addIndirectLarygoscopyExam(esIndirectLarygoscopyExaminationDocument);
		}
		Response<IndirectLarygoscopyExamination> response = new Response<IndirectLarygoscopyExamination>();
		response.setData(indirectLarygoscopyExamination);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_THROAT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_THROAT, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_THROAT)
	public Response<PresentingComplaintThroat> deletePCThroat(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintThroat presentingComplaintThroat = clinicalNotesService.deletePCThroat(id, doctorId,
				locationId, hospitalId, discarded);
		if (presentingComplaintThroat != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintThroat.getId()),
					Resource.PC_THROAT, false);
			ESPresentingComplaintThroatDocument esPresentingComplaintThroatDocument = new ESPresentingComplaintThroatDocument();
			BeanUtil.map(presentingComplaintThroat, esPresentingComplaintThroatDocument);
			esClinicalNotesService.addPCThroat(esPresentingComplaintThroatDocument);
		}
		Response<PresentingComplaintThroat> response = new Response<PresentingComplaintThroat>();
		response.setData(presentingComplaintThroat);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_CERTIFICATE_TEMPLATES)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_CERTIFICATE_TEMPLATES, notes = PathProxy.AdminUrls.ADD_CERTIFICATE_TEMPLATES)
	public Response<Boolean> addCertificateTemplates(@RequestBody CertificateTemplate request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.addCertificateTemplates(request));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_CERTIFICATE_TEMPLATE_BY_ID)
	@ApiOperation(value = PathProxy.AdminUrls.GET_CERTIFICATE_TEMPLATE_BY_ID, notes = PathProxy.AdminUrls.GET_CERTIFICATE_TEMPLATE_BY_ID)
	public Response<CertificateTemplate> getCertificateTemplateById(
			@RequestParam(required = false, value = "templateId") String templateId) {
		if (DPDoctorUtils.anyStringEmpty(templateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CertificateTemplate> response = new Response<CertificateTemplate>();
		response.setData(adminServices.getCertificateTemplateById(templateId));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_CERTIFICATE_TEMPLATES)
	@ApiOperation(value = PathProxy.AdminUrls.GET_CERTIFICATE_TEMPLATES, notes = PathProxy.AdminUrls.GET_CERTIFICATE_TEMPLATES)
	public Response<CertificateTemplate> getCertificateTemplates(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "discarded", defaultValue = "false") Boolean discarded,
			@MatrixParam("speciality") List<String> specialities) {
		Response<CertificateTemplate> response = new Response<CertificateTemplate>();
		response.setDataList(adminServices.getCertificateTemplates(page, size, discarded, specialities));
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_CERTIFICATE_TEMPLATES)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_CERTIFICATE_TEMPLATES, notes = PathProxy.AdminUrls.DELETE_CERTIFICATE_TEMPLATES)
	public Response<Boolean> discardCertificateTemplates(@PathVariable("templateId") String templateId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(templateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.discardCertificateTemplates(templateId, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.SEARCH_DOCTOR)
	@ApiOperation(value = PathProxy.AdminUrls.SEARCH_DOCTOR, notes = PathProxy.AdminUrls.SEARCH_DOCTOR)
	public Response<DoctorSearchResponse> searchDoctors(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "speciality") String speciality,
			@RequestParam(required = false, value = "city") String city) {

		List<DoctorSearchResponse> SearchDoctorResponses = adminServices.searchDoctor(size, page, searchTerm,
				speciality, city);
		Response<DoctorSearchResponse> response = new Response<DoctorSearchResponse>();
		response.setDataList(SearchDoctorResponses);
		return response;

	}

	@GetMapping(value = PathProxy.AdminUrls.COUNT_DOCTOR)
	@ApiOperation(value = PathProxy.AdminUrls.COUNT_DOCTOR, notes = PathProxy.AdminUrls.COUNT_DOCTOR)
	public Response<DataCount> countDoctors() {

		DataCount count = adminServices.getDoctorCount();
		Response<DataCount> response = new Response<DataCount>();
		response.setData(count);
		return response;

	}

	@GetMapping(value = PathProxy.AdminUrls.COUNT_LAB)
	@ApiOperation(value = PathProxy.AdminUrls.COUNT_LAB, notes = PathProxy.AdminUrls.COUNT_LAB)
	public Response<DataCount> countLab() {

		DataCount count = adminServices.getLabsCount();
		Response<DataCount> response = new Response<DataCount>();
		response.setData(count);
		return response;

	}

	@GetMapping(value = PathProxy.AdminUrls.COUNT_CLINIC)
	@ApiOperation(value = PathProxy.AdminUrls.COUNT_CLINIC, notes = PathProxy.AdminUrls.COUNT_CLINIC)
	public Response<DataCount> countClinic() {

		DataCount count = adminServices.getclinicsCount();
		Response<DataCount> response = new Response<DataCount>();
		response.setData(count);
		return response;

	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_EXPENSE_TYPE)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_EXPENSE_TYPE, notes = PathProxy.AdminUrls.ADD_EXPENSE_TYPE)
	public Response<ExpenseType> addExpenseType(@RequestBody ExpenseType request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setData(expenseTypeService.addEditExpenseType(request));
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_EAMIL_LIST_FOR_DOCTOR_CONTACT)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_EAMIL_LIST_FOR_DOCTOR_CONTACT, notes = PathProxy.AdminUrls.ADD_EAMIL_LIST_FOR_DOCTOR_CONTACT)
	public Response<EmailList> addEmailList(@RequestBody EmailList request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<EmailList> response = new Response<EmailList>();
		response.setData(adminServices.addEmailList(request));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_EXPENSE_TYPE_BY_ID)
	@ApiOperation(value = PathProxy.AdminUrls.GET_EXPENSE_TYPE_BY_ID, notes = PathProxy.AdminUrls.GET_EXPENSE_TYPE_BY_ID)
	public Response<ExpenseType> getExpenseTypeById(
			@RequestParam(required = false, value = "expenseTypeId") String expenseTypeId) {
		if (DPDoctorUtils.anyStringEmpty(expenseTypeId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setData(expenseTypeService.getExpenseType(expenseTypeId));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_EXPENSE_TYPE)
	@ApiOperation(value = PathProxy.AdminUrls.GET_EXPENSE_TYPE, notes = PathProxy.AdminUrls.GET_EXPENSE_TYPE)
	public Response<ExpenseType> getExpenseTypes(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "discarded", defaultValue = "false") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Response<ExpenseType> response = new Response<ExpenseType>();
		response.setDataList(expenseTypeService.getExpenseType(page, size, searchTerm, discarded));
		return response;
	}

	@GetMapping(value = "/newapi")
	@ApiOperation(value = "newapi")
	public String getExpenseTypes() {
		return "Hello world";
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_EXPENSE_TYPE)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_EXPENSE_TYPE, notes = PathProxy.AdminUrls.DELETE_EXPENSE_TYPE)
	public Response<Boolean> discardExpenseType(@PathVariable("expenseTypeId") String expenseTypeId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(expenseTypeId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(expenseTypeService.deleteExpenseType(expenseTypeId, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DRUG_NAME_BY_ID)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DRUG_NAME_BY_ID, notes = PathProxy.AdminUrls.GET_DRUG_NAME_BY_ID)
	public Response<Drug> getDrugNameById(@PathVariable("drugNameId") String drugNameId) {
		if (drugNameId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Drug> response = new Response<Drug>();
		response.setData(prescriptionServices.getDrugNameById(drugNameId));
		return response;

	}

	@GetMapping(value = PathProxy.AdminUrls.GET_USER_APP_USERS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_USER_APP_USERS, notes = PathProxy.AdminUrls.GET_USER_APP_USERS)
	public Response<Object> getUserAppUsersList(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "mobileNumber") String mobileNumber) {

		Response<Object> response = adminServices.getUserAppUsers(size, page, searchTerm, mobileNumber);

		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_TAGS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_TAGS, notes = PathProxy.AdminUrls.GET_TAGS)
	public Response<Object> getTags(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		Response<Object> response = blogService.getTags(size, page, discarded);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_TAG)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_TAG, notes = PathProxy.AdminUrls.ADD_TAG)
	public Response<TagResponse> addTag(@RequestBody TagResponse request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		TagResponse blogresponse = blogService.saveTag(request);
		Response<TagResponse> response = new Response<TagResponse>();
		response.setData(blogresponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_TAG)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_TAG, notes = PathProxy.AdminUrls.DELETE_TAG)
	public Response<Boolean> deleteTag(@PathVariable("tagId") String tagId) {

		Boolean blogResponse = blogService.deleteTag(tagId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(blogResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.SEND_DAILY_REPORT_ANALYTIC_REPORT_TO_DOCTOR)
	@ApiOperation(value = PathProxy.AdminUrls.SEND_DAILY_REPORT_ANALYTIC_REPORT_TO_DOCTOR, notes = PathProxy.AdminUrls.SEND_DAILY_REPORT_ANALYTIC_REPORT_TO_DOCTOR)
	public Response<Boolean> getDailyReportAnalyticstoDoctor() {
		Boolean dailyReportAnalyticstoDoctorResponse = adminServices.getDailyReportAnalyticstoDoctor();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(dailyReportAnalyticstoDoctorResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.SEND_WEEKLY_REPORT_ANALYTIC_REPORT_TO_DOCTOR)
	@ApiOperation(value = PathProxy.AdminUrls.SEND_WEEKLY_REPORT_ANALYTIC_REPORT_TO_DOCTOR, notes = PathProxy.AdminUrls.SEND_WEEKLY_REPORT_ANALYTIC_REPORT_TO_DOCTOR)
	public Response<Boolean> getWeeklyReportAnalyticstoDoctor() {
		Boolean dailyReportAnalyticstoDoctorResponse = adminServices.getWeeklyReportAnalyticstoDoctor();

		Response<Boolean> response = new Response<Boolean>();
		response.setData(dailyReportAnalyticstoDoctorResponse);
		return response;
	}
}
