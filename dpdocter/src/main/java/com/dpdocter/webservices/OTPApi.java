package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.User;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.OTPService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.OTP_BASE_URL, produces = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.OTP_BASE_URL, description = "Endpoint for otp")
public class OTPApi {

	private static Logger logger = LogManager.getLogger(OTPApi.class.getName());

	@Autowired
	private OTPService otpService;

	@GetMapping(value = PathProxy.OTPUrls.OTP_GENERATOR)
	@ApiOperation(value = PathProxy.OTPUrls.OTP_GENERATOR, notes = PathProxy.OTPUrls.OTP_GENERATOR)
	public Response<String> otpGenerator(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input. DoctorId, LocationId, HospitalId, PatientId Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. DoctorId, LocationId, HospitalId, PatientId, Cannot Be Empty");
		}
		String OTP = otpService.otpGenerator(doctorId, locationId, hospitalId, patientId);
		Response<String> response = new Response<String>();
		response.setData(OTP);
		return response;
	}

	@GetMapping(value = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE)
	@ApiOperation(value = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE, notes = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE)
	public Response<Boolean> otpGenerator(@PathVariable("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn("Invalid Input. Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty");
		}
		Boolean OTP = otpService.otpGenerator(mobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(OTP);
		return response;
	}

	@GetMapping(value = PathProxy.OTPUrls.VERIFY_OTP)
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP, notes = PathProxy.OTPUrls.VERIFY_OTP)
	public Response<Boolean> verifyOTP(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId, @PathVariable("otpNumber") String otpNumber) {
		if (DPDoctorUtils.anyStringEmpty(otpNumber, doctorId, locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
		}
		Boolean verifyOTPResponse = otpService.verifyOTP(doctorId, locationId, hospitalId, patientId, otpNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyOTPResponse);
		return response;
	}

	@GetMapping(value = PathProxy.OTPUrls.VERIFY_OTP_MOBILE)
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP_MOBILE, notes = PathProxy.OTPUrls.VERIFY_OTP_MOBILE)
	public Response<Boolean> verifyOTP(@PathVariable("mobileNumber") String mobileNumber,
			@PathVariable("otpNumber") String otpNumber) {
		if (DPDoctorUtils.anyStringEmpty(otpNumber, mobileNumber)) {
			logger.warn("Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
		}
		Boolean verifyOTPResponse = otpService.verifyOTP(mobileNumber, otpNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyOTPResponse);
		return response;
	}

	@GetMapping(value = PathProxy.OTPUrls.VERIFY_ADMIN_OTP)
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_ADMIN_OTP, notes = PathProxy.OTPUrls.VERIFY_ADMIN_OTP)
	public Response<User> verifyAdminOTP(@PathVariable("mobileNumber") String mobileNumber,
			@PathVariable("otpNumber") String otpNumber) {
		if (DPDoctorUtils.anyStringEmpty(otpNumber, mobileNumber)) {
			logger.warn("Invalid Input. mobileNumber , OTP Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. mobileNumber , OTP Number Cannot Be Empty");
		}
		User verifyOTPResponse = otpService.verifyOTPForAdmin(mobileNumber, otpNumber);
		Response<User> response = new Response<User>();
		response.setData(verifyOTPResponse);
		return response;
	}

}
