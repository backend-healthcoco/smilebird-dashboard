package com.dpdocter.services;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Zone;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.DentalChainAppointmentRequest;
import com.dpdocter.response.ConsultationSpeciality;
import com.dpdocter.response.PatientAppUsersResponse;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.WebAppointmentSlotDataResponse;

import common.util.web.Response;

public interface AppointmentService {

	City addCity(City city);

	Boolean activateDeactivateCity(String cityId, boolean activate);

	Response<Object> getCities(int size, int page, String state, String searchTerm,Boolean isDiscarded);

	City getCity(String cityId);

	LandmarkLocality addLandmaklLocality(LandmarkLocality locality);

	Clinic getClinic(String locationId);

	Lab getLab(String locationId);

	List<City> getCountries();

	List<City> getStates(String country);

	Boolean cancelAppointment(String appointmentId);

	public Response<Object> getLandmarkLocality(int size, int page, String cityId, String searchTerm,Boolean isDiscarded);
	
	public Boolean verifyClinicDocument(String doctorId, Boolean isClinicOwnershipVerified, String locationId);

	Boolean deleteCityById(String cityId, Boolean isDiscarded);

	Boolean deleteLocalityById(String localityId, Boolean isDiscarded);

	Integer getCountriesCount();

	Integer getStatesCount(String country);

	Appointment addAppointment(AppointmentRequest request, boolean isFormattedResponseRequired);

	Integer getUsersCount(int size, int page, String searchTerm,String mobileNumber);

	List<PatientAppUsersResponse> getUsers(int size, int page, String searchTerm,String mobileNumber);

	
	Response<Appointment> getAppointments(String locationId,String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String status, String sortBy, String fromTime,
			String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb, Boolean discarded, String branch,Boolean isAnonymousAppointment,String type);

	
	Appointment updateAppointment(AppointmentRequest request, Boolean isStatusChange);

	Appointment getAppointmentById(ObjectId appointmentId);

	Response<ConsultationSpeciality> getConsultationWithSpeciality(String locationId, String doctorId, String patientId,
			String from, String to, int page, int size, String updatedTime, String status, String sortBy,
			String fromTime, String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb, Boolean discarded,
			String branch, Boolean isAnonymousAppointment, String type);

	Response<Object> getUsersNew(int size, int page, String searchTerm, String mobileNumber,Boolean isDentalChainPatient);

	Response<Object> getUsersFromUserCl(int size, int page, String searchTerm, String mobileNumber,
			Boolean isDentalChainPatient);

	Zone addZone(Zone request);

	Response<Object> getZone(int size, int page, String zoneId, String searchTerm, Boolean isDiscarded);

	Boolean deleteZoneById(String zoneId, Boolean isDiscarded);

}
