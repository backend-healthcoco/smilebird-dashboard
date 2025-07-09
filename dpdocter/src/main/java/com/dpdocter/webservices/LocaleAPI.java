package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DataCount;
import com.dpdocter.beans.Locale;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditLocaleAddressDetailsRequest;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.services.LocaleService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.LOCALE_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LOCALE_BASE_URL, description = "Endpoint for locale")
public class LocaleAPI {

	@Autowired
	LocaleService localeService;

	@Value(value = "${invalid.input}")
	private String invalidInput;

	private static final Logger logger = LogManager.getLogger(LocaleAPI.class.getName());

	@GetMapping(PathProxy.LocaleUrls.GET_LOCALE_LIST)
	public Response<Object> getLocaleList(@RequestParam(required = false, value = "page", defaultValue="0") int page, @RequestParam(required = false, value = "size", defaultValue="0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm, @RequestParam(required = false, value ="isListed") Boolean isListed) {
		List<Locale> locales = localeService.getLocaleList(page, size, searchTerm, isListed);
		Response<Object> response = new Response<Object>();
		response.setData(localeService.countLocaleList(searchTerm, isListed));
		response.setDataList(locales);
		return response;
	}

	@GetMapping(value = PathProxy.LocaleUrls.ACTIVATE_DEACTIVATE_LOCALE)
	@ApiOperation(value = PathProxy.LocaleUrls.ACTIVATE_DEACTIVATE_LOCALE, notes = PathProxy.LocaleUrls.ACTIVATE_DEACTIVATE_LOCALE)
	public Response<Locale> activateLocale(@PathVariable(value = "id") String id,
			@RequestParam(required = false, value ="activate", defaultValue = "true") Boolean activate) {
		if (id == null) {
			// logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Locale locale = localeService.activateDeactivateLocale(id, activate);

		Response<Locale> response = new Response<Locale>();
		response.setData(locale);
		return response;

	}

	@GetMapping(PathProxy.LocaleUrls.GET_LOCALE)
	public Response<Locale> getLocaleDetails(@PathVariable("id") String id) {
		Response<Locale> response = null;
		Locale locale = null;

		if (DPDoctorUtils.allStringsEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}

		locale = localeService.getLocaleDetails(id);
		response = new Response<Locale>();
		response.setData(locale);

		return response;
	}

	@PostMapping(PathProxy.LocaleUrls.EDIT_LOCALE_ADDRESS_DETAILS)
	public Response<Locale> updateLocaleAddress(@RequestBody AddEditLocaleAddressDetailsRequest request) {
		Response<Locale> response = null;
		Locale locale = null;
		try {
			if (request == null || request.getId() == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			locale = localeService.updateLocaleAddress(request);
			response = new Response<Locale>();
			response.setData(locale);

		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;

	}

	@PostMapping(PathProxy.LocaleUrls.EDIT_LOCALE_DETAILS)
	public Response<Locale> editLocaleprofile(@RequestBody Locale request) {
		Response<Locale> response = null;
		Locale locale = null;
		try {
			if (request == null || request.getId() == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			locale = localeService.editLocaleprofile(request);
			response = new Response<Locale>();
			response.setData(locale);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;

	}

	@PostMapping(value = PathProxy.LocaleUrls.UPLOAD)
	@ApiOperation(value = PathProxy.LocaleUrls.UPLOAD, notes = PathProxy.LocaleUrls.UPLOAD)
	public Response<Boolean> addLocaleImage(@RequestBody ClinicImageAddRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (request.getImages() == null) {
			logger.warn("Invalid Input. Request Image Is Null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Image Is Null");
		} else if (request.getImages().size() > 5) {
			logger.warn("More than 5 images cannot be uploaded at a time");
			throw new BusinessException(ServiceError.NotAcceptable, "More than 5 images cannot be uploaded at a time");
		}
		Response<Boolean> response = new Response<Boolean>();

		response.setData(localeService.addLocaleImage(request));
		return response;
	}

	@DeleteMapping(PathProxy.LocaleUrls.DELETE_LOCALE_IMAGE)
	@ApiOperation(value = PathProxy.LocaleUrls.DELETE_LOCALE_IMAGE, notes = PathProxy.LocaleUrls.DELETE_LOCALE_IMAGE)
	public Response<Boolean> deleteLocaleImage(@PathVariable("localeId") String localeId,
			@MatrixParam("imageIds") List<String> imageIds) {
		Response<Boolean> response = null;
		if (DPDoctorUtils.anyStringEmpty(localeId) || imageIds.isEmpty() || imageIds == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		Boolean status = localeService.deleteLocaleImage(localeId, imageIds);
		if (status != null) {
			response = new Response<Boolean>();
			response.setData(status);
		}

		return response;
	}

	@GetMapping(PathProxy.LocaleUrls.COUNT_LOCALE)
	@ApiOperation(value = PathProxy.LocaleUrls.COUNT_LOCALE, notes = PathProxy.LocaleUrls.COUNT_LOCALE)
	public Response<DataCount> getLocaleCount() {
		Response<DataCount> response = null;
		DataCount dataCount = localeService.getLocaleCount();
		response = new Response<DataCount>();
		response.setData(dataCount);

		return response;
	}

	
}
