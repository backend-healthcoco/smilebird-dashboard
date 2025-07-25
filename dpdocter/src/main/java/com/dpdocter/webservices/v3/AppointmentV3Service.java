package com.dpdocter.webservices.v3;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Zone;
import com.dpdocter.request.BroadcastByTreatmentRequest;
import com.dpdocter.request.DentalChainAppointmentRequest;
import com.dpdocter.response.PatientTimelineHistoryResponse;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.WebAppointmentSlotDataResponse;

import common.util.web.Response;

public interface AppointmentV3Service {

	Clinic getDentalClinicById(String locationId, Boolean active);

	SlotDataResponse getTimeSlots(String doctorId, String locationId, Date dateObj, Boolean isPatient);

	City addCity(City city);

	Boolean activateDeactivateCity(String cityId, boolean activate);

	Response<Object> getCities(int size, int page, String state, String searchTerm, Boolean isDiscarded,
			Boolean isActivated);

	City getCity(String cityId);

	LandmarkLocality addLandmaklLocality(LandmarkLocality locality);

	Zone addZone(Zone zone);

	Clinic getClinic(String locationId);

	List<City> getCountries();

	List<City> getStates(String country);

	Appointment addAppointmentForDentalChain(DentalChainAppointmentRequest request);

	Appointment updateAppointment(DentalChainAppointmentRequest request);

	Boolean deleteCityById(String cityId, Boolean isDiscarded);

	Boolean deleteLocalityById(String localityId, Boolean isDiscarded);

	Integer getCountriesCount();

	Integer getStatesCount(String country);

	public Response<Object> getLandmarkLocality(int size, int page, String cityId, String searchTerm,
			Boolean isDiscarded);

	Boolean updateAppointmentState(String appointmentId, String state, String userId);

	WebAppointmentSlotDataResponse getTimeSlotsWeb(String doctorId, String locationId, String hospitalId, String date);

	Response<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId,List<String> treatmentId, String from,
			String to, int page, int size, String updatedTime, String status, String state, String sortBy,
			String fromTime, String toTime, Boolean isRegisteredPatientRequired, String type,String searchTerm,String smileBuddyId);

	Response<Object> getUsersFromUserCl(int size, int page, String searchTerm, String mobileNumber,
			Boolean isDentalChainPatient, String fromDate, String toDate, String dateFilterType, String city,
			List<String> treatmentId, String locality, String language, String gender, String followupType, Integer age,
			String complaint, Boolean isDiscarded,Boolean isMobileNumberPresent,String smileBuddyId);

	Appointment getPatientLastAppointment(String locationId);

	List<PatientTimelineHistoryResponse> getPatientTimelineHistory(String locationId, String doctorId,

			String patientId);

	Response<Object> getCampMsgTemplateByTreatmentName(Boolean preTreatment, Boolean postTreatment, Boolean isDiscarded,
			List<String> treatmentId);

	Boolean broadcastByTreatmentToUser(BroadcastByTreatmentRequest request);

	Response<Object> getUsersFromUserClCount(int size, int page, String searchTerm, String mobileNumber,
			Boolean isDentalChainPatient, String fromDate, String toDate, String dateFilterType, String city,
			List<String> treatmentId, String locality, String language, String gender, String followupType, Integer age,
			String complaint, Boolean isDiscarded, Boolean isMobileNumberPresent,String smileBuddyId);

	Response<Object> getDoctorsCount(String city, String locationId, List<String> doctorId, String gender);

	Boolean updateAppointmentStatus(String appointmentId, String status, String userId);

}
