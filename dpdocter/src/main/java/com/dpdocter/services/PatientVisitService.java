package com.dpdocter.services;

import com.dpdocter.enums.VisitedFor;
import com.dpdocter.response.PatientTreatmentResponse;

public interface PatientVisitService {

	String addRecord(Object details, VisitedFor visitedFor, String visitId);
   
}
