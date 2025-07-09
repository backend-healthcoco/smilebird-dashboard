package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.LabTest;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_PRESCRIPTION_BASEURL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_PRESCRIPTION_BASEURL, description = "Endpoint for solr prescription")
public class ESPrescriptionApi {

	private static Logger logger = LogManager.getLogger(ESPrescriptionApi.class.getName());
	
    @Autowired
    private ESPrescriptionService esPrescriptionService;
    
    @GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
    @ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
    public Response<ESDrugDocument> searchDrug(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded, @RequestParam(required = false, value = "searchTerm") String searchTerm) {

    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}

	List<ESDrugDocument> complaints = esPrescriptionService.searchDrug(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<ESDrugDocument> response = new Response<ESDrugDocument>();
	response.setDataList(complaints);
	return response;
    }

    @GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
    @ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_LAB_TEST)
    public Response<LabTest> searchLabTest(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "hospitalId") String hospitalId,
	    @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded,
	    @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, locationId, hospitalId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<LabTest> labTests = esPrescriptionService.searchLabTest(range, page, size, locationId, hospitalId, updatedTime, discarded, searchTerm);
	Response<LabTest> response = new Response<LabTest>();
	response.setDataList(labTests);
	return response;
    }

    @GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
    @ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DIAGNOSTIC_TEST)
    public Response<ESDiagnosticTestDocument> searchDiagnosticTest(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
	    @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "hospitalId") String hospitalId,
	    @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded,
	    @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, locationId, hospitalId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESDiagnosticTestDocument> diagnosticTests = esPrescriptionService.searchDiagnosticTest(range, page, size, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<ESDiagnosticTestDocument> response = new Response<ESDiagnosticTestDocument>();
	response.setDataList(diagnosticTests);
	return response;
    }

}
