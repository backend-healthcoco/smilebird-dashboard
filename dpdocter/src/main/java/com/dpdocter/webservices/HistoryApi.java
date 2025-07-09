package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.HISTORY_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.HISTORY_BASE_URL, description = "Endpoint for history")
public class HistoryApi {

    private static Logger logger = LogManager.getLogger(HistoryApi.class.getName());

    @Autowired
    private HistoryServices historyServices;

    @Autowired
    private TransactionalManagementService transactionalManagementService;
    
    @Autowired
    private ESMasterService esMasterService; 

    @Value(value = "${image.path}")
    private String imagePath;

    @PostMapping(value = PathProxy.HistoryUrls.ADD_DISEASE)
    @ApiOperation(value = PathProxy.HistoryUrls.ADD_DISEASE, notes = PathProxy.HistoryUrls.ADD_DISEASE)
    public Response<DiseaseAddEditResponse> addDiseases(@RequestBody List<DiseaseAddEditRequest> request) {
    	if (request == null || request.isEmpty() || DPDoctorUtils.anyStringEmpty(request.get(0).getDoctorId(), request.get(0).getLocationId(), request.get(0).getHospitalId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<DiseaseAddEditResponse> diseases = historyServices.addDiseases(request);
	for(DiseaseAddEditResponse addEditResponse : diseases){
		transactionalManagementService.addResource(new ObjectId(addEditResponse.getId()), Resource.DISEASE, false);
		ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
		BeanUtil.map(addEditResponse, esDiseasesDocument);
		esMasterService.addEditDisease(esDiseasesDocument);
	}
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setDataList(diseases);
	return response;
    }

    @PutMapping(value = PathProxy.HistoryUrls.EDIT_DISEASE)
    @ApiOperation(value = PathProxy.HistoryUrls.EDIT_DISEASE, notes = PathProxy.HistoryUrls.EDIT_DISEASE)
    public Response<DiseaseAddEditResponse> editDisease(@PathVariable(value = "diseaseId") String diseaseId, DiseaseAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(diseaseId, request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    }
	request.setId(diseaseId);
	DiseaseAddEditResponse diseases = historyServices.editDiseases(request);
	transactionalManagementService.addResource(new ObjectId(diseases.getId()), Resource.DISEASE, false);
	ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
	BeanUtil.map(diseases, esDiseasesDocument);
	esMasterService.addEditDisease(esDiseasesDocument);
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setData(diseases);
	return response;
    }

    @DeleteMapping(value = PathProxy.HistoryUrls.DELETE_DISEASE)
    @ApiOperation(value = PathProxy.HistoryUrls.DELETE_DISEASE, notes = PathProxy.HistoryUrls.DELETE_DISEASE)
    public Response<DiseaseAddEditResponse> deleteDisease(@PathVariable(value = "diseaseId") String diseaseId, @PathVariable(value = "doctorId") String doctorId,
	    @PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
	    @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
	if (DPDoctorUtils.anyStringEmpty(diseaseId, doctorId, hospitalId, locationId)) {
	    logger.warn("Disease Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Disease Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	DiseaseAddEditResponse diseaseDeleteResponse = historyServices.deleteDisease(diseaseId, doctorId, hospitalId, locationId, discarded);
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setData(diseaseDeleteResponse);
	return response;
    }

    @GetMapping(value = PathProxy.HistoryUrls.GET_DISEASES)
    @ApiOperation(value = PathProxy.HistoryUrls.GET_DISEASES, notes = PathProxy.HistoryUrls.GET_DISEASES)
    public Response<DiseaseListResponse> getDiseases(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
	    @RequestParam(required = false, value ="doctorId") String doctorId, @RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="hospitalId") String hospitalId,
	    @RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime, @RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
    if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
    	    logger.warn("Range or Doctor Id Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Range or Doctor Id Cannot Be Empty");
    	}
	List<DiseaseListResponse> diseaseListResponse = historyServices.getDiseases(range, page, size, doctorId, hospitalId, locationId, updatedTime, discarded, false, null);
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(diseaseListResponse);
	return response;
    }
}
