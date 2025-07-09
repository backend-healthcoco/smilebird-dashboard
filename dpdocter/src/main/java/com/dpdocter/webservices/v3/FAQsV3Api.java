package com.dpdocter.webservices.v3;

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

import com.dpdocter.beans.FAQsResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.FAQS_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.FAQS_BASE_URL, description = "Endpoint for faqs")
public class FAQsV3Api {
	
	private static Logger logger = LogManager.getLogger(FAQsV3Api.class.getName());

	@Autowired
	FAQsV3Service faqsV3Service;
	
	@PostMapping(value = PathProxy.FaqsUrls.ADD_EDIT_FAQS)
	@ApiOperation(value = PathProxy.FaqsUrls.ADD_EDIT_FAQS, notes = PathProxy.FaqsUrls.ADD_EDIT_FAQS)
	public Response<FAQsResponse> addEditFaqsReasons(@RequestBody FAQsResponse request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		FAQsResponse faqsResponse = faqsV3Service.addEditFaqsReasons(request);

		Response<FAQsResponse> response = new Response<FAQsResponse>();
		response.setData(faqsResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.FaqsUrls.DELETE_FAQS)
	@ApiOperation(value = PathProxy.FaqsUrls.DELETE_FAQS, notes = PathProxy.FaqsUrls.DELETE_FAQS)
	public Response<Boolean> deleteFaqsById(@PathVariable(value = "id") String id,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(faqsV3Service.deleteFaqsById(id, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.FaqsUrls.GET_FAQS)
	@ApiOperation(value = PathProxy.FaqsUrls.GET_FAQS, notes = PathProxy.FaqsUrls.GET_FAQS)
	public Response<Object> getFaqsList(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = faqsV3Service.getFaqsList(page, size);
		return response;
	}

}
