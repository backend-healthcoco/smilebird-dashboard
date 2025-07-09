package com.dpdocter.webservices;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.DentalChainAppointmentRequest;
import com.dpdocter.response.ConsultationSpeciality;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.APPOINTMENT_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class AppointmentApi {

	private static Logger logger = LogManager.getLogger(AppointmentApi.class.getName());

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

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
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = appointmentService.getCities(size, page, state, searchTerm, isDiscarded);
//		Response<City> response = new Response<City>();
//		response.setDataList(cities);
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

	@GetMapping(value = PathProxy.AppointmentUrls.GET_CLINIC)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_CLINIC, notes = PathProxy.AppointmentUrls.GET_CLINIC)
	public Response<Clinic> getClinic(@PathVariable(value = "locationId") String locationId) {

		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Location Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
		}
		Clinic clinic = appointmentService.getClinic(locationId);
		Response<Clinic> response = new Response<Clinic>();
		response.setData(clinic);
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
//		Response<LandmarkLocality> response = new Response<LandmarkLocality>();
//		response.setDataList(locality);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_LAB)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_LAB, notes = PathProxy.AppointmentUrls.GET_LAB)
	public Response<Lab> getLabs(@PathVariable("locationId") String locationId) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Location Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
		}
		Lab lab = appointmentService.getLab(locationId);
		Response<Lab> response = new Response<Lab>();
		response.setData(lab);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.CANCEL_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.CANCEL_APPOINTMENT, notes = PathProxy.AppointmentUrls.CANCEL_APPOINTMENT)
	public Response<Boolean> updateState(@PathVariable("appointmentId") String appointmentId) {
		Boolean appointmentResponse = appointmentService.cancelAppointment(appointmentId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(appointmentResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.UPLOAD_LANDMARK_LOCALITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.UPLOAD_LANDMARK_LOCALITY, notes = PathProxy.AppointmentUrls.UPLOAD_LANDMARK_LOCALITY)
	public Response<Boolean> uploadLandmaklLocality(@RequestParam(required = false, value = "cityId") String cityId,
			@RequestParam(required = false, value = "path") String path) throws IOException {
		LandmarkLocality request = null;
		Criteria criteria = new Criteria("cityId").is(cityId);
		elasticsearchTemplate.delete(new CriteriaQuery(criteria), ESLandmarkLocalityDocument.class);
		BufferedReader br = new BufferedReader(new FileReader("/home/ubuntu/" + path + ".csv"));
		String line = "";
		while ((line = br.readLine()) != null) {
			String[] b = line.split(",");
			request = new LandmarkLocality();
			request.setCityId(cityId);
			request.setLocality(b[0].replaceAll("\"", ""));
			request.setLatitude(Double.parseDouble(b[1].replaceAll(" ", "")));
			request.setLatitude(Double.parseDouble(b[2].replaceAll(" ", "")));
			LandmarkLocality locality = appointmentService.addLandmaklLocality(request);
			transnationalService.addResource(new ObjectId(request.getId()), Resource.LANDMARKLOCALITY, false);
			ESLandmarkLocalityDocument esLandmarkLocalityDocument = new ESLandmarkLocalityDocument();
			BeanUtil.map(locality, esLandmarkLocalityDocument);
			esLandmarkLocalityDocument.setGeoPoint(new GeoPoint(locality.getLatitude(), locality.getLongitude()));
			esCityService.addLocalityLandmark(esLandmarkLocalityDocument);

		}
		br.close();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.VERIFY_CLINIC_DOCUMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.VERIFY_CLINIC_DOCUMENT, notes = PathProxy.AppointmentUrls.VERIFY_CLINIC_DOCUMENT)
	public Response<Boolean> verifyClinicDocument(
			@RequestParam(required = true, value = "isClinicOwnershipVerified") Boolean isClinicOwnershipVerified,
			@RequestParam(required = true, value = "doctorId") String doctorId,
			@RequestParam(required = true, value = "locationId") String locationId) {

		Boolean clinicResponse = appointmentService.verifyClinicDocument(doctorId, isClinicOwnershipVerified,
				locationId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(clinicResponse);
		return response;

	}

	@PostMapping(value = PathProxy.AppointmentUrls.ADD_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.ADD_APPOINTMENT, notes = PathProxy.AppointmentUrls.ADD_APPOINTMENT)
	public Response<Appointment> BookAppoinment(@RequestBody AppointmentRequest request,
			@RequestParam(required = false, value = "isStatusChange", defaultValue = "false") Boolean isStatusChange)
			throws MessagingException {
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
			appointment = appointmentService.addAppointment(request, true);
		} else {
			throw new BusinessException(ServiceError.InvalidInput, "Can't Update from admin ");
		}

		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_APPOINTMENT, notes = PathProxy.AppointmentUrls.GET_APPOINTMENT)
	public Response<Appointment> getDoctorAppointments(
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "patientId") String patientId,
			@RequestParam(required = false, value = "from") String from,
			@RequestParam(required = false, value = "to") String to,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "status") String status,
			@RequestParam(required = false, value = "sortBy") String sortBy,
			@RequestParam(required = false, value = "fromTime") String fromTime,
			@RequestParam(required = false, value = "toTime") String toTime,
			@RequestParam(required = false, value = "isRegisteredPatientRequired", defaultValue = "false") Boolean isRegisteredPatientRequired,
			@RequestParam(required = false, value = "isWeb", defaultValue = "false") Boolean isWeb,
			@RequestParam(required = false, value = "discarded", defaultValue = "false") Boolean discarded,
			@RequestParam(required = false, value = "type") String type,
			@RequestParam(required = false, value = "branch") String branch,
			@RequestParam(required = false, value = "isAnonymousAppointment", defaultValue = "false") Boolean isAnonymousAppointment) {

		Response<Appointment> response = appointmentService.getAppointments(locationId, doctorId, patientId, from, to,
				page, size, updatedTime, status, sortBy, fromTime, toTime, isRegisteredPatientRequired, isWeb,
				discarded, branch, isAnonymousAppointment, type);
		return response;
	}
	
	// get patients from patient app from patient collection
	@GetMapping(value = PathProxy.AppointmentUrls.GET_PATIENTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENTS, notes = PathProxy.AppointmentUrls.GET_PATIENTS)
	public Response<Object> getPatients(@RequestParam(required = false, value = "size", defaultValue = "10") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm,
			@RequestParam(required = false, value = "mobileNumber") String mobileNumber,
			@RequestParam(required = false, value = "isDentalChainPatient",defaultValue = "false") Boolean isDentalChainPatient) {
	//	Integer count = appointmentService.getUsersCount(size, page, searchTerm,mobileNumber);
		Response<Object> response = appointmentService.getUsersNew(size, page, searchTerm,mobileNumber,isDentalChainPatient);
	//	Response<PatientAppUsersResponse> response = new Response<PatientAppUsersResponse>();
		//response.setDataList(patientList);
		//response.setCount(count);
		return response;
	}
	
	// get patients from patient app from user collection
		@GetMapping(value = PathProxy.AppointmentUrls.GET_USERS)
		@ApiOperation(value = PathProxy.AppointmentUrls.GET_USERS, notes = PathProxy.AppointmentUrls.GET_USERS)
		public Response<Object> getUsers(@RequestParam(required = false, value = "size", defaultValue = "10") int size,
				@RequestParam(required = false, value = "page", defaultValue = "0") int page,
				@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm,
				@RequestParam(required = false, value = "mobileNumber") String mobileNumber,
				@RequestParam(required = false, value = "isDentalChainPatient",defaultValue = "false") Boolean isDentalChainPatient) {
		//	Integer count = appointmentService.getUsersCount(size, page, searchTerm,mobileNumber);
			Response<Object> response = appointmentService.getUsersFromUserCl(size, page, searchTerm,mobileNumber,isDentalChainPatient);
		//	Response<PatientAppUsersResponse> response = new Response<PatientAppUsersResponse>();
			//response.setDataList(patientList);
			//response.setCount(count);
			return response;
		}
	

	// Get online appointment slot api for fetching available slots of doctor
	@GetMapping(value = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS, notes = PathProxy.AppointmentUrls.GET_ONLINE_CONSULTATION_TIME_SLOTS)
	public Response<SlotDataResponse> getOnlineConsultationTimeSlots(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value = "type") String type, @PathVariable("date") String date,
			@RequestParam(required = false, value = "isPatient", defaultValue = "true") Boolean isPatient)
			throws MessagingException {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		Date dateObj = new Date(Long.parseLong(date));
		SlotDataResponse slotDataResponse = appointmentService.getOnlineConsultationTimeSlots(doctorId, type, dateObj,
				isPatient);
		Response<SlotDataResponse> response = new Response<SlotDataResponse>();
		response.setData(slotDataResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT)
	@ApiOperation(value = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT, notes = PathProxy.AppointmentUrls.UPDATE_APPOINTMENT)
	public Response<Appointment> UpdateAppoinment(@RequestBody AppointmentRequest request,
			@RequestParam(required = false, value = "isStatusChange", defaultValue = "false") Boolean isStatusChange)
			throws MessagingException {
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
		if (request.getAppointmentId() != null) {
			appointment = appointmentService.updateAppointment(request, isStatusChange);
		}

		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;

	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_APPOINTMENT_ID)
	@ApiOperation(value = "GET_APPOINTMENT_ID", notes = "GET_APPOINTMENT_ID")
	public Response<Appointment> getAppointmentById(@PathVariable(value = "appointmentId") String appointmentId)
			throws MessagingException {
		if (DPDoctorUtils.anyStringEmpty(appointmentId)) {
			logger.warn("Invalid Input");
			// mailService.sendExceptionMail("Invalid input :: AppointmentId cannot be
			// null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Appointment appointment = appointmentService.getAppointmentById(new ObjectId(appointmentId));
		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;
	}

	@GetMapping(value = PathProxy.AppointmentUrls.GET_APPOINTMENT_WITH_SPECIALITY)
	@ApiOperation(value = PathProxy.AppointmentUrls.GET_APPOINTMENT_WITH_SPECIALITY, notes = PathProxy.AppointmentUrls.GET_APPOINTMENT_WITH_SPECIALITY)
	public Response<ConsultationSpeciality> getConsultationWithSpeciality(
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "patientId") String patientId,
			@RequestParam(required = false, value = "from") String from,
			@RequestParam(required = false, value = "to") String to,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "status") String status,
			@RequestParam(required = false, value = "sortBy") String sortBy,
			@RequestParam(required = false, value = "fromTime") String fromTime,
			@RequestParam(required = false, value = "toTime") String toTime,
			@RequestParam(required = false, value = "isRegisteredPatientRequired", defaultValue = "false") Boolean isRegisteredPatientRequired,
			@RequestParam(required = false, value = "isWeb", defaultValue = "false") Boolean isWeb,
			@RequestParam(required = false, value = "discarded", defaultValue = "false") Boolean discarded,
			@RequestParam(required = false, value = "type") String type,
			@RequestParam(required = false, value = "branch") String branch,
			@RequestParam(required = false, value = "isAnonymousAppointment", defaultValue = "false") Boolean isAnonymousAppointment) {

		Response<ConsultationSpeciality> response = appointmentService.getConsultationWithSpeciality(locationId,
				doctorId, patientId, from, to, page, size, updatedTime, status, sortBy, fromTime, toTime,
				isRegisteredPatientRequired, isWeb, discarded, branch, isAnonymousAppointment, type);
		return response;
	}

}