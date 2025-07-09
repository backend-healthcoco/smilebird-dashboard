package com.dpdocter.webservices;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.services.ResetPasswordService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.RESET_PASSWORD_BASE_URL, produces = {MediaType.APPLICATION_JSON} ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.RESET_PASSWORD_BASE_URL, description = "Endpoint for resetPassword")
public class ResetPasswordApi {
	 private static Logger logger = LogManager.getLogger(ResetPasswordApi.class.getName());
	
	 @Autowired
	    private  ResetPasswordService resetPasswordService;
	 
	  @PostMapping(value = PathProxy.ResetPasswordUrls.RESET_PASSWORD_USER)
	    @ApiOperation(value = PathProxy.ResetPasswordUrls.RESET_PASSWORD_USER, notes = PathProxy.ResetPasswordUrls.RESET_PASSWORD_USER)
	    public Response<String> resetPassword(@RequestBody ResetPasswordRequest request) {
	    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getUserId())) {
	    	    logger.warn("Invalid Input");
	    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	    	}
	    	resetPasswordService.resetPasswordForUser(request);
		Response<String> response = new Response<String>();
		response.setData("Your password is reset successfully.");
		return response;
	    }
}
