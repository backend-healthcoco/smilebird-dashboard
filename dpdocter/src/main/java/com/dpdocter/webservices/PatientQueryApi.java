package com.dpdocter.webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.PatientQuery;
import com.dpdocter.beans.PatientQueryRequest;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.PatientQueryService;


import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.PATIENT_QUERY_BASE_URL,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.PATIENT_QUERY_BASE_URL, description = "Endpoint for patient query")

public class PatientQueryApi {

private static Logger logger = LogManager.getLogger(PatientQueryApi.class.getName());
	
	@Autowired
	private PatientQueryService patientQueryServices;
	
	@PostMapping(value=PathProxy.PatientQueryUrls.ADD_EDIT_PATIENT_QUERY)
	@ApiOperation(value = PathProxy.PatientQueryUrls.ADD_EDIT_PATIENT_QUERY, notes = PathProxy.PatientQueryUrls.ADD_EDIT_PATIENT_QUERY)
	public Response<PatientQuery> addEditUserSymptom(@RequestBody PatientQueryRequest request)
	{
		
	if (request == null) {
		logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	} 
	Response<PatientQuery> response = new Response<PatientQuery>();
	response.setData(patientQueryServices.addEditPatientQuery(request));
	return response;
	}
	
	@GetMapping(value=PathProxy.PatientQueryUrls.GET_PATIENT_QUERY)
	@ApiOperation(value = PathProxy.PatientQueryUrls.GET_PATIENT_QUERY, notes = PathProxy.PatientQueryUrls.GET_PATIENT_QUERY)
	public Response<PatientQuery> getLanguages(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="searchTerm") String searchTerm,
			@RequestParam(required = false, value ="city") String city,@RequestParam(required = false, value ="speciality") String speciality) {
	
		Response<PatientQuery> response = new Response<PatientQuery>();
		
			response.setDataList(patientQueryServices.getPatientQuery(size, page, searchTerm,speciality,city));
		
		return response;
	}

}
