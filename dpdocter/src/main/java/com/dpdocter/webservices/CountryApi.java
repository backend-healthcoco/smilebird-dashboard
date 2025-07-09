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

import com.dpdocter.beans.Country;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.CountryService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "Endpoint for Country")
@RequestMapping(value = PathProxy.SUBSCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class CountryApi {

	private static Logger logger = LogManager.getLogger(CountryApi.class.getName());

	@Autowired
	private CountryService countryService;

	@PostMapping(value = PathProxy.CountryUrls.ADD_EDIT_COUNTRY)
	@ApiOperation(value = PathProxy.CountryUrls.ADD_EDIT_COUNTRY, notes = PathProxy.CountryUrls.ADD_EDIT_COUNTRY)
	public Response<Country> addEditCountry(@RequestBody Country request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Country> response = new Response<Country>();
		response.setData(countryService.addEditCountry(request));
		return response;
	}

	@GetMapping(value = PathProxy.CountryUrls.GET_COUNTRY)
	@ApiOperation(value = PathProxy.CountryUrls.GET_COUNTRY, notes = PathProxy.CountryUrls.GET_COUNTRY)
	public Response<Country> getCountry(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = countryService.countCountry(isDiscarded, searchTerm);
		Response<Country> response = new Response<Country>();
		if (count > 0)
			response.setDataList(countryService.getCountry(size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@GetMapping(value = PathProxy.CountryUrls.GET_COUNTRY_BY_ID)
	@ApiOperation(value = PathProxy.CountryUrls.GET_COUNTRY_BY_ID, notes = PathProxy.CountryUrls.GET_COUNTRY_BY_ID)
	public Response<Country> getCountryById(@PathVariable("id") String id) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Country> response = new Response<Country>();
		response.setData(countryService.getCountryById(id));
		return response;

	}

	@DeleteMapping(value = PathProxy.CountryUrls.DELETE_COUNTRY)
	@ApiOperation(value = PathProxy.CountryUrls.DELETE_COUNTRY, notes = PathProxy.CountryUrls.DELETE_COUNTRY)
	public Response<Boolean> discardCountry(@PathVariable("id") String id,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(countryService.deleteCountry(id, isDiscarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.CountryUrls.GET_COLLECTION)
	@ApiOperation(value = PathProxy.CountryUrls.GET_COLLECTION, notes = PathProxy.CountryUrls.GET_COLLECTION)
	public Response<Boolean> discardCountry(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
//		if (DPDoctorUtils.anyStringEmpty(id)) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//
//		}
		Response<Boolean> response = new Response<Boolean>();
		response.setDataList(countryService.getgenericcollection(0, 0, isDiscarded, searchTerm));
		return response;
	}
	
	@GetMapping(value = PathProxy.CountryUrls.GET_DRUGCOLLECTION)
	@ApiOperation(value = PathProxy.CountryUrls.GET_DRUGCOLLECTION, notes = PathProxy.CountryUrls.GET_DRUGCOLLECTION)
	public Response<Boolean> DrugInCountry(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
//		if (DPDoctorUtils.anyStringEmpty(id)) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//
//		}
		Response<Boolean> response = new Response<Boolean>();
		response.setDataList(countryService.getDrugInfocollection(0, 0, isDiscarded, searchTerm));
		return response;
	}
}
