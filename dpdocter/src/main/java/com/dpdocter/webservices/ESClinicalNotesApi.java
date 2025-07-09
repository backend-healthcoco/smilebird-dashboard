package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_CLINICAL_NOTES_BASEURL, produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_CLINICAL_NOTES_BASEURL, description = "Endpoint for es clinical notes")
public class ESClinicalNotesApi {

	private static Logger logger = LogManager.getLogger(ESClinicalNotesApi.class.getName());
	
    @Value(value = "${image.path}")
    private String imagePath;

    @Autowired
    private ESClinicalNotesService esClinicalNotesService;
    
    @GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
    public Response<ESComplaintsDocument> searchComplaints(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded, @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESComplaintsDocument> complaints = esClinicalNotesService.searchComplaints(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<ESComplaintsDocument> response = new Response<ESComplaintsDocument>();
	response.setDataList(complaints);
	return response;
    }
    
    @GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
    public Response<ESDiagnosesDocument> searchDiagnoses(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded, @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESDiagnosesDocument> diagnoses = esClinicalNotesService.searchDiagnoses(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	Response<ESDiagnosesDocument> response = new Response<ESDiagnosesDocument>();
	response.setDataList(diagnoses);
	return response;
    }

    @GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
    public Response<ESNotesDocument> searchNotes(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded, @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESNotesDocument> notes = esClinicalNotesService.searchNotes(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<ESNotesDocument> response = new Response<ESNotesDocument>();
	response.setDataList(notes);
	return response;
    }

    @GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
    public Response<ESDiagramsDocument> searchDiagrams(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded, @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESDiagramsDocument> diagrams = esClinicalNotesService.searchDiagrams(range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded, searchTerm);
	diagrams = getFinalDiagrams(diagrams);
	Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();
	response.setDataList(diagrams);
	return response;
    }

//    @Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
//    @GET
//    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
//    public Response<ESDiagramsDocument> searchDiagramsBySpeciality(@PathVariable(value = "searchTerm") String searchTerm) {
//
//	List<ESDiagramsDocument> diagrams = esClinicalNotesService.searchDiagramsBySpeciality(searchTerm);
//	diagrams = getFinalDiagrams(diagrams);
//	Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();
//	response.setDataList(diagrams);
//	return response;
//    }

    @GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
    public Response<ESInvestigationsDocument> searchInvestigations(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
	    @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded, @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESInvestigationsDocument> investigations = esClinicalNotesService.searchInvestigations(range, page, size, doctorId, locationId, hospitalId,
		updatedTime, discarded, searchTerm);
	Response<ESInvestigationsDocument> response = new Response<ESInvestigationsDocument>();
	response.setDataList(investigations);
	return response;
    }

    @GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
    @ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
    public Response<ESObservationsDocument> searchObservations(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded, @RequestParam(required = false, value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<ESObservationsDocument> observations = esClinicalNotesService.searchObservations(range, page, size, doctorId, locationId, hospitalId,
		updatedTime, discarded, searchTerm);
	Response<ESObservationsDocument> response = new Response<ESObservationsDocument>();
	response.setDataList(observations);
	return response;
    }

    private List<ESDiagramsDocument> getFinalDiagrams(List<ESDiagramsDocument> diagrams) {
	for (ESDiagramsDocument diagram : diagrams) {
	    if (diagram.getDiagramUrl() != null) {
		diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
	    }
	}
	return diagrams;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;
    }
}
