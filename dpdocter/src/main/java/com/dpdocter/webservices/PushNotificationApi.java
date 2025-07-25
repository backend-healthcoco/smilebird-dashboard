package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.BroadcastNotificationRequest;
import com.dpdocter.services.PushNotificationServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.PUSH_NOTIFICATION_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PUSH_NOTIFICATION_BASE_URL, description = "Endpoint for push notification")
public class PushNotificationApi {

    private static Logger logger = LogManager.getLogger(PushNotificationApi.class.getName());
	
	@Autowired
	PushNotificationServices pushNotificationServices;
	
	@PostMapping(value = PathProxy.PushNotificationUrls.ADD_DEVICE)
	@ApiOperation(value = PathProxy.PushNotificationUrls.ADD_DEVICE, notes = PathProxy.PushNotificationUrls.ADD_DEVICE)
	public Response<UserDevice> addDevice(@RequestBody UserDevice request){
		if(request == null || DPDoctorUtils.anyStringEmpty(request.getDeviceId(), request.getPushToken()) || request.getDeviceType() == null || request.getRole() == null){
			    logger.warn("Invalid Input");
			    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		UserDevice userDevice = pushNotificationServices.addDevice(request);
		Response<UserDevice> response = new Response<UserDevice>();
		response.setData(userDevice);
		return response;
	}
	
	@PostMapping(value = PathProxy.PushNotificationUrls.BROADCAST_NOTIFICATION)
	@ApiOperation(value = PathProxy.PushNotificationUrls.BROADCAST_NOTIFICATION, notes = PathProxy.PushNotificationUrls.BROADCAST_NOTIFICATION)
	public Response<Boolean> broadcastNotification(@RequestBody BroadcastNotificationRequest request){
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@PostMapping(value = PathProxy.PushNotificationUrls.READ_NOTIFICATION)
	@ApiOperation(value = PathProxy.PushNotificationUrls.READ_NOTIFICATION, notes = PathProxy.PushNotificationUrls.READ_NOTIFICATION)
	public Response<Boolean> readNotification(@PathVariable(value = "deviceId") String deviceId,
		    @RequestParam(required = false, value ="count", defaultValue = "0") Integer count){
		if(DPDoctorUtils.anyStringEmpty(deviceId)){
			    logger.warn("Invalid Input");
			    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		pushNotificationServices.readNotification(deviceId, count);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
}
