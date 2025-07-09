package com.dpdocter.webservices;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ConsultationProblemDetailsRequest;
import com.dpdocter.services.ConsultationProblemDetailsService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.CONSULTATION_PROBLEM_DETAILS_BASE_URL, description = "Endpoint")
@RequestMapping(value = PathProxy.CONSULTATION_PROBLEM_DETAILS_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class ConsultationProblemDetailsApi {

	
private static Logger logger = LogManager.getLogger(ConsultationProblemDetailsApi.class.getName());
	
	@Autowired
	private ConsultationProblemDetailsService consultationProblemDetailsService;
	
	
	@PostMapping(value = PathProxy.ConsultationproblemDetailsUrls.ADD_EDIT_CONSULTATION_PROBLEM_DETAILS)
	@ApiOperation(value = PathProxy.ConsultationproblemDetailsUrls.ADD_EDIT_CONSULTATION_PROBLEM_DETAILS, notes = PathProxy.ConsultationproblemDetailsUrls.ADD_EDIT_CONSULTATION_PROBLEM_DETAILS)
	public Response<ConsultationProblemDetails> addEditConsultationProblemDetails(@RequestBody ConsultationProblemDetailsRequest request)
	{
		
	if (request == null) {
		logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	} 
	Response<ConsultationProblemDetails> response = new Response<ConsultationProblemDetails>();
	response.setData(consultationProblemDetailsService.addEditProblemDetails(request));
	return response;
	}
	
	@GetMapping(value = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS)
	@ApiOperation(value = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS, notes = PathProxy.ConsultationproblemDetailsUrls.GET_CONSULTATION_PROBLEM_DETAILS)
	public Response<ConsultationProblemDetails> getConsultationDetails(@RequestParam(required = false, value = "size", defaultValue = "10") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded", defaultValue = "false") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm,
			@RequestParam(required=false,value="doctorId")String doctorId,
			@RequestParam(required=false,value="problemDetailsId")String problemDetailsId,
			@RequestParam(required=false,value="userId")String userId) {
		
		Integer count = consultationProblemDetailsService.countConsultationProblemDetails(discarded, searchTerm,doctorId,problemDetailsId,userId);
		Response<ConsultationProblemDetails> response = new Response<ConsultationProblemDetails>();
		
			response.setDataList(consultationProblemDetailsService.getProblemDetails(size, page, discarded, searchTerm,doctorId,problemDetailsId,userId));
		response.setCount(count);
		return response;
	}
}
