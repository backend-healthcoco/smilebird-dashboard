package com.dpdocter.webservices;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.CompanyLocation;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.CompanyLocationService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@RequestMapping(value = PathProxy.COMPANY_LOCATION_BASE_URL, consumes = "application/json", produces = "application/json")
@Api(value = PathProxy.COMPANY_LOCATION_BASE_URL, description = "Endpoint for analytics")
public class CompanyLocationAPI {
	private static Logger logger = LogManager.getLogger(CompanyLocationAPI.class.getName());

	@Autowired
	private CompanyLocationService companyLocationService;

	@PostMapping(value = PathProxy.CompanyLocationUrls.ADD_EDIT_COMPANY_LOCATION)
	@ApiOperation(value = PathProxy.CompanyLocationUrls.ADD_EDIT_COMPANY_LOCATION, notes = PathProxy.CompanyLocationUrls.ADD_EDIT_COMPANY_LOCATION)
	@ResponseBody
	public Response<CompanyLocation> addCompanyLocation(@RequestBody CompanyLocation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getLocationName(), request.getCompanyName())) {
			logger.warn("Location Name ,Comapany Name should not be Null or empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Location Name ,Comapany Name should not be Null or empty");
		}

		CompanyLocation CompanyLocation = companyLocationService.addCompanyLocation(request);
		Response<CompanyLocation> response = new Response<CompanyLocation>();
		response.setData(CompanyLocation);
		return response;
	}

	@GetMapping(value = PathProxy.CompanyLocationUrls.GET_COMPANY_LOCATIONS)
	@ApiOperation(value = PathProxy.CompanyLocationUrls.GET_COMPANY_LOCATIONS, notes = PathProxy.CompanyLocationUrls.GET_COMPANY_LOCATIONS)
	@ResponseBody
	public Response<CompanyLocation> getCompanyLocations(@PathVariable("companyId") String companyId,
			@RequestParam(value = "size", defaultValue = "0") int size,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "searchTerm", required = false, defaultValue = "") String searchTerm,
			@RequestParam(value = "isDiscarded", required = false, defaultValue = "false") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(companyId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<CompanyLocation> companyLocations = null;

		Integer count = companyLocationService.countCompanyLocation(companyId, isDiscarded, searchTerm);
		companyLocations = companyLocationService.getCompanyLocationList(page, size, searchTerm, isDiscarded,
				companyId);
		Response<CompanyLocation> response = new Response<CompanyLocation>();
		response.setDataList(companyLocations);
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.CompanyLocationUrls.DELETE_COMPANY_LOCATION)
	@ApiOperation(value = PathProxy.CompanyLocationUrls.DELETE_COMPANY_LOCATION, notes = PathProxy.CompanyLocationUrls.DELETE_COMPANY_LOCATION)
	@ResponseBody
	public Response<Boolean> deleteCompanyLocation(@PathVariable("id") String id,
			@RequestParam(value = "isDiscarded", required = false) boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Boolean status = companyLocationService.discardCompanyLocation(id, isDiscarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}
	

	@GetMapping(value = PathProxy.CompanyLocationUrls.GET_EMPLOYEE_COUNT_BY_LOCATION)
	@ApiOperation(value = PathProxy.CompanyLocationUrls.GET_EMPLOYEE_COUNT_BY_LOCATION, notes = PathProxy.CompanyLocationUrls.GET_EMPLOYEE_COUNT_BY_LOCATION)
	@ResponseBody
	public Response<Integer> getEmployeeCount(@RequestParam(value = "companyId", required = true) String companyId,
			@RequestParam(value = "companylocationId", required = false) String companylocationId) {
		if (DPDoctorUtils.anyStringEmpty(companyId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
	

		Integer count = companyLocationService.getTotalEmployeeCountByLocationId(companyId, companylocationId);
		
		Response<Integer> response = new Response<Integer>();
		response.setCount(count);
		return response;
	}


}
