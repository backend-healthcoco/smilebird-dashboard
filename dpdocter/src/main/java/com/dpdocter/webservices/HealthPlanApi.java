package com.dpdocter.webservices;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.HealthPlan;
import com.dpdocter.beans.HealthPlanResponse;
import com.dpdocter.beans.HealthPlanTypeResponse;
import com.dpdocter.enums.HealthPackagesPlanType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.HealthiansPlanObject;
import com.dpdocter.services.HealthTherapyService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.HEALTHTHERAPY_URL, produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.HEALTHTHERAPY_URL, description = "Endpoint for plans")
public class HealthPlanApi {

	private static Logger logger = LogManager.getLogger(HealthPlanApi.class.getName());

	@Autowired
	private HealthTherapyService healthTherapyService;

	@PostMapping(value = PathProxy.HealthTherapyModuleUrls.ADD_EDIT_HEALTHPLAN)
	@ApiOperation(value = PathProxy.HealthTherapyModuleUrls.ADD_EDIT_HEALTHPLAN, notes = PathProxy.HealthTherapyModuleUrls.ADD_EDIT_HEALTHPLAN)
	public Response<HealthPlanResponse> addEditHealthTherapyPlan(@RequestBody HealthPlan request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<HealthPlanResponse> response = new Response<HealthPlanResponse>();

		response.setData(healthTherapyService.addEditHealthTherapyPlan(request));

		return response;
	}
	
	@GetMapping(PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_GET_BY_ID)
	@ApiOperation(value = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_GET_BY_ID, notes = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_GET_BY_ID)
	public Response<HealthPlanResponse> getHealthTherapyPlanById(@PathVariable("planId") String planId) {

		Response<HealthPlanResponse> response = null;

		if (DPDoctorUtils.anyStringEmpty(planId)) {
			logger.warn("plan Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<HealthPlanResponse>();
		response.setData(healthTherapyService.getHealthTherapyPlanById(planId));

		return response;
	}

	@GetMapping(PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_LIST)
	@ApiOperation(value = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_LIST, notes = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_LIST)
	public @ResponseBody Response<HealthPlanResponse> getHealthTherapyPlan(@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="type") String type, 
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "false") boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {

		Integer count = healthTherapyService.countPlans(type, isDiscarded, searchTerm);
		Response<HealthPlanResponse> response = new Response<HealthPlanResponse>();
		response.setDataList(healthTherapyService.getHealthTherapyPlans(page, size, type, isDiscarded,searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(PathProxy.HealthTherapyModuleUrls.DELETE_HEALTHPLAN_GET_BY_ID)
	@ApiOperation(value = PathProxy.HealthTherapyModuleUrls.DELETE_HEALTHPLAN_GET_BY_ID, notes = PathProxy.HealthTherapyModuleUrls.DELETE_HEALTHPLAN_GET_BY_ID)
	public Response<Boolean> DeleteHealthTherapyPlan(@PathVariable("planId") String planId,
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "true") boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(planId)) {
			logger.warn("plan Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(healthTherapyService.deleteHealthTherapyPlan(planId, isDiscarded));

		return response;
	}
	
	@GetMapping(PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_BY_SLUGURL)
	@ApiOperation(value = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_BY_SLUGURL, notes = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLAN_BY_SLUGURL)
	public Response<HealthPlanResponse> getHealthTherapyPlanBySlugUrl(@PathVariable("slugURL") String slugURL,@PathVariable("planUId") String planUId) {

		Response<HealthPlanResponse> response = null;

		if (DPDoctorUtils.anyStringEmpty(planUId)) {
			logger.warn("planUId Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<HealthPlanResponse>();
		response.setData(healthTherapyService.getHealthTherapyPlanBySlugUrl(slugURL,planUId));

		return response;
	}
	
	@GetMapping(PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLANS_TITLE)
	@ApiOperation(value = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLANS_TITLE, notes = PathProxy.HealthTherapyModuleUrls.GET_HEALTHPLANS_TITLE)
	public @ResponseBody Response<HealthPlanTypeResponse> getHealthTherapyPlanTitles(@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="type") HealthPackagesPlanType type, 
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "false") boolean isDiscarded,
			@RequestParam(required = false, value = "country", defaultValue = "India") String country) {

		Integer count = healthTherapyService.countPlansTitles(type, isDiscarded, country);
		Response<HealthPlanTypeResponse> response = new Response<HealthPlanTypeResponse>();
		response.setDataList(healthTherapyService.getHealthTherapyPlansTitles(page, size, type, isDiscarded,country));
		response.setCount(count);
		return response;
	}
	
	@GetMapping(PathProxy.HealthTherapyModuleUrls.GET_HEALTHIENS_PLANS)
	@ApiOperation(value = PathProxy.HealthTherapyModuleUrls.GET_HEALTHIENS_PLANS, notes = PathProxy.HealthTherapyModuleUrls.GET_HEALTHIENS_PLANS)
	public Response<Object> HealthiensPlans(@PathVariable("partnerName") String partnerName,
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "true") boolean isDiscarded) throws IOException {
		if (DPDoctorUtils.anyStringEmpty(partnerName)) {
			logger.warn("plan Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<Object> response = healthTherapyService.getHealthiensPlans(partnerName, isDiscarded);

		return response;
	}
}
