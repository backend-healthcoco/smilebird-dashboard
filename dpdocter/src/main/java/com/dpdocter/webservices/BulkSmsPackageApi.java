package com.dpdocter.webservices;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BankDetails;
import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsCreditsRequest;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.BulkSmsReport;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.OnlineConsultationAccountRequest;
import com.dpdocter.services.BulkSmsPackageServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.BULK_SMS_PACKAGE_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BULK_SMS_PACKAGE_BASE_URL, description = "Endpoint for doctor profile")
public class BulkSmsPackageApi {

	private static Logger logger = LogManager.getLogger(BulkSmsPackageApi.class.getName());
	
	@Autowired
	private BulkSmsPackageServices bulkSmsServices;

	@PostMapping(value=PathProxy.BulkSmsPackageUrls.ADD_EDIT_PACKAGE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.ADD_EDIT_PACKAGE, notes = PathProxy.BulkSmsPackageUrls.ADD_EDIT_PACKAGE)
	public Response<BulkSmsPackage> addEditSmsPackage(@RequestBody BulkSmsPackage request)
	{	
	if (request == null) {
		logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	BulkSmsPackage bulkSms=bulkSmsServices.addEditBulkSmsPackage(request);
	Response<BulkSmsPackage> response = new Response<BulkSmsPackage>();
	
	response.setData(bulkSms);
	return response;
	}
	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	public Response<BulkSmsPackage> getBulkSmsPackages(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="discarded",defaultValue = "false" ) Boolean discarded, 
			@RequestParam(required = false, value ="searchTerm") String searchTerm) {

		Response<BulkSmsPackage> response = new Response<BulkSmsPackage>();
		
			response.setDataList(bulkSmsServices.getBulkSmsPackage(page, size, searchTerm, discarded));
	
		return response;
	}

	@DeleteMapping(value = PathProxy.BulkSmsPackageUrls.DELETE_SMS_PACKAGE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.DELETE_SMS_PACKAGE, notes = PathProxy.BulkSmsPackageUrls.DELETE_SMS_PACKAGE)
	public Response<BulkSmsPackage> deleteBulkSmsPackages(@RequestParam(required = false, value ="packageId") String packageId,
			@RequestParam(required = false, value ="discarded") Boolean discarded) {

		Response<BulkSmsPackage> response = new Response<BulkSmsPackage>();
		

		if(DPDoctorUtils.anyStringEmpty(packageId))

		{
			throw new BusinessException(ServiceError.InvalidInput, "PackageId is null");
		}
		
			response.setData(bulkSmsServices.deleteSmsPackage(packageId, discarded));
	
		return response;
	}

	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS, notes = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)
	public Response<BulkSmsCredits> getBulkSmsCredits(@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		
			response.setData(bulkSmsServices.getCreditsByDoctorId(doctorId));
	
		return response;
	}
	
	@PostMapping(value=PathProxy.BulkSmsPackageUrls.ADD_BULK_SMS_CREDITS)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.ADD_BULK_SMS_CREDITS, notes = PathProxy.BulkSmsPackageUrls.ADD_BULK_SMS_CREDITS)
	public Response<Boolean> addEditSmsCredits(@RequestBody BulkSmsCreditsRequest request)
	{	
	if (request == null) {
		logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Boolean bulkSms=bulkSmsServices.addBulkSmsCredits(request);
	Response<Boolean> response = new Response<Boolean>();
	
	response.setData(bulkSms);
	return response;
	}
	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	public Response<BulkSmsCredits> getBulkSmsHistory(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId,
			@RequestParam(required = false, value ="searchTerm") String searchTerm) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		
			response.setDataList(bulkSmsServices.getBulkSmsHistory(page, size, searchTerm, doctorId,locationId));
	
		return response;
	}
	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_REPORTS)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_REPORTS, notes = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_REPORTS)
	public Response<BulkSmsReport> getBulkSmsReport(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
			@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId) {

		Response<BulkSmsReport> response = new Response<BulkSmsReport>();
		if (doctorId == null && locationId==null) {
			logger.warn("doctorId or locationid  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(bulkSmsServices.getSmsReport(page, size, doctorId, locationId));
	
		return response;
	}
	
}
