package com.dpdocter.webservices;

import java.util.List;

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

import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.SubscriptionDetailServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SUBSCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "Endpoint for Suscription")
public class SubscriptionApi {
	private static Logger logger = LogManager.getLogger(SubscriptionApi.class.getName());

	@Autowired
	private SubscriptionDetailServices subscriptionDetailServices;

	@PostMapping(value = PathProxy.SescriptionUrls.ACTIVATE_SUBSCRIPTION)
	@ApiOperation(value = PathProxy.SescriptionUrls.ACTIVATE_SUBSCRIPTION, notes = PathProxy.SescriptionUrls.ACTIVATE_SUBSCRIPTION)
	public Response<SubscriptionDetail> activate(@RequestBody SubscriptionDetail request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("doctorId  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId is NULL");
		}
		SubscriptionDetail result = subscriptionDetailServices.activate(request);
		Response<SubscriptionDetail> response = new Response<SubscriptionDetail>();
		response.setData(result);
		return response;

	}

	@GetMapping(value = PathProxy.SescriptionUrls.GET_SUBSCRIPTION_DETAILS)
	@ApiOperation(value = PathProxy.SescriptionUrls.GET_SUBSCRIPTION_DETAILS, notes = PathProxy.SescriptionUrls.GET_SUBSCRIPTION_DETAILS)
	public Response<Object> getSubscriptionDetails(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="doctorId") String doctorId, @RequestParam(required = false, value ="isDemo") boolean isDemo,
			@RequestParam(required = false, value ="isExpired") boolean isExpired) {
		List<SubscriptionDetail> details = subscriptionDetailServices.getSubscriptionDetails(size, page, doctorId, isDemo,
				isExpired);
		Response<Object> response = new Response<Object>();
		response.setDataList(details);
		return response;

	}

	@GetMapping(value = PathProxy.SescriptionUrls.DEACTIVATE_SUBSCRIPTION)
	@ApiOperation(value = PathProxy.SescriptionUrls.DEACTIVATE_SUBSCRIPTION, notes = PathProxy.SescriptionUrls.DEACTIVATE_SUBSCRIPTION)
	public Response<Boolean> deactivate(@PathVariable("subscriptionDetailId") String subscriptionDetailId) {

		Boolean result = subscriptionDetailServices.deactivate(subscriptionDetailId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(result);
		return response;

	}

}
