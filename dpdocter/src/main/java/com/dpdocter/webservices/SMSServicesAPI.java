 package com.dpdocter.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSFormat;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.beans.WhatsAppMessageRequest;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.MessageRequest;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SMS_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SMS_BASE_URL, description = "Endpoint for sms")
public class SMSServicesAPI {
    private static Logger logger = LogManager.getLogger(SMSServicesAPI.class);

    @Autowired
    private SMSServices smsServices;

    @GetMapping(consumes = {MediaType.APPLICATION_JSON})
    @ApiOperation(value = "GET_SMS", notes = "GET_SMS")
    public Response<SMSResponse> getSMS(@RequestParam(required = false, value ="page", defaultValue = "0") int page, 
    		@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
    		@RequestParam(required = false, value = "doctorId") String doctorId,
	    @RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "hospitalId") String hospitalId) {
    	if(DPDoctorUtils.allStringsEmpty(doctorId, locationId)){
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	SMSResponse smsTrackDetails = smsServices.getSMS(page, size, doctorId, locationId, hospitalId);
	Response<SMSResponse> response = new Response<SMSResponse>();
	response.setData(smsTrackDetails);
	return response;
    }

    @GetMapping(value = PathProxy.SMSUrls.GET_SMS_DETAILS, consumes = {MediaType.APPLICATION_JSON})
    @ApiOperation(value = PathProxy.SMSUrls.GET_SMS_DETAILS, notes = PathProxy.SMSUrls.GET_SMS_DETAILS)
    public Response<SMSTrack> getSMSDetails(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value = "patientId") String patientId,
	    @RequestParam(required = false, value = "doctorId") String doctorId, @RequestParam(required = false, value = "locationId") String locationId,
	    @RequestParam(required = false, value = "hospitalId") String hospitalId) {
    	if(DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)){
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<SMSTrack> smsTrackDetails = smsServices.getSMSDetails(page, size, patientId, doctorId, locationId, hospitalId);
	Response<SMSTrack> response = new Response<SMSTrack>();
	response.setDataList(smsTrackDetails);
	return response;
    }

    @PostMapping(value = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS)
    @ApiOperation(value = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS, notes = PathProxy.SMSUrls.UPDATE_DELIVERY_REPORTS)
    public String updateDeliveryReports(@RequestBody String request) {

	try {
	    request = request.replaceFirst("data=", "");
	    ObjectMapper mapper = new ObjectMapper();
	    List<SMSDeliveryReports> list = mapper.readValue(request, TypeFactory.collectionType(List.class, SMSDeliveryReports.class));
	    smsServices.updateDeliveryReports(list);
	} catch (JsonParseException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	} catch (JsonMappingException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	} catch (IOException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	}
	return "true";
    }

    @GetMapping(value = PathProxy.SMSUrls.ADD_NUMBER, consumes = {MediaType.APPLICATION_JSON})
    @ApiOperation(value = PathProxy.SMSUrls.ADD_NUMBER, notes = PathProxy.SMSUrls.ADD_NUMBER)
    public Response<Boolean> addNumber(@PathVariable(value = "mobileNumber") String mobileNumber) {
	smsServices.addNumber(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @GetMapping(value = PathProxy.SMSUrls.DELETE_NUMBER, consumes = {MediaType.APPLICATION_JSON})
    @ApiOperation(value = PathProxy.SMSUrls.DELETE_NUMBER, notes = PathProxy.SMSUrls.DELETE_NUMBER)
    public Response<Boolean> deleteNumber(@PathVariable(value = "mobileNumber") String mobileNumber) {
	smsServices.deleteNumber(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @GetMapping(value = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT, consumes = {MediaType.APPLICATION_JSON})
    @ApiOperation(value = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT, notes = PathProxy.SMSUrls.ADD_EDIT_SMS_FORMAT)
    public Response<SMSFormat> addSmsFormat(@RequestBody SMSFormat request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),request.getHospitalId())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	SMSFormat smsFormat = smsServices.addSmsFormat(request);
	Response<SMSFormat> response = new Response<SMSFormat>();
	response.setData(smsFormat);
	return response;
    }

    @GetMapping(value = PathProxy.SMSUrls.GET_SMS_FORMAT, consumes = {MediaType.APPLICATION_JSON})
    @ApiOperation(value = PathProxy.SMSUrls.GET_SMS_FORMAT, notes = PathProxy.SMSUrls.GET_SMS_FORMAT)
    public Response<SMSFormat> getSmsFormat(@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
	    @PathVariable(value = "hospitalId") String hospitalId, @RequestParam(required = false, value = "type") String type) {
    	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<SMSFormat> smsFormat = smsServices.getSmsFormat(doctorId, locationId, hospitalId, type);
	Response<SMSFormat> response = new Response<SMSFormat>();
	response.setDataList(smsFormat);
	return response;
    }
    
    @GetMapping(value = PathProxy.SMSUrls.GET_SMS_STATUS, consumes = {MediaType.APPLICATION_JSON})
    @ApiOperation(value = PathProxy.SMSUrls.GET_SMS_STATUS, notes = PathProxy.SMSUrls.GET_SMS_STATUS)
    public Response<String> getSMSStatus() {
    
	String smsTrackDetails = smsServices.getSmsStatus();
	Response<String> response = new Response<String>();
	response.setData(smsTrackDetails);
	return response;
    }
    
    @PostMapping(value = PathProxy.SMSUrls.SEND_WHATSAPP_MESSAGE)
	@ApiOperation(value = PathProxy.SMSUrls.SEND_WHATSAPP_MESSAGE, notes = PathProxy.SMSUrls.SEND_WHATSAPP_MESSAGE)
	public Response<Boolean> sendSMS(@RequestBody WhatsAppMessageRequest request) {
		if (request.getMobileNumber().equals(null) &&  request.getMessage().equals(null)) {
			throw new BusinessException(ServiceError.InvalidInput, "mobileNumber,message should not null");
		}
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		SMSDetail smsDetail = new SMSDetail();
		smsTrackDetail.setType("WHATSAPP_MESSAGE");
		SMS sms = new SMS();
		// String message = invoiceRemainderSMS;

		

		sms.setSmsText(request.getMessage());

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(request.getMobileNumber());
		sms.setSmsAddress(smsAddress);
		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		//smsTrackDetail.setTemplateId("1307161641234614877");
		Boolean resp=smsServices.sendWhatsappMessage(smsTrackDetail, true);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(resp);
		return response;
	}
}
