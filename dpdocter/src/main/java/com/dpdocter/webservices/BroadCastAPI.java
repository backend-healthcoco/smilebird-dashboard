package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BroadcastRequest;
import com.dpdocter.beans.FeedsResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.BroadCastMailRequest;
import com.dpdocter.request.BroadCastRequest;
import com.dpdocter.request.MessageRequest;
import com.dpdocter.services.BroadCastService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.BROAD_CAST_BASE_URL, produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BROAD_CAST_BASE_URL, description = "Endpoint for broadcast")
public class BroadCastAPI {
	private static Logger logger = LogManager.getLogger(BroadCastAPI.class.getName());

	@Autowired
	private BroadCastService broadCastService;

	@PostMapping(value = PathProxy.BroadCastUrls.BROAD_CAST_MAIL)
	@ApiOperation(value = PathProxy.BroadCastUrls.BROAD_CAST_MAIL, notes = PathProxy.BroadCastUrls.BROAD_CAST_MAIL)
	public Response<Boolean> broadcastMail(@RequestBody BroadCastMailRequest request) {
		if (DPDoctorUtils.allStringsEmpty(request.getAdminId(), request.getBody(), request.getSubject())) {
			throw new BusinessException(ServiceError.InvalidInput, "AdminId,Body and subject should not null");
		}
		broadCastService.broadcastMail(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@PostMapping(value = PathProxy.BroadCastUrls.BROAD_CAST_NOTIFICATION)
	@ApiOperation(value = PathProxy.BroadCastUrls.BROAD_CAST_NOTIFICATION, notes = PathProxy.BroadCastUrls.BROAD_CAST_NOTIFICATION)
	public Response<Boolean> broadcastPushNotification(@RequestBody BroadCastRequest request) {
		if (DPDoctorUtils.allStringsEmpty(request.getAdminId(), request.getMessage())) {
			throw new BusinessException(ServiceError.InvalidInput, "AdminId,massage should not null");
		}
		broadCastService.broadcastPushNotification(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@PostMapping(value = PathProxy.BroadCastUrls.BROAD_CAST_SMS)
	@ApiOperation(value = PathProxy.BroadCastUrls.BROAD_CAST_SMS, notes = PathProxy.BroadCastUrls.BROAD_CAST_SMS)
	public Response<Boolean> broadcastSMS(@RequestBody BroadCastRequest request) {
		if (DPDoctorUtils.allStringsEmpty(request.getAdminId(), request.getMessage())) {
			throw new BusinessException(ServiceError.InvalidInput, "AdminId,massage should not null");
		}
		broadCastService.broadcastSMS(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@PostMapping(value = PathProxy.BroadCastUrls.SEND_SMS)
	@ApiOperation(value = PathProxy.BroadCastUrls.SEND_SMS, notes = PathProxy.BroadCastUrls.SEND_SMS)
	public Response<String> sendSMS(@RequestBody MessageRequest request) {
		if (request.getMobileNumber().equals(null) &&  request.getMessage().equals(null)) {
			throw new BusinessException(ServiceError.InvalidInput, "mobileNumber,message should not null");
		}
		String sms=broadCastService.sendSMSToUser(request);
		Response<String> response = new Response<String>();
		response.setData(sms);
		return response;
	}
	
	
	
	
	@PostMapping(value = PathProxy.BroadCastUrls.GET_USERS_COUNT)
	@ApiOperation(value = PathProxy.BroadCastUrls.GET_USERS_COUNT, notes = PathProxy.BroadCastUrls.GET_USERS_COUNT)
	public Response<Integer> getFeeds(@RequestBody BroadcastRequest request) {
		Response<Integer> response = new Response<Integer>();
		response.setCount(broadCastService.countUsers(request));
		return response;
	}
	
	
}
