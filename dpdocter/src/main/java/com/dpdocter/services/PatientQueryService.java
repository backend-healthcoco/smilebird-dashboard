package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PatientQuery;
import com.dpdocter.beans.PatientQueryRequest;

public interface PatientQueryService {

	//PatientQuery addEditPatientQuery(PatientQuery request);

	PatientQuery addEditPatientQuery(PatientQueryRequest request);

	List<PatientQuery> getPatientQuery(int size, int page, String searchTerm,String speciality,String city);
}
