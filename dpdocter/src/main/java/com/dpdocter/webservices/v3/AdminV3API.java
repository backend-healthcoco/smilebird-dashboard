package com.dpdocter.webservices.v3;

import java.util.List;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DoctorSlugUrlRequest;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DentalCampRequest;
import com.dpdocter.request.DentalTreatmentDetailRequest;
import com.dpdocter.request.LeadsTypeReasonsRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.DentalAdminNameResponse;
import com.dpdocter.response.DentalCampResponse;
import com.dpdocter.response.DentalTreatmentDetailResponse;
import com.dpdocter.response.DentalTreatmentNameResponse;
import com.dpdocter.response.LeadsTypeReasonsResponse;
import com.dpdocter.services.AdminServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.ADMIN_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "Endpoint for admin")
public class AdminV3API {

	private static Logger logger = LogManager.getLogger(AdminV3API.class.getName());

	@Autowired
	AdminServices adminServices;

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_DENTALCHAIN_STATE_OF_CLINIC)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_DENTALCHAIN_STATE_OF_CLINIC, notes = PathProxy.AdminUrls.UPDATE_DENTALCHAIN_STATE_OF_CLINIC)
	public Response<Boolean> updateDentalChainStateOfClinic(@PathVariable(value = "locationId") String locationId,
			@RequestParam(required = false, value = "isDentalChain", defaultValue = "false") Boolean isDentalChain) {
		Boolean doctorContactUs = adminServices.updateDentalChainStateOfClinic(locationId, isDentalChain);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorContactUs);
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
		response.setData(adminServices.countClinics(hospitalId, isClinic, isLab, isParent, isDentalWorkLab,
				isDentalImagingLab, isDentalChain, searchTerm, isListed, isActivate, city, toDate, fromDate));
		response.setDataList(locations);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_PATIENT_VERIFY)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_PATIENT_VERIFY, notes = PathProxy.AdminUrls.UPDATE_PATIENT_VERIFY)
	public Response<Boolean> updatePatientIsDentalChain(@PathVariable(value = "patientId") String patientId,
			@RequestParam(required = false, value = "isDentalChainVerified", defaultValue = "false") Boolean isDentalChainVerified) {
		Boolean doctorContactUs = adminServices.updatePatientIsDentalChain(patientId, isDentalChainVerified);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorContactUs);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_EDIT_DENTAL_TREAMENT_DETAIL)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_EDIT_DENTAL_TREAMENT_DETAIL, notes = PathProxy.AdminUrls.ADD_EDIT_DENTAL_TREAMENT_DETAIL)
	public Response<DentalTreatmentDetailResponse> addEditDentalTreatmentDetail(
			@RequestBody DentalTreatmentDetailRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DentalTreatmentDetailResponse dentalCampResponse = adminServices.addEditDentalTreatmentDetail(request);

		Response<DentalTreatmentDetailResponse> response = new Response<DentalTreatmentDetailResponse>();
		response.setData(dentalCampResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_EDIT_DENTAL_REASONS)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_EDIT_DENTAL_REASONS, notes = PathProxy.AdminUrls.ADD_EDIT_DENTAL_REASONS)
	public Response<LeadsTypeReasonsResponse> addEditLeadsTypeReasons(@RequestBody LeadsTypeReasonsRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LeadsTypeReasonsResponse dentalCampResponse = adminServices.addEditLeadsTypeReasons(request);

		Response<LeadsTypeReasonsResponse> response = new Response<LeadsTypeReasonsResponse>();
		response.setData(dentalCampResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DENTAL_REASONS)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DENTAL_REASONS, notes = PathProxy.AdminUrls.DELETE_DENTAL_REASONS)
	public Response<Boolean> deleteDentalReasonsById(@PathVariable(value = "reasonId") String reasonId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(reasonId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.deleteDentalReasonsById(reasonId, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_USER_PROFILE)
	@ApiOperation(value = PathProxy.AdminUrls.GET_USER_PROFILE, notes = PathProxy.AdminUrls.GET_USER_PROFILE)
	public Response<Object> getUserProfile(@RequestParam(required = false, value = "userId") String userId) {
		Response<Object> response = adminServices.getUserProfile(userId);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DENTAL_REASONS)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DENTAL_REASONS, notes = PathProxy.AdminUrls.GET_DENTAL_REASONS)
	public Response<Object> getDentalReasonsList(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Response<Object> response = adminServices.getDentalReasonsList(page, size, searchTerm);
		return response;
	}

	@DeleteMapping(value = PathProxy.AdminUrls.DELETE_DENTAL_TREAMENT_DETAIL)
	@ApiOperation(value = PathProxy.AdminUrls.DELETE_DENTAL_TREAMENT_DETAIL, notes = PathProxy.AdminUrls.DELETE_DENTAL_TREAMENT_DETAIL)
	public Response<Boolean> deleteDentalTreatmentDetailById(@PathVariable(value = "id") String id,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.deleteDentalTreatmentDetailById(id, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DENTAL_TREAMENT_DETAIL)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DENTAL_TREAMENT_DETAIL, notes = PathProxy.AdminUrls.GET_DENTAL_TREAMENT_DETAIL)
	public Response<Object> getDentalTreatmentDetail(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		List<DentalTreatmentDetailResponse> doctorContactUs = adminServices.getDentalTreatmentDetail(page, size,
				searchTerm);
		Response<Object> response = new Response<Object>();
		response.setDataList(doctorContactUs);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DENTAL_TREAMENT_NAMES)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DENTAL_TREAMENT_NAMES, notes = PathProxy.AdminUrls.GET_DENTAL_TREAMENT_NAMES)
	public Response<Object> getDentalTreatmentNames(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		List<DentalTreatmentNameResponse> doctorContactUs = adminServices.getDentalTreatmentNames(page, size,
				searchTerm);
		Response<Object> response = new Response<Object>();
		response.setDataList(doctorContactUs);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.GET_DENTAL_ADMIN_NAME)
	@ApiOperation(value = PathProxy.AdminUrls.GET_DENTAL_ADMIN_NAME, notes = PathProxy.AdminUrls.GET_DENTAL_ADMIN_NAME)
	public Response<Object> getDentalAdminNames(@RequestParam(required = false, defaultValue = "false") Boolean isBuddy,
			@RequestParam(required = false, defaultValue = "false") Boolean isAgent) {
		List<DentalAdminNameResponse> doctorContactUs = adminServices.getDentalAdminNames(isBuddy, isAgent);
		Response<Object> response = new Response<Object>();
		response.setDataList(doctorContactUs);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.EDIT_LOCATION_SLUG_URL)
	@ApiOperation(value = PathProxy.AdminUrls.EDIT_LOCATION_SLUG_URL, notes = PathProxy.AdminUrls.EDIT_LOCATION_SLUG_URL)
	public Response<Boolean> editLocationSlugUrl(
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "locationSlugUrl") String locationSlugUrl) {
		if (DPDoctorUtils.anyStringEmpty(locationId) || DPDoctorUtils.anyStringEmpty(locationSlugUrl)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditRegistrationDetailResponse = adminServices.editLocationSlugUrl(locationId, locationSlugUrl);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AdminUrls.ADD_EDIT_CLINIC_CHARGE_CODES)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_EDIT_CLINIC_CHARGE_CODES, notes = PathProxy.AdminUrls.ADD_EDIT_CLINIC_CHARGE_CODES)
	public Response<Boolean> addEditClinicChargeCode(
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "chargeCode") String chargeCode) {
		if (DPDoctorUtils.anyStringEmpty(locationId) || DPDoctorUtils.anyStringEmpty(chargeCode)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditRegistrationDetailResponse = adminServices.addEditClinicChargeCode(locationId, chargeCode);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}
}
