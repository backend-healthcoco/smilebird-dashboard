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

import com.dpdocter.beans.AppointmentPaymentInfo;
import com.dpdocter.beans.DoctorOnlineConsultation;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AppointmentPaymentTransferRequest;
import com.dpdocter.response.AppointmentResponse;
import com.dpdocter.services.AppointmentPaymentService;
import com.dpdocter.services.OnlineConsultationPaymentServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(value=PathProxy.APPOINTMENT_PAYMENT_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.APPOINTMENT_PAYMENT_BASE_URL, description = "Endpoint for doctor profile")

public class AppointmentPaymentApi {

	
private static Logger logger = LogManager.getLogger(AppointmentPaymentApi.class.getName());
	
	@Autowired
	private AppointmentPaymentService appointmentPaymentService;

	
	@GetMapping(value = PathProxy.AppointmentPaymentUrls.GET_ANONYMOUS_APPOINTMENT_LIST)
	@ApiOperation(value = PathProxy.AppointmentPaymentUrls.GET_ANONYMOUS_APPOINTMENT_LIST, notes = PathProxy.AppointmentPaymentUrls.GET_ANONYMOUS_APPOINTMENT_LIST)
	public Response<AppointmentPaymentInfo> getDoctorConsultion(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="discarded",defaultValue = "false" ) Boolean discarded, 
			@RequestParam(required = false, value ="searchTerm") String searchTerm,
			@RequestParam(required = false, value ="city") String city,
			@RequestParam(required = false, value ="transactionStatus") String transactionStatus,
			@RequestParam(required = false, value ="specialities") String specialities,
			@RequestParam(required = false, value ="fromDate")String fromDate,
			@RequestParam(required = false, value ="toDate")String toDate) {

		List<AppointmentPaymentInfo> doctorOnline = appointmentPaymentService.getAppointmentPaymentInfo(page, size, searchTerm, city, specialities, transactionStatus, fromDate, toDate);
		Response<AppointmentPaymentInfo> response = new Response<AppointmentPaymentInfo>();
		response.setDataList(doctorOnline);

		
		return response;
	}
	
	
	@PostMapping(PathProxy.AppointmentPaymentUrls.CREATE_TRANSFER)
	@ApiOperation(value = PathProxy.AppointmentPaymentUrls.CREATE_TRANSFER, notes = PathProxy.AppointmentPaymentUrls.CREATE_TRANSFER)
	@ResponseBody
	public Response<Boolean> createPaymentTransfer(@RequestBody AppointmentPaymentTransferRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid accountId");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid doctorId");
		}
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(appointmentPaymentService.createPaymentTransfer(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.AppointmentPaymentUrls.GET_PATIENT_APPOINTMENT_REQUEST)
	@ApiOperation(value = PathProxy.AppointmentPaymentUrls.GET_PATIENT_APPOINTMENT_REQUEST, notes = PathProxy.AppointmentPaymentUrls.GET_PATIENT_APPOINTMENT_REQUEST)
	public Response<AppointmentResponse> getPatientAppointRequests(
			@RequestParam(required = false, value = "searchTerm") String searchTerm, 
			@RequestParam(required = false, value = "transactionStatus") String transactionStatus, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		
			List<AppointmentResponse>appointment =	appointmentPaymentService.getAppointmentRequest(size, page, searchTerm,transactionStatus);
		
			Response<AppointmentResponse> response = new Response<AppointmentResponse>();
			response.setDataList(appointment);
			return response;
	}

}
