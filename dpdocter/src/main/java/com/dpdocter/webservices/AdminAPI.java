package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DataCount;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.ExpenseType;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocaleContactUs;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.beans.VerifiedDocuments;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.LocaleContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.EmailList;
import com.dpdocter.request.VerifiedDocumentRequest;
import com.dpdocter.response.DoctorResponseNew;
import com.dpdocter.response.DoctorSearchResponse;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.ClinicContactUsService;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.ExpenseTypeService;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.LocaleContactUsService;
import com.dpdocter.services.SubscriptionDetailServices;
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
	HistoryServices historyServices;

	@Autowired
	ESMasterService esMasterService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

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

		Response<Object> response = adminServices.getDoctors(page, size, locationId, state, searchTerm, isListed,
				isNutritionist, isAdminNutritionist, isActive, isRegistrationDetailsVerified, isPhotoIdVerified,
				isOnlineConsultationAvailable, city, specialitiesIds, toDate, fromDate, isHealthcocoDoctor);
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

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_CLINIC_NPS_SCORE)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_CLINIC_NPS_SCORE, notes = PathProxy.AdminUrls.UPDATE_CLINIC_NPS_SCORE)
	public Response<Boolean> updateClinicNpsScore(
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "npsScore", defaultValue = "0") double npsScore) {
		Boolean count = adminServices.updateClinicNpsScore(locationId, npsScore);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(count);
		return response;
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

	@GetMapping(value = PathProxy.AdminUrls.BLOCK_USER)
	@ApiOperation(value = PathProxy.AdminUrls.BLOCK_USER, notes = PathProxy.AdminUrls.BLOCK_USER)
	public Response<Boolean> blockUser(@PathVariable("userId") String userId,
			@RequestParam(required = false, value = "blockForDay", defaultValue = "true") Boolean blockForDay,
			@RequestParam(required = false, value = "blockForHour", defaultValue = "false") Boolean blockForHour) {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.blockUser(userId, blockForDay, blockForHour));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_LOCATION)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_LOCATION, notes = PathProxy.AdminUrls.UPDATE_LOCATION)
	public Response<Boolean> updateLocation() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.updateLocation());
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
