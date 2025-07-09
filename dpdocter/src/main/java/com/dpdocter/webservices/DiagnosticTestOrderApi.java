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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DiagnosticTestPackage;
import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.collections.DiagnosticTestPickUpSlotCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.DiagnosticTestOrderService;
import com.dpdocter.webservices.PathProxy.DiagnosticTestOrderUrls;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.DIAGNOSTIC_TEST_ORDER_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DIAGNOSTIC_TEST_ORDER_BASE_URL, description = "Endpoint for diagnostic test order apis")
public class DiagnosticTestOrderApi {

	private static Logger logger = LogManager.getLogger(DiagnosticTestOrderApi.class.getName());
	
	@Autowired
	private DiagnosticTestOrderService diagnosticTestOrderService;

	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS, notes = DiagnosticTestOrderUrls.GET_SAMPLE_PICKUP_TIME_SLOTS)
	public Response<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots() {

		List<DiagnosticTestSamplePickUpSlot> labSearchResponses = diagnosticTestOrderService.getDiagnosticTestSamplePickUpTimeSlots();
		
		Response<DiagnosticTestSamplePickUpSlot> response = new Response<DiagnosticTestSamplePickUpSlot>();
		response.setDataList(labSearchResponses);
		return response;
	}
	
	@PostMapping(value = PathProxy.DiagnosticTestOrderUrls.ADD_EDIT_PICKUP_TIME_SLOTS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.ADD_EDIT_PICKUP_TIME_SLOTS, notes = DiagnosticTestOrderUrls.ADD_EDIT_PICKUP_TIME_SLOTS)
	public Response<DiagnosticTestPickUpSlotCollection> addEditPickUpTimeSlots(@RequestBody DiagnosticTestPickUpSlotCollection request) {

		if(request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DiagnosticTestPickUpSlotCollection> response = new Response<DiagnosticTestPickUpSlotCollection>();
		response.setData(diagnosticTestOrderService.addEditPickUpTimeSlots(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.UPDATE_ORDER_STATUS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.UPDATE_ORDER_STATUS, notes = DiagnosticTestOrderUrls.UPDATE_ORDER_STATUS)
	public Response<OrderDiagnosticTest> updateDiagnosticTestOrderStatus(@PathVariable("orderId") String orderId, @PathVariable("status") String status) {

		OrderDiagnosticTest orderDiagnosticTest = diagnosticTestOrderService.updateDiagnosticTestOrderStatus(orderId, status);
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setData(orderDiagnosticTest);
		return response;
	}
	
	@GetMapping(value = PathProxy.DiagnosticTestOrderUrls.GET_ORDERS)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_ORDERS, notes = DiagnosticTestOrderUrls.GET_ORDERS)
	public Response<OrderDiagnosticTest> getLabOrders(@RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="userId") String userId, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		List<OrderDiagnosticTest> orderDiagnosticTests = diagnosticTestOrderService.getOrders(locationId, userId, page, size);
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setDataList(orderDiagnosticTests);
		return response;
	}
	
	@GetMapping(PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID, notes = PathProxy.DiagnosticTestOrderUrls.GET_ORDER_BY_ID)
	public Response<OrderDiagnosticTest> getDiagnosticTestOrderById(@PathVariable("orderId") String orderId) {
		 if (DPDoctorUtils.anyStringEmpty(orderId)) {
				throw new BusinessException(ServiceError.InvalidInput, "OderId cannot be null");
		}
		
		Response<OrderDiagnosticTest> response = new Response<OrderDiagnosticTest>();
		response.setData(diagnosticTestOrderService.getDiagnosticTestOrderById(orderId));

		return response;
	}
	
	@PostMapping(PathProxy.DiagnosticTestOrderUrls.ADD_EDIT_DIAGNOSTIC_TEST_PACKAGE)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.ADD_EDIT_DIAGNOSTIC_TEST_PACKAGE, notes = PathProxy.DiagnosticTestOrderUrls.ADD_EDIT_DIAGNOSTIC_TEST_PACKAGE)
	public Response<DiagnosticTestPackage> addEditDiagnosticTestPackage(@RequestBody DiagnosticTestPackage request) {
		 if (request == null || DPDoctorUtils.anyStringEmpty(request.getPackageName(), request.getLocationId(), request.getHospitalId())) {
				throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");
		}
		
		Response<DiagnosticTestPackage> response = new Response<DiagnosticTestPackage>();
		response.setData(diagnosticTestOrderService.addEditDiagnosticTestPackage(request));

		return response;
	}

	@GetMapping(PathProxy.DiagnosticTestOrderUrls.GET_DIAGNOSTIC_TEST_PACKAGES)
	@ApiOperation(value = PathProxy.DiagnosticTestOrderUrls.GET_DIAGNOSTIC_TEST_PACKAGES, notes = PathProxy.DiagnosticTestOrderUrls.GET_DIAGNOSTIC_TEST_PACKAGES)
	public Response<DiagnosticTestPackage> getDiagnosticTestPackages(@RequestParam(required = false, value ="locationId") String locationId, 
			@RequestParam(required = false, value ="hospitalId") String hospitalId, @RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
		
		Response<DiagnosticTestPackage> response = new Response<DiagnosticTestPackage>();
		response.setDataList(diagnosticTestOrderService.getDiagnosticTestPackages(locationId, hospitalId, discarded, page, size));

		return response;
	}
}
