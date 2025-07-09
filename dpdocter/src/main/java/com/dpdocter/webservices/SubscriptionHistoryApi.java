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
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Subscription;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.SubscriptionHistoryServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "Endpoint for Subscription")
@RequestMapping(value = PathProxy.SUBSCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class SubscriptionHistoryApi {

	private static Logger logger = LogManager.getLogger(SubscriptionHistoryApi.class.getName());

	@Autowired
	private SubscriptionHistoryServices subscriptionHistoryServices;

	@PostMapping(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION_HISTORY)
	@ApiOperation(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION_HISTORY, notes = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION_HISTORY)
	public Response<Subscription> addEditSubscription(@RequestBody Subscription request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Subscription> response = new Response<Subscription>();
		response.setData(subscriptionHistoryServices.addEditSubscriptionHistory(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_HISTORY)
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_HISTORY, notes = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_HISTORY)
	public Response<Subscription> getCountry(@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = subscriptionHistoryServices.countSubscriptionHistory(isDiscarded, searchTerm,doctorId);
		Response<Subscription> response = new Response<Subscription>();
		if (count > 0)
			response.setDataList(subscriptionHistoryServices.getSubscriptionHistory(size, page, isDiscarded, searchTerm,doctorId));
		response.setCount(count);
		return response;
	}
	
	
	@DeleteMapping(value = PathProxy.SubscriptionUrls.DELETE_SUBSCRIPTION_HISTORY)
	@ApiOperation(value = PathProxy.SubscriptionUrls.DELETE_SUBSCRIPTION_HISTORY, notes = PathProxy.SubscriptionUrls.DELETE_SUBSCRIPTION_HISTORY)
	public Response<Boolean> discardSubscriptionHistory(@PathVariable("id") String id,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(subscriptionHistoryServices.discardSubscriptionHistory(id, isDiscarded));
		return response;
	}
	
}
