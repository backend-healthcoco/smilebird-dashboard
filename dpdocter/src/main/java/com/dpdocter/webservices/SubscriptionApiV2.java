package com.dpdocter.webservices;

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

import com.dpdocter.beans.Country;
import com.dpdocter.beans.DoctorSubscriptionPayment;
import com.dpdocter.beans.Subscription;
import com.dpdocter.collections.DoctorSubscriptionPaymentCollection;
import com.dpdocter.enums.PackageType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.SubscriptionServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "Endpoint for Subscription")
@RequestMapping(value = PathProxy.SUBSCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class SubscriptionApiV2 {

	private static Logger logger = LogManager.getLogger(SubscriptionApiV2.class.getName());

	@Autowired
	private SubscriptionServices subscriptionServices;

	@PostMapping(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION)
	@ApiOperation(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION, notes = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION)
	public Response<Subscription> addEditSubscription(@RequestBody Subscription request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Subscription> response = new Response<Subscription>();
		response.setData(subscriptionServices.addEditSubscription(request));
		return response;
	}

	@GetMapping(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION)
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION, notes = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION)
	public Response<Subscription> getSubscription(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = subscriptionServices.countSubscription(isDiscarded, searchTerm);
		Response<Subscription> response = new Response<Subscription>();
		if (count > 0)
			response.setDataList(subscriptionServices.getSubscription(size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@GetMapping(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID)
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID, notes = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID)
	public Response<Subscription> getSubscriptionByDoctorId(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value = "packageName") PackageType packageName,
			@RequestParam(required = false,value ="duration") int duration,@RequestParam(required = false,value ="newAmount") int newAmount) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Subscription> response = new Response<Subscription>();
		response.setData(subscriptionServices.getSubscriptionByDoctorId(doctorId, packageName,duration,newAmount));
		return response;

	}

	@GetMapping(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_PAYMENT)
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_PAYMENT, notes = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_PAYMENT)
	public Response<DoctorSubscriptionPayment> getSubscriptionPaymentByDoctorId(
			@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page) {
		Integer count = subscriptionServices.countSubscriptionPayment(doctorId);
		Response<DoctorSubscriptionPayment> response = new Response<DoctorSubscriptionPayment>();
		if (count > 0)
			response.setDataList(subscriptionServices.getSubscriptionPaymentByDoctorId(doctorId, size, page));
		response.setCount(count);
		return response;
	}

}
