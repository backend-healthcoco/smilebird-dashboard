package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.services.NutritionReferenceService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.NUTRITION_REFERENCE_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NUTRITION_REFERENCE_BASE_URL, description = "Endpoint for nutrition Referenceapi's")
public class NutritionReferenceAPI {
	private static Logger logger = LogManager.getLogger(NutritionReferenceAPI.class.getName());

	@Autowired
	private NutritionReferenceService nutritionReferenceService;

	@GetMapping(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_REFERENCES)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_REFERENCES)
	public Response<NutritionReferenceResponse> getNutritionReference(@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="searchTerm") String searchTerm, @RequestParam(required = false, value ="status") String status,
			@RequestParam(required = false, value ="fromDate", defaultValue = "0") Long fromDate,
			@RequestParam(required = false, value ="toDate", defaultValue = "0") Long toDate) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setDataList(nutritionReferenceService.getNutritionReferenceList(doctorId, locationId, page, size,
				searchTerm, status, fromDate, toDate));
		return response;
	}

	@GetMapping(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_ANALYTICS)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_ANALYTICS)
	public Response<NutritionGoalAnalytics> getNutritionAnalytics(@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId, 
			@RequestParam(required = false, value ="fromDate", defaultValue = "0") Long fromDate,
			@RequestParam(required = false, value ="toDate", defaultValue = "0") Long toDate) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionGoalAnalytics> response = new Response<NutritionGoalAnalytics>();
		response.setData(nutritionReferenceService.getGoalAnalytics(doctorId, locationId, fromDate, toDate));
		return response;
	}

	@GetMapping(value = PathProxy.NutritionReferenceUrl.GRT_NUTRITION_REFERNCE)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GRT_NUTRITION_REFERNCE)
	public Response<NutritionReferenceResponse> getNutritionReference(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setData(nutritionReferenceService.getNutritionReferenceById(id));
		return response;
	}

	@GetMapping(value = PathProxy.NutritionReferenceUrl.CHANGE_REFERENCE_STATUS)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.CHANGE_REFERENCE_STATUS)
	public Response<Boolean> changeNutritionReference(@PathVariable("id") String id,
			@RequestParam(required = false, value ="regularityStatus") String regularityStatus, @RequestParam(required = false, value ="goalStatus") String goalStatus) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(nutritionReferenceService.changeStatus(id, regularityStatus, goalStatus));
		return response;
	}
}
