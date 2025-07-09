package com.dpdocter.webservices.v3;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
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

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.BroadcastByTreatmentRequest;
import com.dpdocter.request.DentalChainAppointmentRequest;
import com.dpdocter.response.PatientTimelineHistoryResponse;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.WebAppointmentSlotDataResponse;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.APPOINTMENT_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class AppointmentV3Api {

	private static Logger logger = LogManager.getLogger(AppointmentV3Api.class.getName());

	@Autowired
	private AppointmentV3Service appointmentService;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CLINIC)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CLINIC, notes = PathProxy.AppointmentUrls.GET_CLINIC)
	public Response<Clinic> getDentalClinicById(@PathVariable(value = "locationId") String locationId,
			@RequestParam(required = false, value = "active", defaultValue = "false") Boolean active)
			throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Location Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
		}
		Clinic clinic = appointmentService.getDentalClinicById(locationId, active);
		Response<Clinic> response = new Response<Clinic>();
		response.setData(clinic);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS, notes = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
	public Response<SlotDataResponse> getTimeSlots(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("date") String date,
			@RequestParam(required = false, value = "isPatient", defaultValue = "true") Boolean isPatient)
			throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		Date dateObj = new Date(Long.parseLong(date));
		SlotDataResponse slotDataResponse = appointmentService.getTimeSlots(doctorId, locationId, dateObj, isPatient);
		Response<SlotDataResponse> response = new Response<SlotDataResponse>();
		response.setData(slotDataResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.DENTAL_CHAIN_BOOK_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.DENTAL_CHAIN_BOOK_APPOINTMENT, notes = PathProxy.AppointmentUrls.DENTAL_CHAIN_BOOK_APPOINTMENT)
	public Response<Appointment> bookAppointmentForDentalChain(@RequestBody DentalChainAppointmentRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid input :: Doctor Id ,Location Id or Hostipal Id cannot be empty");
		} else if (request.getTime() != null && (request.getTime().getFromTime() > request.getTime().getToTime())) {
			logger.warn("Invalid Time");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Time");
		} else if (request.getTime() != null
				&& ((request.getTime().getToTime() - request.getTime().getFromTime()) > 120)) {
			logger.warn("Invalid Time");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Time : Appointment duration cannot be greater than 120 mins");
		}

		Appointment appointment = null;
		if (request.getAppointmentId() == null) {
			appointment = appointmentService.addAppointmentForDentalChain(request);
		} else {
			appointment = appointmentService.updateAppointment(request);
		}

		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY, notes = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
	public Response<Boolean> activateCity(@PathVariable(value = "cityId") String cityId,
			@RequestParam(required = false, value = "activate", defaultValue = "true") Boolean activate) {
		if (cityId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean isActivated = false;
		isActivated = appointmentService.activateDeactivateCity(cityId, activate);
		transnationalService.addResource(new ObjectId(cityId), Resource.CITY, false);
		esCityService.activateDeactivateCity(cityId, activate);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(isActivated);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_COUNTRIES)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_COUNTRIES, notes = PathProxy.AppointmentUrls.GET_COUNTRIES)
	public Response<City> getCountries() {
		Integer count = appointmentService.getCountriesCount();
		List<City> countries = appointmentService.getCountries();
		Response<City> response = new Response<City>();
		response.setCount(count);
		response.setDataList(countries);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_STATES)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_STATES, notes = PathProxy.AppointmentUrls.GET_STATES)
	public Response<City> getStates(@RequestParam(required = false, value = "country") String country) {
		Integer count = appointmentService.getStatesCount(country);
		List<City> states = appointmentService.getStates(country);
		Response<City> response = new Response<City>();
		response.setDataList(states);
		response.setCount(count);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY, notes = PathProxy.AppointmentUrls.GET_CITY)
	public Response<Object> getCities(@RequestParam(required = false, value = "state") String state,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "isActivated") Boolean isActivated,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = appointmentService.getCities(size, page, state, searchTerm, isDiscarded,
				isActivated);
		return response;
	}

	@DeleteMapping(value = PathProxy.AppointmentUrls.DELETE_CITY_BY_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.DELETE_CITY_BY_ID, notes = PathProxy.AppointmentUrls.DELETE_CITY_BY_ID)
	public Response<Boolean> deleteCityById(@PathVariable(value = "cityId") String cityId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(cityId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(appointmentService.deleteCityById(cityId, isDiscarded));
		return response;
	}

	@DeleteMapping(value = PathProxy.AppointmentUrls.DELETE_LOCALITY_BY_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.DELETE_LOCALITY_BY_ID, notes = PathProxy.AppointmentUrls.DELETE_LOCALITY_BY_ID)
	public Response<Boolean> deleteLocalityById(@PathVariable(value = "localityId") String localityId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(localityId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(appointmentService.deleteLocalityById(localityId, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CITY_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY_ID, notes = PathProxy.AppointmentUrls.GET_CITY_ID)
	public Response<City> getCityById(@PathVariable(value = "cityId") String cityId) {
		if (cityId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		City cities = appointmentService.getCity(cityId);
		Response<City> response = new Response<City>();
		response.setData(cities);
		return response;

	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY, notes = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
	public Response<LandmarkLocality> addLandmaklLocality(@RequestBody LandmarkLocality request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LandmarkLocality locality = appointmentService.addLandmaklLocality(request);
		transnationalService.addResource(new ObjectId(request.getId()), Resource.LANDMARKLOCALITY, false);
		ESLandmarkLocalityDocument esLandmarkLocalityDocument = new ESLandmarkLocalityDocument();
		BeanUtil.map(locality, esLandmarkLocalityDocument);
		esLandmarkLocalityDocument.setGeoPoint(new GeoPoint(locality.getLatitude(), locality.getLongitude()));
		esCityService.addLocalityLandmark(esLandmarkLocalityDocument);
		Response<LandmarkLocality> response = new Response<LandmarkLocality>();
		response.setData(locality);
		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_CITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_CITY, notes = PathProxy.AppointmentUrls.ADD_CITY)
	public Response<City> addCity(@RequestBody City request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		City city = appointmentService.addCity(request);

		ESCityDocument esCityDocument = new ESCityDocument();
		BeanUtil.map(city, esCityDocument);
		esCityDocument.setGeoPoint(new GeoPoint(city.getLatitude(), city.getLongitude()));
		esCityService.addCities(esCityDocument);

		Response<City> response = new Response<City>();
		response.setData(city);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_LANDMARK_LOCALITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_LANDMARK_LOCALITY, notes = PathProxy.AppointmentUrls.GET_LANDMARK_LOCALITY)
	public Response<Object> getLandmarkLocality(@PathVariable(value = "cityId") String cityId,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {

		if (DPDoctorUtils.anyStringEmpty(cityId)) {
			logger.warn("city Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "city Id cannot be empty");
		}
		Response<Object> response = appointmentService.getLandmarkLocality(size, page, cityId, searchTerm, isDiscarded);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_DENTAL_CHAIN_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_DENTAL_CHAIN_APPOINTMENT, notes = PathProxy.AppointmentUrls.GET_DENTAL_CHAIN_APPOINTMENT)
	@ResponseBody
	public Response<Appointment> getDoctorAppointments(
			@RequestParam(required = false, value = "locationId") String locationId,
			@MatrixVariable(required = false, value = "doctorIds") List<String> doctorIds,
			@RequestParam(value = "doctorId", required = false) List<String> doctorId,
			@RequestParam(required = false, value = "patientId") String patientId,
			@RequestParam(required = false, value = "smileBuddyId") String smileBuddyId,
			@RequestParam(required = false, value = "treatmentId") List<String> treatmentId,
			@RequestParam(required = false, value = "from") String from,
			@RequestParam(required = false, value = "to") String to,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "status") String status,
			@RequestParam(required = false, value = "state") String state,
			@RequestParam(required = false, value = "sortBy") String sortBy,
			@RequestParam(required = false, value = "fromTime") String fromTime,
			@RequestParam(required = false, value = "toTime") String toTime,
			@RequestParam(required = false, value = "isRegisteredPatientRequired", defaultValue = "false") Boolean isRegisteredPatientRequired,
			@RequestParam(required = false, value = "type") String type) {
		Response<Appointment> response = appointmentService.getAppointments(locationId, doctorId, patientId,
				treatmentId, from, to, page, size, updatedTime, status, state, sortBy, fromTime, toTime,
				isRegisteredPatientRequired, type, searchTerm, smileBuddyId);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.UPDATE_DENTAL_CHAIN_APPOINTMENT_STATE)
	@ApiOperation(value = PathProxy.AppointmentUrls.UPDATE_DENTAL_CHAIN_APPOINTMENT_STATE, notes = PathProxy.AppointmentUrls.UPDATE_DENTAL_CHAIN_APPOINTMENT_STATE)
	public Response<Boolean> updateAppointmentState(@PathVariable(value = "appointmentId") String appointmentId,
			@RequestParam(required = false, value = "state") String state,
			@RequestParam(required = false, value = "userId") String userId) {

		Boolean stateResponse = appointmentService.updateAppointmentState(appointmentId, state, userId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(stateResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.AppointmentUrls.UPDATE_DENTAL_CHAIN_APPOINTMENT_STATUS)
	@ApiOperation(value = PathProxy.AppointmentUrls.UPDATE_DENTAL_CHAIN_APPOINTMENT_STATUS, notes = PathProxy.AppointmentUrls.UPDATE_DENTAL_CHAIN_APPOINTMENT_STATUS)
	public Response<Boolean> updateAppointmentStatus(@PathVariable(value = "appointmentId") String appointmentId,
			@RequestParam(required = false, value = "status") String status,
			@RequestParam(required = false, value = "userId") String userId) {

		Boolean stateResponse = appointmentService.updateAppointmentStatus(appointmentId, status, userId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(stateResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS_WEB)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS_WEB, notes = PathProxy.AppointmentUrls.GET_TIME_SLOTS_WEB)
	public Response<WebAppointmentSlotDataResponse> getTimeSlotsWeb(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("date") String date,
			@RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "isPatient", defaultValue = "true") Boolean isPatient)
			throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		Date dateObj = new Date(Long.parseLong(date));
		WebAppointmentSlotDataResponse slotDataResponse = appointmentService.getTimeSlotsWeb(doctorId, locationId,
				hospitalId, date);
		Response<WebAppointmentSlotDataResponse> response = new Response<WebAppointmentSlotDataResponse>();
		response.setData(slotDataResponse);
		return response;
	}

	// get patients from patient app from user collection
	@GetMapping(value = PathProxy.AppointmentUrls.GET_USERS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_USERS, notes = PathProxy.AppointmentUrls.GET_USERS)
	public Response<Object> getUsers(@RequestParam(required = false, value = "locality") String locality,
			@RequestParam(required = false, value = "language") String language,
			@RequestParam(value = "treatmentId", required = false) List<String> treatmentId,
			@RequestParam(required = false, value = "isMobileNumberPresent") Boolean isMobileNumberPresent,
			@RequestParam(required = false, value = "followupType") String followupType,
			@RequestParam(required = false, value = "gender") String gender,
			@RequestParam(required = false, value = "age", defaultValue = "0") Integer age,
			@RequestParam(required = false, value = "complaint") String complaint,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "smileBuddyId") String smileBuddyId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm,
			@RequestParam(required = false, value = "mobileNumber") String mobileNumber,
			@RequestParam(required = false, value = "isDentalChainPatient", defaultValue = "false") Boolean isDentalChainPatient,
			@RequestParam(required = false, value = "dateFilterType") String dateFilterType,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate) {
		Response<Object> response = appointmentService.getUsersFromUserCl(size, page, searchTerm, mobileNumber,
				isDentalChainPatient, fromDate, toDate, dateFilterType, city, treatmentId, locality, language, gender,
				followupType, age, complaint, isDiscarded, isMobileNumberPresent, smileBuddyId);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_USERS_COUNT)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_USERS_COUNT, notes = PathProxy.AppointmentUrls.GET_USERS_COUNT)
	public Response<Object> getUsersCount(@RequestParam(required = false, value = "locality") String locality,
			@RequestParam(required = false, value = "language") String language,
			@RequestParam(value = "treatmentId", required = false) List<String> treatmentId,
			@RequestParam(required = false, value = "isMobileNumberPresent") Boolean isMobileNumberPresent,
			@RequestParam(required = false, value = "followupType") String followupType,
			@RequestParam(required = false, value = "gender") String gender,
			@RequestParam(required = false, value = "age", defaultValue = "0") Integer age,
			@RequestParam(required = false, value = "complaint") String complaint,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "smileBuddyId") String smileBuddyId,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm,
			@RequestParam(required = false, value = "mobileNumber") String mobileNumber,
			@RequestParam(required = false, value = "isDentalChainPatient", defaultValue = "false") Boolean isDentalChainPatient,
			@RequestParam(required = false, value = "dateFilterType") String dateFilterType,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate) {
		Response<Object> response = appointmentService.getUsersFromUserClCount(size, page, searchTerm, mobileNumber,
				isDentalChainPatient, fromDate, toDate, dateFilterType, city, treatmentId, locality, language, gender,
				followupType, age, complaint, isDiscarded, isMobileNumberPresent, smileBuddyId);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_DOCTORS_COUNT)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_DOCTORS_COUNT, notes = PathProxy.AppointmentUrls.GET_DOCTORS_COUNT)
	public Response<Object> getDoctorsCount(@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(value = "doctorId", required = false) List<String> doctorId,
			@RequestParam(required = false, value = "gender") String gender,
			@RequestParam(required = false, value = "city") String city) {
		Response<Object> response = appointmentService.getDoctorsCount(city, locationId, doctorId, gender);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT, notes = PathProxy.AppointmentUrls.GET_PATIENT_LAST_APPOINTMENT)
	public Response<Appointment> getPatientLastAppointment(@PathVariable(value = "patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Appointment appointment = appointmentService.getPatientLastAppointment(patientId);
		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_PATIENT_TIMELINE_HISTORY)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_TIMELINE_HISTORY, notes = PathProxy.AppointmentUrls.GET_PATIENT_TIMELINE_HISTORY)
	public Response<PatientTimelineHistoryResponse> getPatientTimelineHistory(
			@PathVariable(value = "patientId") String patientId,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<PatientTimelineHistoryResponse> appointment = appointmentService.getPatientTimelineHistory(locationId,
				doctorId, patientId);
		Response<PatientTimelineHistoryResponse> response = new Response<PatientTimelineHistoryResponse>();
		response.setDataList(appointment);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_DENTAL_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_DENTAL_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID, notes = PathProxy.AppointmentUrls.GET_DENTAL_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID)
	public Response<Object> getCampMsgTemplate(
			@RequestParam(required = false, value = "preTreatment") Boolean preTreatment,
			@RequestParam(required = false, value = "postTreatment") Boolean postTreatment,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(value = "treatmentId", required = false) List<String> treatmentId) {
		Response<Object> response = appointmentService.getCampMsgTemplateByTreatmentName(preTreatment, postTreatment,
				isDiscarded, treatmentId);
		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.BROADCAST_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID)
	@ApiOperation(value = PathProxy.AppointmentUrls.BROADCAST_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID, notes = PathProxy.AppointmentUrls.BROADCAST_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID)
	public Response<Boolean> broadcastByTreatmentToUser(@RequestBody BroadcastByTreatmentRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean sent = appointmentService.broadcastByTreatmentToUser(request);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(sent);
		return response;
	}
}