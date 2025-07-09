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

import com.dpdocter.beans.Language;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LanguageServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.LANGUAGE_BASE_URL, description = "Endpoint for Language Api")
@RequestMapping(value=PathProxy.LANGUAGE_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class LanguageApi {
	
	private static Logger logger = LogManager.getLogger(LanguageApi.class.getName());
	 
	 @Autowired
	 private LanguageServices languageService;
	 
	 @PostMapping(value=PathProxy.LanguageUrls.ADD_EDIT_LANGUAGE)
		@ApiOperation(value = PathProxy.LanguageUrls.ADD_EDIT_LANGUAGE, notes = PathProxy.LanguageUrls.ADD_EDIT_LANGUAGE)
		public Response<Language> addEditLanguage(@RequestBody Language request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Language> response = new Response<Language>();
		response.setData(languageService.addEditLanguage(request));
		return response;
		}
	 
	 
	 	@GetMapping(value=PathProxy.LanguageUrls.GET_LANGUAGE_BY_ID)
		@ApiOperation(value = PathProxy.LanguageUrls.GET_LANGUAGE_BY_ID, notes = PathProxy.LanguageUrls.GET_LANGUAGE_BY_ID)
		public Response<Language> getLanguage(@PathVariable("id")String id)
		{
			
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Language> response = new Response<Language>();
		response.setData(languageService.getLanguage(id));
		return response;
		}
		
		@GetMapping(value = PathProxy.LanguageUrls.GET_LANGUAGES)
		@ApiOperation(value = PathProxy.LanguageUrls.GET_LANGUAGES, notes = PathProxy.LanguageUrls.GET_LANGUAGES)
		public Response<Language> getLanguages(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm) {
			Integer count = languageService.countLanguage(discarded, searchTerm);
			Response<Language> response = new Response<Language>();
			
				response.setDataList(languageService.getLanguages(size, page, discarded, searchTerm));
			response.setCount(count);
			return response;
		}
		
		@DeleteMapping(value=PathProxy.LanguageUrls.DELETE_LANGUAGE)
		@ApiOperation(value = PathProxy.LanguageUrls.DELETE_LANGUAGE, notes = PathProxy.LanguageUrls.DELETE_LANGUAGE)
		public Response<Language> deleteLanguage(@PathVariable("id")String id)
		{
			
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Language> response = new Response<Language>();
		response.setData(languageService.deleteLanguageById(id));
		return response;
		}

}
