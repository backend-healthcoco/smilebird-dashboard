package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorAppFeedback;
import com.dpdocter.response.PatientFeedbackResponse;

public interface FeedbackService {

	DoctorAppFeedback saveDoctorAppFeedback(DoctorAppFeedback feedback);

	List<DoctorAppFeedback> getList(int size, int page);

	public List<PatientFeedbackResponse> getPatientFeedbackList(int size, int page, String patientId, String doctorId,
			String localeId, String locationId, String hospitalId, String type, List<String> services,
			Boolean discarded, Boolean isApproved);

	public Integer countPatientFeedbackList(String patientId, String doctorId, String localeId, String locationId,
			String hospitalId, String type, List<String> services, Boolean discarded, Boolean isApproved);
	
	public Boolean approvePatientFeedback(String id, Boolean isApproved);

}
