package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BankDetails;
import com.dpdocter.beans.DoctorOnlineConsultation;
import com.dpdocter.beans.OnlineConsultationPayment;

import com.dpdocter.beans.OnlineConsultationSpecialityPrice;
import com.dpdocter.beans.TransactionStatusRequest;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.OnlineConsultationAccountRequest;
import com.dpdocter.request.RazorPayAccountRequest;
import com.dpdocter.response.RazorPayAccountResponse;
import com.dpdocter.services.OnlineConsultationPaymentServices;
import com.razorpay.RazorpayException;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.ONLINE_CONSULTATION_PAYMENT_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ONLINE_CONSULTATION_PAYMENT_BASE_URL, description = "Endpoint for doctor profile")
public class OnlineConsultationPaymentApi {
private static Logger logger = LogManager.getLogger(OnlineConsultationPaymentApi.class.getName());
	
	@Autowired
	private OnlineConsultationPaymentServices onlineConsultationServices;
	
	@PostMapping(value=PathProxy.OnlinConsultationPaymentUrls.EDIT_ONLINE_CONSULTATION_PAYMENT)
	@ApiOperation(value = PathProxy.OnlinConsultationPaymentUrls.EDIT_ONLINE_CONSULTATION_PAYMENT, notes = PathProxy.OnlinConsultationPaymentUrls.EDIT_ONLINE_CONSULTATION_PAYMENT)
	public Response<Boolean> EditDoctorOnlineConsultationPaymentDetails(@RequestBody OnlineConsultationAccountRequest request)
	{
		
	if (request == null) {
		logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	onlineConsultationServices.editPaymentDetail(request);
	Response<Boolean> response = new Response<Boolean>();
	
	response.setData(true);
	return response;
	}
	
	@GetMapping(value = PathProxy.OnlinConsultationPaymentUrls.GET_BANK_DETAILS)
	@ApiOperation(value = PathProxy.OnlinConsultationPaymentUrls.GET_BANK_DETAILS, notes = PathProxy.OnlinConsultationPaymentUrls.GET_BANK_DETAILS)
	public Response<Object> getBankDetails(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="discarded",defaultValue = "false" ) Boolean discarded, 
			@RequestParam(required = false, value ="searchTerm") String searchTerm,
			@RequestParam(required = false, value ="city") String city,
			@RequestParam(required = false, value ="specialities") String specialities,
			@RequestParam(required = false, value ="isRegistrationDetailsVerified") Boolean isRegistrationDetailsVerified,
			@RequestParam(required = false, value ="isPhotoIdVerified")Boolean isPhotoIdVerified,
			@RequestParam(required = false, value ="fromDate")String fromDate,
			@RequestParam(required = false, value ="toDate")String toDate) {

		Response<Object> response = onlineConsultationServices.getBankDetailsList(page, size, searchTerm, city, specialities, isRegistrationDetailsVerified, isPhotoIdVerified,fromDate,toDate);
	
		return response;
	}
	
	@PostMapping(value = PathProxy.OnlinConsultationPaymentUrls.CREATE_ACCOUNT)
	@ApiOperation(value = PathProxy.OnlinConsultationPaymentUrls.CREATE_ACCOUNT, notes = PathProxy.OnlinConsultationPaymentUrls.CREATE_ACCOUNT)
		@ResponseBody
	public Response<RazorPayAccountResponse> createRayzorPayAccount(@RequestBody RazorPayAccountRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Response<RazorPayAccountResponse> response = new Response<RazorPayAccountResponse>();
		response.setData(onlineConsultationServices.createAccount(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.OnlinConsultationPaymentUrls.GET_DOCTOR_CONSULTATIONS)
	@ApiOperation(value = PathProxy.OnlinConsultationPaymentUrls.GET_DOCTOR_CONSULTATIONS, notes = PathProxy.OnlinConsultationPaymentUrls.GET_DOCTOR_CONSULTATIONS)
	public Response<DoctorOnlineConsultation> getDoctorConsultion(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="discarded",defaultValue = "false" ) Boolean discarded, 
			@RequestParam(required = false, value ="searchTerm") String searchTerm,
			@RequestParam(required = false, value ="city") String city,
			@RequestParam(required = false, value ="transactionStatus") String transactionStatus,
			@RequestParam(required = false, value ="specialities") String specialities,
			@RequestParam(required = false, value ="fromDate")String fromDate,
			@RequestParam(required = false, value ="toDate")String toDate) {

		List<DoctorOnlineConsultation> doctorOnline = onlineConsultationServices.getDoctorOnlineConsultationInfo(page, size, searchTerm, city, specialities,transactionStatus, fromDate, toDate);
		Response<DoctorOnlineConsultation> response = new Response<DoctorOnlineConsultation>();
		response.setDataList(doctorOnline);

		
		return response;
	}

	
	@PostMapping(value = PathProxy.OnlinConsultationPaymentUrls.UPDATE_TRANSACTION_STATUS)
	@ApiOperation(value = PathProxy.OnlinConsultationPaymentUrls.UPDATE_TRANSACTION_STATUS, notes = PathProxy.OnlinConsultationPaymentUrls.UPDATE_TRANSACTION_STATUS)
	public Response<Boolean> updateTransactionStatus( @RequestBody TransactionStatusRequest request) {

		Boolean doctorOnline = onlineConsultationServices.updateTransactionStatus(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorOnline);

		
		return response;
	}


	
	@PostMapping(value = PathProxy.OnlinConsultationPaymentUrls.ADD_EDIT_ONLINE_CONSULTATION_SPECIALITY_PRICE)
	@ApiOperation(value = PathProxy.OnlinConsultationPaymentUrls.ADD_EDIT_ONLINE_CONSULTATION_SPECIALITY_PRICE, notes = PathProxy.OnlinConsultationPaymentUrls.ADD_EDIT_ONLINE_CONSULTATION_SPECIALITY_PRICE)

	public Response<OnlineConsultationSpecialityPrice> addDoctorOnlineConsultationPrice(@RequestBody OnlineConsultationSpecialityPrice request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Response<OnlineConsultationSpecialityPrice> response = new Response<OnlineConsultationSpecialityPrice>();
		response.setData(onlineConsultationServices.addEditPrice(request));
		return response;
	}

	@GetMapping(value = PathProxy.OnlinConsultationPaymentUrls.GET_ONLINE_CONSULTATION_SPECIALITY_PRICE)
	@ApiOperation(value = PathProxy.OnlinConsultationPaymentUrls.GET_ONLINE_CONSULTATION_SPECIALITY_PRICE, notes = PathProxy.OnlinConsultationPaymentUrls.GET_ONLINE_CONSULTATION_SPECIALITY_PRICE)
	public Response<OnlineConsultationSpecialityPrice> getOnlineConsultationSpecialityPrice(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="searchTerm") String searchTerm) {

		List<OnlineConsultationSpecialityPrice> doctorOnline = onlineConsultationServices.getDoctorOnlineConsultationPriceList(page, size, searchTerm);
		Response<OnlineConsultationSpecialityPrice> response = new Response<OnlineConsultationSpecialityPrice>();
		response.setDataList(doctorOnline);

		
		return response;
	}

	

}
