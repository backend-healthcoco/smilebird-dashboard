package com.dpdocter.webservices;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.PackageDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.PackageDetailServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "Endpoint for Country")
@RequestMapping(value=PathProxy.SUBSCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class PackageDetailsApi {
	
	 private static Logger logger = LogManager.getLogger(PackageDetailsApi.class.getName());

	 @Autowired
	 PackageDetailServices packageDetailServices;
	 
	 @PostMapping(value=PathProxy.PackageUrls.ADD_EDIT_PACKAGE)
		@ApiOperation(value = PathProxy.PackageUrls.ADD_EDIT_PACKAGE, notes = PathProxy.PackageUrls.ADD_EDIT_PACKAGE)
		public Response<PackageDetail> addEditPackageDetail(@RequestBody PackageDetail request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<PackageDetail> response = new Response<PackageDetail>();
		response.setData(packageDetailServices.addEditPackageDetail(request));
		return response;
		}
		
		@GetMapping(value = PathProxy.PackageUrls.GET_PACKAGE)
		@ApiOperation(value = PathProxy.PackageUrls.GET_PACKAGE, notes = PathProxy.PackageUrls.GET_PACKAGE)
		public Response<PackageDetail> getPackageDetails(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded",defaultValue = "false") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm",defaultValue = "") String searchTerm) {
			Integer count = packageDetailServices.countPackageDetails(discarded, searchTerm);
			Response<PackageDetail> response = new Response<PackageDetail>();
			if (count > 0)
				response.setDataList(packageDetailServices.getPackageDetails(size, page, discarded, searchTerm));
			response.setCount(count);
			return response;
		}
		
		


	
		
		@DeleteMapping(value = PathProxy.PackageUrls.DELETE_PACKAGE)
		@ApiOperation(value = PathProxy.PackageUrls.DELETE_PACKAGE, notes = PathProxy.PackageUrls.DELETE_PACKAGE)
		public Response<PackageDetail> discardPackageDetail(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<PackageDetail> response = new Response<PackageDetail>();
			response.setData(packageDetailServices.deletePackageDetail(id, discarded));
			return response;
		}

}
