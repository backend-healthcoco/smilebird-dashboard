package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DoctorConference;
import com.dpdocter.beans.DoctorConferenceAgenda;
import com.dpdocter.beans.DoctorConferenceSession;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.RegisteredUser;
import com.dpdocter.beans.ResearchPaperCategory;
import com.dpdocter.beans.ResearchPaperSubCategory;
import com.dpdocter.beans.SessionTopic;
import com.dpdocter.beans.SpeakerProfile;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ConferenceBulkSMSRequest;
import com.dpdocter.request.ConfexAdminRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.SessionDateResponse;
import com.dpdocter.services.ConferenceService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.CONFERENCE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CONFERENCE_URL, description = "Endpoint for Conference")
public class DoctorConferenceAPI {

	private static Logger logger = LogManager.getLogger(DoctorConferenceAPI.class.getName());

	@Autowired
	private ConferenceService conferenceService;

	@Value(value = "${image.path}")
	private String imagePath;

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_EDIT_SESSION_TOPIC)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_SESSION_TOPIC, notes = PathProxy.ConferenceUrls.ADD_EDIT_SESSION_TOPIC)
	public Response<SessionTopic> addEditTopic(@RequestBody SessionTopic request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getTopic())) {
			logger.warn("Topic should not null");
			throw new BusinessException(ServiceError.InvalidInput, "Topic should not null");
		}
		SessionTopic topic = conferenceService.addEditTopic(request);
		Response<SessionTopic> response = new Response<SessionTopic>();
		response.setData(topic);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_SESSION_TOPICS)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SESSION_TOPICS, notes = PathProxy.ConferenceUrls.GET_SESSION_TOPICS)
	public Response<SessionTopic> getTopics(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {

		Response<SessionTopic> response = new Response<SessionTopic>();
		response.setDataList(conferenceService.getTopics(size, page, discarded, searchTerm));
		return response;
	}

	@DeleteMapping(value = PathProxy.ConferenceUrls.DELETE_SESSION_TOPIC)
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_SESSION_TOPIC, notes = PathProxy.ConferenceUrls.DELETE_SESSION_TOPIC)
	public Response<SessionTopic> deleteTopic(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		SessionTopic topic = conferenceService.deleteTopic(id, discarded);

		Response<SessionTopic> response = new Response<SessionTopic>();
		response.setData(topic);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_SESSION_TOPIC)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SESSION_TOPIC, notes = PathProxy.ConferenceUrls.GET_SESSION_TOPIC)
	public Response<SessionTopic> getTopic(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		SessionTopic topic = conferenceService.getTopic(id);
		Response<SessionTopic> response = new Response<SessionTopic>();
		response.setData(topic);
		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_EDIT_SPEAKER_PROFILE)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_SPEAKER_PROFILE, notes = PathProxy.ConferenceUrls.ADD_EDIT_SPEAKER_PROFILE)
	public Response<SpeakerProfile> addEditSpeakerProfile(@RequestBody SpeakerProfile request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getFirstName())) {
			logger.warn("Topic should not null");
			throw new BusinessException(ServiceError.InvalidInput, "Topic should not null");
		}
		SpeakerProfile speaker = conferenceService.addEditSpeakerProfile(request);
		Response<SpeakerProfile> response = new Response<SpeakerProfile>();
		response.setData(speaker);
		return response;

	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILES)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILES, notes = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILES)
	public Response<SpeakerProfile> getSpeakerProfiles(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {

		Response<SpeakerProfile> response = new Response<SpeakerProfile>();
		response.setDataList(conferenceService.getSpeakerProfiles(size, page, discarded, searchTerm));
		return response;

	}

	@DeleteMapping(value = PathProxy.ConferenceUrls.DELETE_SPEAKER_PROFILE)
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_SPEAKER_PROFILE, notes = PathProxy.ConferenceUrls.DELETE_SPEAKER_PROFILE)
	public Response<SpeakerProfile> deleteSpeakerProfile(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		SpeakerProfile topic = conferenceService.deleteSpeakerProfile(id, discarded);
		Response<SpeakerProfile> response = new Response<SpeakerProfile>();
		response.setData(topic);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILE)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILE, notes = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILE)
	public Response<SpeakerProfile> getSpeakerProfile(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		SpeakerProfile profile = conferenceService.getSpeakerProfile(id);
		Response<SpeakerProfile> response = new Response<SpeakerProfile>();
		response.setData(profile);
		return response;

	}

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_EDIT_CONFERENCE_SESSION)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_CONFERENCE_SESSION, notes = PathProxy.ConferenceUrls.ADD_EDIT_CONFERENCE_SESSION)
	public Response<DoctorConferenceSession> addEditConferenceSession(@RequestBody DoctorConferenceSession request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getTitle(), request.getConferenceId())
				|| request.getOnDate() == null || request.getSchedule() == null) {
			logger.warn("Title,conferrenceId,onDate,Schedule should not null");
			throw new BusinessException(ServiceError.InvalidInput,
					"Title,conferrenceId,onDate,Schedule should not null");
		}
		DoctorConferenceSession session = conferenceService.addEditConferenceSession(request);
		Response<DoctorConferenceSession> response = new Response<DoctorConferenceSession>();
		response.setData(session);
		return response;

	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSIONS)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSIONS, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSIONS)
	public Response<DoctorConferenceSession> getConferenceSessions(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") boolean discarded,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "fromtime", defaultValue = "0") Integer fromtime,
			@RequestParam(required = false, value = "toTime", defaultValue = "0") Integer toTime,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@PathVariable("conferenceId") String conferenceId,
			@MatrixVariable(required = false, value = "topics") List<String> topics) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<DoctorConferenceSession> response = new Response<DoctorConferenceSession>();
		response.setDataList(conferenceService.getConferenceSessions(size, page, discarded, city, fromtime, toTime,
				fromDate, toDate, searchTerm, conferenceId, topics));
		return response;
	}

	@DeleteMapping(value = PathProxy.ConferenceUrls.DELETE_CONFERENCE_SESSION)
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_CONFERENCE_SESSION, notes = PathProxy.ConferenceUrls.DELETE_CONFERENCE_SESSION)
	public Response<DoctorConferenceSession> deleteConferenceSession(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConferenceSession session = conferenceService.deleteConferenceSession(id, discarded);
		Response<DoctorConferenceSession> response = new Response<DoctorConferenceSession>();
		response.setData(session);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION)
	public Response<DoctorConferenceSession> getConferenceSession(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConferenceSession session = conferenceService.getConferenceSession(id);
		Response<DoctorConferenceSession> response = new Response<DoctorConferenceSession>();
		response.setData(session);
		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_EDIT_DOCTOR_CONFERENCE)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_DOCTOR_CONFERENCE, notes = PathProxy.ConferenceUrls.ADD_EDIT_DOCTOR_CONFERENCE)
	public Response<DoctorConference> addEditDoctorConference(@RequestBody DoctorConference request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getTitle()) || request.getFromDate() == null
				|| request.getToDate() == null) {
			logger.warn("Title,FromDate,ToDate should not null");
			throw new BusinessException(ServiceError.InvalidInput, "Topic should not null");
		}
		DoctorConference coference = conferenceService.addEditDoctorConference(request);
		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setData(coference);
		return response;

	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCES)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCES, notes = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCES)
	public Response<DoctorConference> getDoctorConference(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") boolean discarded,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "speciality") String speciality,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		List<DoctorConference> coference = conferenceService.getDoctorConference(size, page, discarded, city,
				speciality, fromDate, toDate, searchTerm);
		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setDataList(coference);
		return response;
	}

	@DeleteMapping(value = PathProxy.ConferenceUrls.DELETE_DOCTOR_CONFERENCE)
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_DOCTOR_CONFERENCE, notes = PathProxy.ConferenceUrls.DELETE_DOCTOR_CONFERENCE)
	public Response<DoctorConference> deleteDoctorConference(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") boolean discarded) {
		DoctorConference coference = conferenceService.deleteDoctorConference(id, discarded);
		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setData(coference);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCE)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCE, notes = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCE)
	public Response<DoctorConference> getDoctorConference(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorConference coference = conferenceService.getDoctorConference(id);
		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setData(coference);
		return response;

	}

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_EDIT_CONFERENCE_AGENDA)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_CONFERENCE_AGENDA, notes = PathProxy.ConferenceUrls.ADD_EDIT_CONFERENCE_AGENDA)
	public Response<DoctorConferenceAgenda> addEditConferenceAgenda(@RequestBody DoctorConferenceAgenda request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getTitle(), request.getConferenceId())
				|| request.getOnDate() == null || request.getSchedule() == null) {
			logger.warn("Title,conferrenceId,onDate,Schedule should not null");
			throw new BusinessException(ServiceError.InvalidInput, "Topic should not null");
		}
		DoctorConferenceAgenda agenda = conferenceService.addEditConferenceAgenda(request);
		Response<DoctorConferenceAgenda> response = new Response<DoctorConferenceAgenda>();
		response.setData(agenda);
		return response;

	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDAS)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDAS, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDAS)
	public Response<DoctorConferenceAgenda> getConferenceAgenda(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") boolean discarded,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "fromtime", defaultValue = "0") Integer fromtime,
			@RequestParam(required = false, value = "toTime", defaultValue = "0") Integer toTime,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@PathVariable("conferenceId") String conferenceId) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		List<DoctorConferenceAgenda> agenda = conferenceService.getConferenceAgenda(size, page, discarded, fromDate,
				toDate, searchTerm, conferenceId);
		Response<DoctorConferenceAgenda> response = new Response<DoctorConferenceAgenda>();
		response.setDataList(agenda);
		return response;
	}

	@DeleteMapping(value = PathProxy.ConferenceUrls.DELETE_CONFERENCE_AGENDA)
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_CONFERENCE_AGENDA, notes = PathProxy.ConferenceUrls.DELETE_CONFERENCE_AGENDA)
	public Response<DoctorConferenceAgenda> deleteConferenceAgenda(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConferenceAgenda agenda = conferenceService.deleteConferenceAgenda(id, discarded);
		Response<DoctorConferenceAgenda> response = new Response<DoctorConferenceAgenda>();
		response.setData(agenda);
		return response;

	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDA)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDA, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDA)
	public Response<DoctorConferenceAgenda> getConferenceAgenda(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConferenceAgenda agenda = conferenceService.getConferenceAgenda(id);

		Response<DoctorConferenceAgenda> response = new Response<DoctorConferenceAgenda>();
		response.setData(agenda);

		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.UPLOAD_CONFERENCE_IMAGE)
	@ApiOperation(value = PathProxy.ConferenceUrls.UPLOAD_CONFERENCE_IMAGE, notes = PathProxy.ConferenceUrls.UPLOAD_CONFERENCE_IMAGE)
	public Response<String> uploadImages(@RequestBody FileDetails request,
			@RequestParam(required = false, value = "module", defaultValue = "conference") String module) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		String image = conferenceService.uploadImages(request, module);
		Response<String> response = new Response<String>();
		response.setData(image);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION_DATES)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION_DATES, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION_DATES)
	public Response<SessionDateResponse> getConferenceSessionDate(@PathVariable("conferenceId") String conferenceId) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		List<SessionDateResponse> dates = conferenceService.getConferenceSessionDate(conferenceId);

		Response<SessionDateResponse> response = new Response<SessionDateResponse>();
		response.setDataList(dates);

		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_CONFERENCE_ADMIN)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_CONFERENCE_ADMIN, notes = PathProxy.ConferenceUrls.ADD_CONFERENCE_ADMIN)
	public Response<DoctorConference> addConferenceAdmin(@RequestBody ConfexAdminRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getFirstName(), request.getConferenceId(),
				request.getEmailAddress())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConference conference = conferenceService.addConferenceAdmin(request);

		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setData(conference);

		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.RESET_PASSWORD)
	@ApiOperation(value = PathProxy.ConferenceUrls.RESET_PASSWORD, notes = PathProxy.ConferenceUrls.RESET_PASSWORD)
	public Response<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (request.getPassword() == null || request.getPassword().length == 0) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getUserName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean status = conferenceService.resetPassword(request);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.ACTIVATE_CONFERENCE_USER)
	@ApiOperation(value = PathProxy.ConferenceUrls.ACTIVATE_CONFERENCE_USER, notes = PathProxy.ConferenceUrls.ACTIVATE_CONFERENCE_USER)
	public Response<Boolean> activateConferenceAdmin(@PathVariable("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean status = conferenceService.activeConferenceAdmin(userId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.EMAIL_RESET_PASSWORD)
	@ApiOperation(value = PathProxy.ConferenceUrls.EMAIL_RESET_PASSWORD, notes = PathProxy.ConferenceUrls.EMAIL_RESET_PASSWORD)
	public Response<Boolean> mailSetPassword(@PathVariable("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean status = conferenceService.sendEmailSetPassword(userId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.SEND_SMS_VERIFY)
	@ApiOperation(value = PathProxy.ConferenceUrls.SEND_SMS_VERIFY, notes = PathProxy.ConferenceUrls.SEND_SMS_VERIFY)
	public Response<Boolean> resendVerificationSMS(@PathVariable("userId") String userId,
			@PathVariable("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(userId, mobileNumber)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean status = conferenceService.resendVerificationSMS(userId, mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.VERIFY_CONFERENCE)
	@ApiOperation(value = PathProxy.ConferenceUrls.VERIFY_CONFERENCE, notes = PathProxy.ConferenceUrls.VERIFY_CONFERENCE)
	public Response<Boolean> verifyAdmin(@PathVariable("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean status = conferenceService.verifyAdmin(userId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.SEND_RESET_PASSWORD_SMS)
	@ApiOperation(value = PathProxy.ConferenceUrls.SEND_RESET_PASSWORD_SMS, notes = PathProxy.ConferenceUrls.SEND_RESET_PASSWORD_SMS)
	public Response<Boolean> resetPasswordSms(@PathVariable("userId") String userId,
			@PathVariable("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(userId, mobileNumber)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean status = conferenceService.resetPasswordSMS(userId, mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.UPDATE_STATUS_CONFERENCE)
	@ApiOperation(value = PathProxy.ConferenceUrls.UPDATE_STATUS_CONFERENCE, notes = PathProxy.ConferenceUrls.UPDATE_STATUS_CONFERENCE)
	public Response<DoctorConference> updateStatus(@PathVariable("id") String id,
			@PathVariable("status") String status) {
		if (DPDoctorUtils.anyStringEmpty(id, status)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConference conference = conferenceService.updateStatus(id, status);
		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setData(conference);
		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_EDIT_SUB_CATEGORY)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_SUB_CATEGORY, notes = PathProxy.ConferenceUrls.ADD_EDIT_SUB_CATEGORY)
	public Response<ResearchPaperSubCategory> addResearchPaperSubCategory(
			@RequestBody ResearchPaperSubCategory request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getSubCategory(), request.getConferenceId())) {
			logger.warn("Category ,conferenceId should not be Null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "Category,conferenceId should not be Null or empty");
		}

		ResearchPaperSubCategory researchPaperSubCategory = conferenceService.addSubCategory(request);
		Response<ResearchPaperSubCategory> response = new Response<ResearchPaperSubCategory>();
		response.setData(researchPaperSubCategory);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_SUB_CATEGORY)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SUB_CATEGORY, notes = PathProxy.ConferenceUrls.GET_SUB_CATEGORY)
	public Response<ResearchPaperSubCategory> getResearchPaperSubCategory(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ResearchPaperSubCategory researchPaperSubCategory = conferenceService.getSubCategory(id);
		Response<ResearchPaperSubCategory> response = new Response<ResearchPaperSubCategory>();
		response.setData(researchPaperSubCategory);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_SUB_CATEGORIES)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SUB_CATEGORIES, notes = PathProxy.ConferenceUrls.GET_SUB_CATEGORIES)
	public Response<ResearchPaperSubCategory> getSubCaterories(@PathVariable("conferenceId") String conferenceId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ResearchPaperSubCategory> paperResponses = null;

		paperResponses = conferenceService.getSubCategoryList(page, size, conferenceId, searchTerm, discarded);
		Response<ResearchPaperSubCategory> response = new Response<ResearchPaperSubCategory>();
		response.setDataList(paperResponses);
		return response;
	}

	@DeleteMapping(value = PathProxy.ConferenceUrls.DELETE_SUB_CATEGORY)
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_SUB_CATEGORY, notes = PathProxy.ConferenceUrls.DELETE_SUB_CATEGORY)
	public Response<Boolean> deleteSubCategory(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Boolean status = conferenceService.discardSubCategory(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.ADD_EDIT_CATEGORY)
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_CATEGORY, notes = PathProxy.ConferenceUrls.ADD_EDIT_CATEGORY)
	public Response<ResearchPaperCategory> addResearchPaperCategory(@RequestBody ResearchPaperCategory request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getCategory(), request.getConferenceId())) {
			logger.warn("Category ,conferenceId should not be Null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "Category,conferenceId should not be Null or empty");
		}

		ResearchPaperCategory researchPaperCategory = conferenceService.addCategory(request);
		Response<ResearchPaperCategory> response = new Response<ResearchPaperCategory>();
		response.setData(researchPaperCategory);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CATEGORY)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CATEGORY, notes = PathProxy.ConferenceUrls.GET_CATEGORY)
	public Response<ResearchPaperCategory> getResearchPaperCategory(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ResearchPaperCategory researchPaperCategory = conferenceService.getCategory(id);
		Response<ResearchPaperCategory> response = new Response<ResearchPaperCategory>();
		response.setData(researchPaperCategory);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CONFERENCE_USER)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_USER, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_USER)
	@ResponseBody
	public Response<RegisteredUser> getRegisteredUser(@PathVariable("conferenceId") String conferenceId,
			@RequestParam(value = "size", defaultValue = "0") int size,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "paymentStatus", required = false) Boolean paymentStatus,
			@RequestParam(value = "searchTerm", required = false) String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		List<RegisteredUser> dates = null;
		Integer count = conferenceService.countRegisterUser(fromDate, toDate, paymentStatus, conferenceId, searchTerm);
		if (count > 0)
			dates = conferenceService.getRegisterUser(size, page, fromDate, toDate, paymentStatus, conferenceId,
					searchTerm);
		Response<RegisteredUser> response = new Response<RegisteredUser>();
		response.setDataList(dates);
		response.setCount(count);
		return response;
	}

	@GetMapping(value = PathProxy.ConferenceUrls.GET_CATEGORIES)
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CATEGORIES, notes = PathProxy.ConferenceUrls.GET_CATEGORIES)
	public Response<ResearchPaperCategory> getCaterories(@PathVariable("conferenceId") String conferenceId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ResearchPaperCategory> paperResponses = null;

		paperResponses = conferenceService.getCategoryList(page, size, conferenceId, searchTerm, discarded);
		Response<ResearchPaperCategory> response = new Response<ResearchPaperCategory>();
		response.setDataList(paperResponses);
		return response;
	}

	@DeleteMapping(value = PathProxy.ConferenceUrls.DELETE_CATEGORY)
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_CATEGORY, notes = PathProxy.ConferenceUrls.DELETE_CATEGORY)
	public Response<Boolean> deleteCategory(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Boolean status = conferenceService.discardCategory(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@PostMapping(value = PathProxy.ConferenceUrls.SEND_CONFERENCE_USER_BULK_SMS)
	@ApiOperation(value = PathProxy.ConferenceUrls.SEND_CONFERENCE_USER_BULK_SMS, notes = PathProxy.ConferenceUrls.SEND_CONFERENCE_USER_BULK_SMS)
	public Response<Boolean> addResearchPaperCategory(@RequestBody ConferenceBulkSMSRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getMessage(), request.getConferenceId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Massage or Conference should not empty or null");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(conferenceService.sendBulkSMS(request));
		return response;
	}
}
