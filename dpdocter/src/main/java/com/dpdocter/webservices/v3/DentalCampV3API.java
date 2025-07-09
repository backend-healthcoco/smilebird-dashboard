package com.dpdocter.webservices.v3;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.CampMsgTemplateRequest;
import com.dpdocter.request.CampNameRequest;
import com.dpdocter.request.CampaignNameRequest;
import com.dpdocter.request.CampaignObjectRequest;
import com.dpdocter.request.DentalCampBroadcastTemplateRequest;
import com.dpdocter.request.DentalCampRequest;
import com.dpdocter.request.DentalCampTreatmentNameRequest;
import com.dpdocter.request.FollowupCommunicationRequest;
import com.dpdocter.response.CampMsgTemplateResponse;
import com.dpdocter.response.CampNameResponse;
import com.dpdocter.response.CampaignNameResponse;
import com.dpdocter.response.CampaignObjectResponse;
import com.dpdocter.response.DentalCampResponse;
import com.dpdocter.response.DentalChainFile;
import com.dpdocter.response.FollowupCommunicationResponse;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.DENTAL_CAMP_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_CAMP_BASE_URL, description = "Endpoint for appointment")
public class DentalCampV3API {

	private static Logger logger = LogManager.getLogger(DentalCampV3API.class.getName());

	@Autowired
	private DentalCampService dentalCampService;

	@PostMapping(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP_NAME, notes = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP_NAME)
	public Response<CampNameResponse> addEditCampName(@RequestBody CampNameRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		CampNameResponse campNameResponse = dentalCampService.addEditCampName(request);

		Response<CampNameResponse> response = new Response<CampNameResponse>();
		response.setData(campNameResponse);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_NAME, notes = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_NAME)
	public Response<Object> getCampNames(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isCamp") Boolean isCamp,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = dentalCampService.getCampNames(size, page, searchTerm, isDiscarded, isCamp);
		return response;
	}

	@DeleteMapping(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP_NAME, notes = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP_NAME)
	public Response<Boolean> deleteCampName(@PathVariable(value = "campNameId") String campNameId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(campNameId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalCampService.deleteCampName(campNameId, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_NAME_BY_ID)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_NAME_BY_ID, notes = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_NAME_BY_ID)
	public Response<CampNameResponse> getCampNameById(@PathVariable(value = "campNameId") String campNameId) {
		if (campNameId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		CampNameResponse campNameResponses = dentalCampService.getCampNameById(campNameId);
		Response<CampNameResponse> response = new Response<CampNameResponse>();
		response.setData(campNameResponses);
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP)
	@ApiOperation(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP, notes = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP)
	public Response<DentalCampResponse> addEditDentalCamp(@RequestBody DentalCampRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DentalCampResponse dentalCampResponse = dentalCampService.addEditDentalCamp(request);

		Response<DentalCampResponse> response = new Response<DentalCampResponse>();
		response.setData(dentalCampResponse);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP, notes = PathProxy.DentalCampUrls.GET_DENTAL_CAMP)
	public Response<Object> getDentalCamps(@RequestParam(required = false, value = "locality") String locality,
			@RequestParam(required = false, value = "language") String language,
			@RequestParam(value = "treatmentId", required = false) List<String> treatmentId,
			@RequestParam(value = "associateDoctorId", required = false) List<String> associateDoctorId,
			@RequestParam(required = false, value = "salaryRange") String salaryRange,
			@RequestParam(required = false, value = "smileBuddyId") String smileBuddyId,
			@RequestParam(required = false, value = "campaignId") String campaignId,
			@RequestParam(required = false, value = "campName") String campName,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "leadType") String leadType,
			@RequestParam(required = false, value = "leadStage") String leadStage,
			@RequestParam(required = false, value = "profession") String profession,
			@RequestParam(required = false, value = "isPatientCreated") Boolean isPatientCreated,
			@RequestParam(required = false, value = "isMobileNumberPresent") Boolean isMobileNumberPresent,
			@RequestParam(required = false, value = "isPhotoUpload") String isPhotoUpload,
			@RequestParam(required = false, value = "followupType") String followupType,
			@RequestParam(required = false, value = "gender") String gender,
			@RequestParam(required = false, value = "age") String age,
			@RequestParam(required = false, value = "complaint") String complaint,
			@RequestParam(required = false, value = "dateFilterType") String dateFilterType,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = dentalCampService.getDentalCamps(treatmentId, associateDoctorId,salaryRange, campName, size, page,
				locality, language, city, leadType, leadStage, profession, isPatientCreated, isPhotoUpload, gender,
				followupType, age, complaint, dateFilterType, fromDate, toDate, searchTerm, isDiscarded,
				isMobileNumberPresent, smileBuddyId,campaignId);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_LEADS_COUNT)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_LEADS_COUNT, notes = PathProxy.DentalCampUrls.GET_DENTAL_LEADS_COUNT)
	public Response<Object> getDentalCampsCount(@RequestParam(required = false, value = "locality") String locality,
			@RequestParam(required = false, value = "language") String language,
			@RequestParam(value = "treatmentId", required = false) List<String> treatmentId,
			@RequestParam(value = "associateDoctorId", required = false) List<String> associateDoctorId,
			@RequestParam(required = false, value = "salaryRange") String salaryRange,
			@RequestParam(required = false, value = "campName") String campName,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "leadType") String leadType,
			@RequestParam(required = false, value = "leadStage") String leadStage,
			@RequestParam(required = false, value = "profession") String profession,
			@RequestParam(required = false, value = "isPatientCreated") Boolean isPatientCreated,
			@RequestParam(required = false, value = "isMobileNumberPresent") Boolean isMobileNumberPresent,
			@RequestParam(required = false, value = "isPhotoUpload") String isPhotoUpload,
			@RequestParam(required = false, value = "followupType") String followupType,
			@RequestParam(required = false, value = "gender") String gender,
			@RequestParam(required = false, value = "age") String age,
			@RequestParam(required = false, value = "complaint") String complaint,
			@RequestParam(required = false, value = "dateFilterType") String dateFilterType,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = dentalCampService.getDentalCampsCount(treatmentId, associateDoctorId, salaryRange,
				campName, size, page, locality, language, city, leadType, leadStage, profession, isPatientCreated,
				isPhotoUpload, gender, followupType, age, complaint, dateFilterType, fromDate, toDate, searchTerm,
				isDiscarded, isMobileNumberPresent);
		return response;
	}

	@DeleteMapping(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP)
	@ApiOperation(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP, notes = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP)
	public Response<Boolean> deleteDentalCampsById(@PathVariable(value = "campId") String campId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(campId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalCampService.deleteDentalCampsById(campId, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_BY_ID)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_BY_ID, notes = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_BY_ID)
	public Response<DentalCampResponse> getDentalCampsById(@PathVariable(value = "campId") String campId) {
		if (campId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DentalCampResponse dentalCampResponses = dentalCampService.getDentalCampsById(campId);
		Response<DentalCampResponse> response = new Response<DentalCampResponse>();
		response.setData(dentalCampResponses);
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.ADD_EDIT_CAMP_MSG_TEMPLATE)
	@ApiOperation(value = PathProxy.DentalCampUrls.ADD_EDIT_CAMP_MSG_TEMPLATE, notes = PathProxy.DentalCampUrls.ADD_EDIT_CAMP_MSG_TEMPLATE)
	public Response<CampMsgTemplateResponse> addEditCampMsgTemplate(@RequestBody CampMsgTemplateRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		CampMsgTemplateResponse campNameResponse = dentalCampService.addEditCampMsgTemplate(request);

		Response<CampMsgTemplateResponse> response = new Response<CampMsgTemplateResponse>();
		response.setData(campNameResponse);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_MSG_TEMPLATE)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_MSG_TEMPLATE, notes = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_MSG_TEMPLATE)
	public Response<Object> getCampMsgTemplate(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "broadcastType") String broadcastType,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = dentalCampService.getCampMsgTemplate(size, page, searchTerm, isDiscarded,
				broadcastType);
		return response;
	}

	@DeleteMapping(value = PathProxy.DentalCampUrls.DELETE_CAMP_MSG_TEMPLATE)
	@ApiOperation(value = PathProxy.DentalCampUrls.DELETE_CAMP_MSG_TEMPLATE, notes = PathProxy.DentalCampUrls.DELETE_CAMP_MSG_TEMPLATE)
	public Response<Boolean> deleteCampMsgTemplate(@PathVariable(value = "templateId") String templateId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(templateId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalCampService.deleteCampMsgTemplate(templateId, isDiscarded));
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP_TREATMENT_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP_TREATMENT_NAME, notes = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMP_TREATMENT_NAME)
	public Response<DentalCampTreatmentNameRequest> addEditDentalCampTreatmentName(
			@RequestBody DentalCampTreatmentNameRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DentalCampTreatmentNameRequest dentalCampTreatmentNameRequest = dentalCampService
				.addEditDentalCampTreatmentName(request);
		Response<DentalCampTreatmentNameRequest> response = new Response<DentalCampTreatmentNameRequest>();
		response.setData(dentalCampTreatmentNameRequest);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_TREATMENT_LIST)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_TREATMENT_LIST, notes = PathProxy.DentalCampUrls.GET_DENTAL_CAMP_TREATMENT_LIST)
	public Response<Object> getDentalCampsTreatmentList(@RequestParam(required = false, value = "type") String type,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = dentalCampService.getDentalCampsTreatmentList(size, page, type, searchTerm,
				isDiscarded);
		return response;

	}

	@PostMapping(value = PathProxy.DentalCampUrls.UPLOAD_IMAGE, consumes = { MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.DentalCampUrls.UPLOAD_IMAGE, notes = PathProxy.DentalCampUrls.UPLOAD_IMAGE)
	public Response<DentalChainFile> saveImage(@RequestParam(value = "file") MultipartFile file) {

		DentalChainFile mindfulness = dentalCampService.uploadImage(file);
		Response<DentalChainFile> response = new Response<DentalChainFile>();
		response.setData(mindfulness);
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.UPLOAD_VIDEO, consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = PathProxy.DentalCampUrls.UPLOAD_VIDEO, notes = PathProxy.DentalCampUrls.UPLOAD_VIDEO)
	public Response<DentalChainFile> saveVideo(@RequestParam(value = "file") MultipartFile file) {

		DentalChainFile mindfulness = dentalCampService.uploadVideo(file);
		Response<DentalChainFile> response = new Response<DentalChainFile>();
		response.setData(mindfulness);
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.UPLOAD_AUDIO, consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = PathProxy.DentalCampUrls.UPLOAD_AUDIO, notes = PathProxy.DentalCampUrls.UPLOAD_AUDIO)
	public Response<DentalChainFile> uploadAudio(@RequestParam(value = "file") MultipartFile file) {

		DentalChainFile mindfulness = dentalCampService.uploadAudio(file);
		Response<DentalChainFile> response = new Response<DentalChainFile>();
		response.setData(mindfulness);
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.BROADCAST_CAMP_MSG_TEMPLATE)
	@ApiOperation(value = PathProxy.DentalCampUrls.BROADCAST_CAMP_MSG_TEMPLATE, notes = PathProxy.DentalCampUrls.BROADCAST_CAMP_MSG_TEMPLATE)
	public Response<Boolean> broadcastTemplate(@RequestBody DentalCampBroadcastTemplateRequest request) {
		Boolean isBroadccast = false;
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		switch (request.getBroadcastTo()) {
		case DOCTOR:
			isBroadccast = dentalCampService.broadcastTemplateToDoctors(request);
			break;
		case PATIENT:
			isBroadccast = dentalCampService.broadcastTemplateToPatients(request);
			break;
		case LEADS:
			isBroadccast = dentalCampService.broadcastTemplateToLeads(request);
			break;
		default:
			isBroadccast = dentalCampService.broadcastTemplateToLeads(request);
			break;
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(isBroadccast);
		return response;
	}

	@DeleteMapping(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP_TREATMENT_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP_TREATMENT_NAME, notes = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMP_TREATMENT_NAME)
	public Response<Boolean> deleteTreatmentName(@PathVariable(value = "id") String id,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalCampService.deleteTreatmentName(id, isDiscarded));
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.ADD_EDIT_FOLLOWUP_COMMUNICATION)
	@ApiOperation(value = PathProxy.DentalCampUrls.ADD_EDIT_FOLLOWUP_COMMUNICATION, notes = PathProxy.DentalCampUrls.ADD_EDIT_FOLLOWUP_COMMUNICATION)
	public Response<FollowupCommunicationRequest> addEditFollowupCommunication(
			@RequestBody FollowupCommunicationRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		FollowupCommunicationRequest communicationDoctorTeamRequest = dentalCampService
				.addEditFollowupCommunication(request);
		Response<FollowupCommunicationRequest> response = new Response<FollowupCommunicationRequest>();
		response.setData(communicationDoctorTeamRequest);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_FOLLOWUP_COMMUNICATION_DETAILS_LIST)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_FOLLOWUP_COMMUNICATION_DETAILS_LIST, notes = PathProxy.DentalCampUrls.GET_FOLLOWUP_COMMUNICATION_DETAILS_LIST)
	public Response<FollowupCommunicationRequest> getFollowupDetailList(
			@RequestParam(required = false, value = "size", defaultValue = "10") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = dentalCampService.countDetailsList(searchTerm);
		Response<FollowupCommunicationRequest> response = new Response<FollowupCommunicationRequest>();
		if (count > 0)
			response.setDataList(dentalCampService.getFollowupDetailList(size, page, searchTerm));
		response.setCount(count);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_FOLLOWUP_COMMUNICATION_DETAILS_GET_BY_USERID)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_FOLLOWUP_COMMUNICATION_DETAILS_GET_BY_USERID, notes = PathProxy.DentalCampUrls.GET_FOLLOWUP_COMMUNICATION_DETAILS_GET_BY_USERID)
	public Response<FollowupCommunicationRequest> getFollowupByUserId(@PathVariable("userId") String userId) {
		if (userId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<FollowupCommunicationRequest> response = new Response<FollowupCommunicationRequest>();
		response.setData(dentalCampService.getFollowupByUserId(userId));
		return response;

	}

	@PostMapping(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMPAIGN_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMPAIGN_NAME, notes = PathProxy.DentalCampUrls.ADD_EDIT_DENTAL_CAMPAIGN_NAME)
	public Response<CampaignNameResponse> addEditCampaignName(@RequestBody CampaignNameRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		CampaignNameResponse campNameResponse = dentalCampService.addEditCampaignName(request);

		Response<CampaignNameResponse> response = new Response<CampaignNameResponse>();
		response.setData(campNameResponse);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMPAIGN_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_DENTAL_CAMPAIGN_NAME, notes = PathProxy.DentalCampUrls.GET_DENTAL_CAMPAIGN_NAME)
	public Response<Object> getCampaignNames(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = dentalCampService.getCampaignNames(size, page, searchTerm, isDiscarded);
		return response;
	}

	@DeleteMapping(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMPAIGN_NAME)
	@ApiOperation(value = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMPAIGN_NAME, notes = PathProxy.DentalCampUrls.DELETE_DENTAL_CAMPAIGN_NAME)
	public Response<Boolean> deleteCampaignName(@PathVariable(value = "campaignId") String campaignId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(campaignId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalCampService.deleteCampaignName(campaignId, isDiscarded));
		return response;
	}

	@PostMapping(value = PathProxy.DentalCampUrls.ADD_EDIT_CAMPAIGN_OBJECT)
	@ApiOperation(value = PathProxy.DentalCampUrls.ADD_EDIT_CAMPAIGN_OBJECT, notes = PathProxy.DentalCampUrls.ADD_EDIT_CAMPAIGN_OBJECT)
	public Response<CampaignObjectResponse> addEditCampaigntObject(@RequestBody CampaignObjectRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		CampaignObjectResponse campNameResponse = dentalCampService.addEditCampaignObject(request);

		Response<CampaignObjectResponse> response = new Response<CampaignObjectResponse>();
		response.setData(campNameResponse);
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_CAMPAIGN_OBJECT)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_CAMPAIGN_OBJECT, notes = PathProxy.DentalCampUrls.GET_CAMPAIGN_OBJECT)
	public Response<Object> getCampaignObjects(@RequestParam(required = false, value = "campaignId") String campaignId,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = dentalCampService.getCampaignObjects(campaignId, size, page, isDiscarded);
		return response;
	}

	@DeleteMapping(value = PathProxy.DentalCampUrls.DELETE_CAMPAIGN_OBJECT)
	@ApiOperation(value = PathProxy.DentalCampUrls.DELETE_CAMPAIGN_OBJECT, notes = PathProxy.DentalCampUrls.DELETE_CAMPAIGN_OBJECT)
	public Response<Boolean> deleteCampaignObject(@PathVariable(value = "campaignObjectId") String campaignObjectId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(campaignObjectId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalCampService.deleteCampaignObject(campaignObjectId, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.DentalCampUrls.GET_FOLLOWUP_DETAILS_LIST)
	@ApiOperation(value = PathProxy.DentalCampUrls.GET_FOLLOWUP_DETAILS_LIST, notes = PathProxy.DentalCampUrls.GET_FOLLOWUP_DETAILS_LIST)
	public Response<FollowupCommunicationResponse> getFollowupList(
			@RequestParam(required = false, value = "smileBuddyId") String smileBuddyId,
			@RequestParam(value = "treatmentId", required = false) List<String> treatmentId,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "size", defaultValue = "10") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = dentalCampService.countDetailsList(searchTerm);
		Response<FollowupCommunicationResponse> response = new Response<FollowupCommunicationResponse>();
		if (count > 0)
			response.setDataList(dentalCampService.getFollowupList(smileBuddyId, treatmentId, fromDate, toDate, size,
					page, searchTerm));
		response.setCount(count);
		return response;
	}
}
