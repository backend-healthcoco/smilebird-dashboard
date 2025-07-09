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

import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CommunicationDoctorTeamRequest;
import com.dpdocter.beans.Country;
import com.dpdocter.beans.DoctorResponseStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.CommunicationService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.COMMUNICATION_MODULE_BASE_URL, produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.COMMUNICATION_MODULE_BASE_URL, description = "Endpoint for communication")
public class CommunicationModuleApi {

	private static Logger logger = LogManager.getLogger(CommunicationModuleApi.class.getName());

	@Autowired
	CommunicationService communicationService;
	/* This api used to save communication details between doctor & sales member */
	@PostMapping(value = PathProxy.CommunicationModuleUrls.ADD_EDIT_CONSULTATION_COMMUNICATION)
	@ApiOperation(value = PathProxy.CommunicationModuleUrls.ADD_EDIT_CONSULTATION_COMMUNICATION, notes = PathProxy.CommunicationModuleUrls.ADD_EDIT_CONSULTATION_COMMUNICATION)
	public Response<CommunicationDoctorTeamRequest> addEditCommunication(@RequestBody CommunicationDoctorTeamRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		CommunicationDoctorTeamRequest communicationDoctorTeamRequest = communicationService.addEditCommunication(request);
		Response<CommunicationDoctorTeamRequest> response = new Response<CommunicationDoctorTeamRequest>();
		response.setData(communicationDoctorTeamRequest);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.CommunicationModuleUrls.GET_COMMUNICATION_DETAILS_LIST)
	@ApiOperation(value = PathProxy.CommunicationModuleUrls.GET_COMMUNICATION_DETAILS_LIST, notes = PathProxy.CommunicationModuleUrls.GET_COMMUNICATION_DETAILS_LIST)
	public Response<CommunicationDoctorTeamRequest> getCountry(@RequestParam(required = false, value = "size", defaultValue = "10") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "status") DoctorResponseStatus status,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = communicationService.countDetailsList(status, searchTerm);
		Response<CommunicationDoctorTeamRequest> response = new Response<CommunicationDoctorTeamRequest>();
		if (count > 0)
			response.setDataList(communicationService.getDetailsLists(size, page, status, searchTerm));
		response.setCount(count);
		return response;
	}
	
	@GetMapping(value = PathProxy.CommunicationModuleUrls.GET_COMMUNICATION_DETAILS_GET_BY_DOCTORID)
	@ApiOperation(value = PathProxy.CommunicationModuleUrls.GET_COMMUNICATION_DETAILS_GET_BY_DOCTORID, notes = PathProxy.CommunicationModuleUrls.GET_COMMUNICATION_DETAILS_GET_BY_DOCTORID)
	public Response<CommunicationDoctorTeamRequest> getByDoctorId(@PathVariable("doctorId") String doctorId) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<CommunicationDoctorTeamRequest> response = new Response<CommunicationDoctorTeamRequest>();
		response.setData(communicationService.getByDoctorId(doctorId));
		return response;

	}
}
