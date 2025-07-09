package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.beans.AppointmentSearchResponse;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.response.LabResponse;

public interface ESAppointmentService {

    List<ESDoctorDocument> getDoctors(int page, int size, String city, String location, String latitude, String longitude, String speciality, String symptom,
	    Boolean booking, Boolean calling, int minFee, int maxFee, int minTime, int maxTime, List<String> days, String gender,
	    int minExperience, int maxExperience);

    List<AppointmentSearchResponse> search(String city, String location, String latitude, String longitude, String searchTerm);

    List<LabResponse> getLabs(int page, int size, String city, String location, String latitude, String longitude, String test, Boolean booking, Boolean calling, int minTime, int maxTime, List<String> days);

	Boolean sendSMSToDoctors();

	Boolean sendSMSToLocale();

}
