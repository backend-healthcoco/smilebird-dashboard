package com.dpdocter.webservices;


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

import com.dpdocter.beans.LabTestRequest;
import com.dpdocter.collections.LabTestUsersPaymentCollection;
import com.dpdocter.enums.DisplayTypeThyrocare;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.LabTestCustomerResponse;
import com.dpdocter.response.LabTestResponse;
import com.dpdocter.services.LabTestPlansService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.LABTEST_URL, produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LABTEST_URL, description = "Endpoint for plans")
public class LabTestPlansApi {

	private static Logger logger = LogManager.getLogger(LabTestPlansApi.class.getName());

	@Autowired
	private LabTestPlansService labTestService;

	@PostMapping(value = PathProxy.LabTestModuleUrls.ADD_EDIT_LAB_TEST)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.ADD_EDIT_LAB_TEST, notes = PathProxy.LabTestModuleUrls.ADD_EDIT_LAB_TEST)
	public Response<LabTestResponse> addEditLabTestPlan(@RequestBody LabTestRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<LabTestResponse> response = new Response<LabTestResponse>();

		response.setData(labTestService.addEditLabTestPlan(request));

		return response;
	}
	
	@GetMapping(PathProxy.LabTestModuleUrls.GET_LAB_TEST_GET_BY_ID)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_LAB_TEST_GET_BY_ID, notes = PathProxy.LabTestModuleUrls.GET_LAB_TEST_GET_BY_ID)
	public Response<LabTestResponse> getLabTestPlanById(@PathVariable("planId") String planId) {

		Response<LabTestResponse> response = null;

		if (DPDoctorUtils.anyStringEmpty(planId)) {
			logger.warn("plan Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<LabTestResponse>();
		response.setData(labTestService.getLabTestPlanById(planId));

		return response;
	}

	@GetMapping(PathProxy.LabTestModuleUrls.GET_LAB_TEST_LIST)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_LAB_TEST_LIST, notes = PathProxy.LabTestModuleUrls.GET_LAB_TEST_LIST)
	public @ResponseBody Response<LabTestResponse> getLabTestPlans(@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="labPartner"  ) String labPartner, 
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "false") boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {

		Integer count = labTestService.countPlans(labPartner, isDiscarded, searchTerm);
		Response<LabTestResponse> response = new Response<LabTestResponse>();
		response.setDataList(labTestService.getLabTestPlans(page, size, labPartner, isDiscarded,searchTerm));
		response.setCount(count);
		return response;
	}
	
	@GetMapping(PathProxy.LabTestModuleUrls.GET_LAB_TEST_PAYMENTLIST)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_LAB_TEST_PAYMENTLIST, notes = PathProxy.LabTestModuleUrls.GET_LAB_TEST_PAYMENTLIST)
	public @ResponseBody Response<LabTestUsersPaymentCollection> getLabTestPayment(@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="labPartner"  ) String labPartner, 
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "false") boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {

		Integer count = labTestService.countPlans(labPartner, isDiscarded, searchTerm);
		Response<LabTestUsersPaymentCollection> response = new Response<LabTestUsersPaymentCollection>();
		response.setDataList(labTestService.getLabTestPayment(page, size, labPartner, isDiscarded,searchTerm));
		response.setCount(count);
		return response;
	}


	@DeleteMapping(PathProxy.LabTestModuleUrls.DELETE_LAB_TEST_GET_BY_ID)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.DELETE_LAB_TEST_GET_BY_ID, notes = PathProxy.LabTestModuleUrls.DELETE_LAB_TEST_GET_BY_ID)
	public Response<Boolean> deleteLabTestPlan(@PathVariable("planId") String planId,
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "true") boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(planId)) {
			logger.warn("plan Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(labTestService.deleteLabTestPlan(planId, isDiscarded));

		return response;
	}
	
	
	@GetMapping(PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PLANS_RESPONSE)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PLANS_RESPONSE, notes = PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PLANS_RESPONSE)
	public @ResponseBody Response<Object> getHealthiensPlans() {

		Response<Object> response = labTestService.getHealthiensPlans();

		return response;
	}
	
	/**
	 * Upload Prod Data of Healthiens Deal Id
	 * **/
	@GetMapping(value = PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PROD_PLANS_UPLOAD)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PROD_PLANS_UPLOAD, notes = PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PROD_PLANS_UPLOAD)
	public Response<Boolean> uploadProdDealDetail() {
		
		Boolean removeDuplicateDrugs = labTestService.uploadProdDealDetail();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}
	
	@GetMapping(PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PROD_PLANS_RESPONSE)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PROD_PLANS_RESPONSE, notes = PathProxy.LabTestModuleUrls.GET_HEALTHIANS_PROD_PLANS_RESPONSE)
	public @ResponseBody Response<Object> getHealthiensPlansProdData() {

		Response<Object> response = labTestService.getHealthiensPlansProdData();

		return response;
	}
	
	@GetMapping(PathProxy.LabTestModuleUrls.GET_THYROCARE_PLANS_RESPONSE)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_THYROCARE_PLANS_RESPONSE, notes = PathProxy.LabTestModuleUrls.GET_THYROCARE_PLANS_RESPONSE)
	public @ResponseBody Response<Object> getThyrocarePlans(@RequestParam(required = false, value = "display_type", defaultValue = "ALL")DisplayTypeThyrocare display_type) {

		Response<Object> response = labTestService.getThyrocarePlans(display_type);

		return response;
	}
	

	@GetMapping(PathProxy.LabTestModuleUrls.GET_LAB_TEST_CUSTOMER_LIST)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_LAB_TEST_CUSTOMER_LIST, notes = PathProxy.LabTestModuleUrls.GET_LAB_TEST_CUSTOMER_LIST)
	public @ResponseBody Response<LabTestCustomerResponse> getLabTestUsersList(@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="labPartner"  ) String labPartner, 
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "false") boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {

		Integer count = labTestService.countLabTestPlans(labPartner, isDiscarded, searchTerm);
		Response<LabTestCustomerResponse> response = new Response<LabTestCustomerResponse>();
		response.setDataList(labTestService.getLabTestUsersList(page, size, labPartner,searchTerm));
		response.setCount(count);
		return response;
	}
	
	@GetMapping(PathProxy.LabTestModuleUrls.GET_LAB_TEST_USER_GET_BY_ID)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.GET_LAB_TEST_USER_GET_BY_ID, notes = PathProxy.LabTestModuleUrls.GET_LAB_TEST_USER_GET_BY_ID)
	public Response<LabTestCustomerResponse> getLabTestPlanCustomerById(@PathVariable("labTestAppointmentId") String labTestAppointmentId) {

		Response<LabTestCustomerResponse> response = null;

		if (DPDoctorUtils.anyStringEmpty(labTestAppointmentId)) {
			logger.warn("lab test appointment Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<LabTestCustomerResponse>();
		response.setData(labTestService.getLabTestPlanCustomerById(labTestAppointmentId));

		return response;
	}
	
	@GetMapping(value = PathProxy.LabTestModuleUrls.LAB_TEST_GET_BOOKING_STATUS)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.LAB_TEST_GET_BOOKING_STATUS, notes = PathProxy.LabTestModuleUrls.LAB_TEST_GET_BOOKING_STATUS)
	public Response<Object> getBookingStatus(@RequestParam(required = true, value = "booking_id") String booking_id) {
		if (booking_id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " booking_id Required");
		}

		Response<Object> response = labTestService.getBookingStatus(booking_id);

		return response;

	}
	
	@GetMapping(value = PathProxy.LabTestModuleUrls.LAB_TEST_GET_BOOKING_STATUS_THYROCARE)
	@ApiOperation(value = PathProxy.LabTestModuleUrls.LAB_TEST_GET_BOOKING_STATUS_THYROCARE, notes = PathProxy.LabTestModuleUrls.LAB_TEST_GET_BOOKING_STATUS_THYROCARE)
	public Response<Object> getCustomerReportForThyrocare(@RequestParam(required = true, value = "ref_orderId") String ref_orderId,
			@RequestParam(required = true, value = "loginMobile") String loginMobile) {
		if (ref_orderId == null && loginMobile == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		
		Response<Object> response = labTestService.getBookingStatusForThyrocare(loginMobile,ref_orderId);

		return response;
	}
	
}
