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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Country;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.CuminLinkRequest;
import com.dpdocter.request.RegistrationUserRequest;
import com.dpdocter.services.CuminService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CUMIN_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CUMIN_BASE_URL, description = "Endpoint for dental lab")
public class CuminApi {

	private static Logger logger = LogManager.getLogger(CountryApi.class.getName());

	@Autowired
	private CuminService cuminService;
	
	@PostMapping(value = PathProxy.CuminUrls.ADD_GROUP)
	@ApiOperation(value = PathProxy.CuminUrls.ADD_GROUP, notes = PathProxy.CuminUrls.ADD_GROUP)
	public Response<Boolean> addEditCountry(@RequestBody CuminLinkRequest request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(cuminService.addEditGroupLink(request));
		return response;
	}
	
	@PostMapping(value = PathProxy.CuminUrls.USER_REGISTRATION)
	@ApiOperation(value = PathProxy.CuminUrls.USER_REGISTRATION, notes = PathProxy.CuminUrls.USER_REGISTRATION)
	public Response<Boolean> Registration(@RequestBody RegistrationUserRequest request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(cuminService.userRegistration(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.CuminUrls.GET_REGISTER_USER)
	@ApiOperation(value = PathProxy.CuminUrls.GET_REGISTER_USER, notes = PathProxy.CuminUrls.GET_REGISTER_USER)
	public Response<Country> getCountry(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Response<Country> response = new Response<Country>();
			response.setDataList(cuminService.getCuminUserList(size, page, isDiscarded, searchTerm));
		return response;
	}
	
	@GetMapping(value = PathProxy.CuminUrls.UPDATE_ISCUMIN_DOCTOR)
	@ApiOperation(value = PathProxy.CuminUrls.UPDATE_ISCUMIN_DOCTOR, notes = PathProxy.CuminUrls.UPDATE_ISCUMIN_DOCTOR)
	public Response<Boolean> updateDoctorClinicIsListed(@PathVariable("locationId") String locationId,
			@RequestParam(required = true, value ="doctorId") String doctorId, 
	        @RequestParam(required = false, value ="isCuminExpert", defaultValue = "true") Boolean isCuminExpert) {
		Boolean status = cuminService.updateDoctorToCuminExpert(locationId, doctorId, isCuminExpert);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}
}
