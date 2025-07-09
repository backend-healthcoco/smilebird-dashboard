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
import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.enums.PackageType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.PackageDetailObjectService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "Endpoint for Subscription")
@RequestMapping(value = PathProxy.SUBSCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class PackageDetailObjectApi {
	private static Logger logger = LogManager.getLogger(PackageDetailObjectApi.class.getName());

	@Autowired
	private PackageDetailObjectService packageDetailObjectService;

	@PostMapping(value = PathProxy.PackageDetaiUrls.ADD_EDIT_PACKAGES)
	@ApiOperation(value = PathProxy.PackageDetaiUrls.ADD_EDIT_PACKAGES, notes = PathProxy.PackageDetaiUrls.ADD_EDIT_PACKAGES)
	public Response<PackageDetailObject> addEditPackageDetailObject(@RequestBody PackageDetailObject request) {
	
		Response<PackageDetailObject> response = new Response<PackageDetailObject>();
		response.setData(packageDetailObjectService.addEditPackageDetailObject(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.PackageDetaiUrls.GET_PACKAGES)
	@ApiOperation(value = PathProxy.PackageDetaiUrls.GET_PACKAGES, notes = PathProxy.PackageDetaiUrls.GET_PACKAGES)
	public Response<PackageDetailObject> getPackagesList(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = packageDetailObjectService.countPackages(isDiscarded, searchTerm);
		Response<PackageDetailObject> response = new Response<PackageDetailObject>();
		if (count > 0)
			response.setDataList(packageDetailObjectService.getPackages(size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;
	}	
	
	@GetMapping(value = PathProxy.PackageDetaiUrls.GET_PACKAGES_BY_NAME)
	@ApiOperation(value = PathProxy.PackageDetaiUrls.GET_PACKAGES_BY_NAME, notes = PathProxy.PackageDetaiUrls.GET_PACKAGES_BY_NAME)
	public Response<PackageDetailObject> getPackageDetailByPackageName(@RequestParam(required = true, value = "packageName") PackageType packageName) {
		if (packageName == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<PackageDetailObject> response = new Response<PackageDetailObject>();
		response.setData(packageDetailObjectService.getPackageDetailByPackageName(packageName));
		return response;

	}
	
	@DeleteMapping(value = PathProxy.PackageDetaiUrls.DELETE_PACKAGE)
	@ApiOperation(value = PathProxy.PackageDetaiUrls.DELETE_PACKAGE, notes = PathProxy.PackageDetaiUrls.DELETE_PACKAGE)
	public Response<Boolean> discardPackageDetail(@PathVariable("id") String id,
			@RequestParam(required = false, value ="isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(packageDetailObjectService.deletePackageDetail(id, isDiscarded));
		return response;
	}

	
}
