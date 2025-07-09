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
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.services.ForgotPasswordService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.FORGOT_PASSWORD_BASE_URL, produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.FORGOT_PASSWORD_BASE_URL, description = "Endpoint for forgot password")
public class ForgotPasswordApi {

    private static Logger logger = LogManager.getLogger(ForgotPasswordApi.class.getName());

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR)
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR, notes = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR)
    public Response<String> forgotPassword(@RequestBody ForgotUsernamePasswordRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	forgotPasswordService.forgotPasswordForDoctor(request);
	Response<String> response = new Response<String>();
	response.setData("RESET YOUR PASSWORD FROM EMAIL ADDRESS");
	return response;
    }

    @PostMapping(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT)
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT, notes = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT)
    public Response<Boolean> forgotPasswordForPatient(@RequestBody ForgotUsernamePasswordRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	boolean flag = forgotPasswordService.forgotPasswordForPatient(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(flag);
	return response;
    }

    @PostMapping(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_PATIENT)
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_PATIENT, notes = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_PATIENT)
    public Response<Boolean> resetPasswordPatient(@RequestBody ResetPasswordRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean isReset = forgotPasswordService.resetPasswordPatient(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(isReset);
	return response;
    }

    @PostMapping(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD)
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD, notes = PathProxy.ForgotPasswordUrls.RESET_PASSWORD)
    public Response<String> resetPassword(@RequestBody ResetPasswordRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getUserId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	String string = forgotPasswordService.resetPassword(request);
	Response<String> response = new Response<String>();
	response.setData(string);
	return response;
    }

    @GetMapping(value = PathProxy.ForgotPasswordUrls.CHECK_LINK_IS_ALREADY_USED)
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.CHECK_LINK_IS_ALREADY_USED, notes = PathProxy.ForgotPasswordUrls.CHECK_LINK_IS_ALREADY_USED)
    public Response<String> checkLinkIsAlreadyUsed(@PathVariable(value = "userId") String userId) {
	if (DPDoctorUtils.anyStringEmpty(userId)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String string = forgotPasswordService.checkLinkIsAlreadyUsed(userId);
	Response<String> response = new Response<String>();
	response.setData(string);
	return response;
    }

    @PostMapping(value = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME)
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME, notes = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME)
    public Response<Boolean> forgotUsername(@RequestBody ForgotUsernamePasswordRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	boolean flag = forgotPasswordService.forgotUsername(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(flag);
	return response;
    }

}
