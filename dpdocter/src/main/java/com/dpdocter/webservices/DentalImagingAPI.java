package com.dpdocter.webservices;

import java.util.List;

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

import com.dpdocter.beans.CBDTArch;
import com.dpdocter.beans.CBDTQuadrant;
import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.FOV;
import com.dpdocter.beans.Hospital;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.DoctorHospitalDentalImagingAssociationLookupResponse;
import com.dpdocter.services.DentalImagingService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.DENTAL_IMAGING_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_IMAGING_URL, description = "Endpoint for dental imaging")
public class DentalImagingAPI {

	private static Logger logger = LogManager.getLogger(DentalImagingAPI.class.getName());
	
	@Autowired
	DentalImagingService dentalImagingService;
	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_EDIT_SERVICES)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_SERVICES, notes = PathProxy.DentalImagingUrl.ADD_EDIT_SERVICES)
	public Response<DentalDiagnosticService> addEditDentalDiagnosticService(@RequestBody DentalDiagnosticService request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalDiagnosticService> response = new Response<DentalDiagnosticService>();
		response.setData(dentalImagingService.addEditService(request));
		return response;
	}
	

	@GetMapping(value = PathProxy.DentalImagingUrl.GET_SERVICES)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICES, notes = PathProxy.DentalImagingUrl.GET_SERVICES)
	public Response<DentalDiagnosticService> getDentalDiagnosticServices(@RequestParam(required = false, value ="searchTerm") String searchTerm , @RequestParam(required = false, value ="type") String type , @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size  ) {
		Response<DentalDiagnosticService> response = new Response<DentalDiagnosticService>();
		response.setDataList(dentalImagingService.getServices(searchTerm, type, page, size));
		return response;
	}
	

	@GetMapping(value = PathProxy.DentalImagingUrl.GET_SERVICES_BY_ID)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICES_BY_ID, notes = PathProxy.DentalImagingUrl.GET_SERVICES_BY_ID)
	public Response<DentalDiagnosticService> getServiceById(@PathVariable("id") String id) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalDiagnosticService> response = new Response<DentalDiagnosticService>();
		response.setData(dentalImagingService.getServiceById(id));
		return response;
	}
	

	@GetMapping(value = PathProxy.DentalImagingUrl.DISCARD_SERVICE)
	@ApiOperation(value = PathProxy.DentalImagingUrl.DISCARD_SERVICE, notes = PathProxy.DentalImagingUrl.DISCARD_SERVICE)
	public Response<DentalDiagnosticService> discardService(@PathVariable("id") String id , @RequestParam(required = false, value ="discarded" , defaultValue = "false") boolean discarded) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalDiagnosticService> response = new Response<DentalDiagnosticService>();
		
		response.setData(dentalImagingService.discardService(id, discarded));
		return response;
	}
	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_DOCTOR_HOSPITAL_ASSOCIATION)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_DOCTOR_HOSPITAL_ASSOCIATION, notes = PathProxy.DentalImagingUrl.ADD_DOCTOR_HOSPITAL_ASSOCIATION)
	public Response<Boolean> addEditDoctorHospitalAssocation(@RequestBody List<DoctorHospitalDentalImagingAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalImagingService.addEditDoctorHospitalDentalImagingAssociation(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_DOCTOR_HOSPITAL_ASSOCIATIONS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_DOCTOR_HOSPITAL_ASSOCIATIONS, notes = PathProxy.DentalImagingUrl.GET_DOCTOR_HOSPITAL_ASSOCIATIONS)
	public Response<DoctorHospitalDentalImagingAssociationLookupResponse> getDoctorHospitalAssociation(@RequestParam(required = false, value ="doctorId") String doctorId,@RequestParam(required = false, value ="doctorLocationId") String doctorLocationId,@RequestParam(required = false, value ="hospitalId") String hospitalId,@RequestParam(required = false, value ="searchTerm") String searchTerm , @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size , @RequestParam(required = false, value ="discarded") Boolean discarded  ) {
		Response<DoctorHospitalDentalImagingAssociationLookupResponse> response = new Response<DoctorHospitalDentalImagingAssociationLookupResponse>();
		response.setDataList(dentalImagingService.getDoctorHospitalAssociation(doctorId,doctorLocationId, hospitalId, searchTerm, page, size ,discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST, notes = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST)
	public Response<Hospital> getLocationAssociatedServices(@RequestParam(required = false, value ="doctorId") String doctorId, @RequestParam(required = false, value ="hospitalId") String hospitalId) {
		Response<Hospital> response = new Response<Hospital>();
		response.setDataList(dentalImagingService.getHospitalList(doctorId, hospitalId));
		return response;
	}
	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_EDIT_CBDT_QUADRANT)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_CBDT_QUADRANT, notes = PathProxy.DentalImagingUrl.ADD_EDIT_CBDT_QUADRANT)
	public Response<CBDTQuadrant> addEditCBDTQuadrant(@RequestBody CBDTQuadrant cbdtQuadrant) {
		if (cbdtQuadrant == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CBDTQuadrant> response = new Response<CBDTQuadrant>();
		response.setData(dentalImagingService.addeditCBDTQuadrant(cbdtQuadrant));
		return response;
	}
	

	@GetMapping(value = PathProxy.DentalImagingUrl.GET_CBDT_QUADRANTS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_CBDT_QUADRANTS, notes = PathProxy.DentalImagingUrl.GET_CBDT_QUADRANTS)
	public Response<CBDTQuadrant> getCBDTQuadrants(@RequestParam(required = false, value ="searchTerm") String searchTerm ,  @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size  ) {
		Response<CBDTQuadrant> response = new Response<CBDTQuadrant>();
		response.setDataList(dentalImagingService.getCBDTQuadrants(searchTerm, page, size));
		return response;
	}
	

	@GetMapping(value = PathProxy.DentalImagingUrl.GET_CBDT_QUADRANT_BY_ID)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_CBDT_QUADRANT_BY_ID, notes = PathProxy.DentalImagingUrl.GET_CBDT_QUADRANT_BY_ID)
	public Response<CBDTQuadrant> getCBDTQuadrantById(@PathVariable("id") String id) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CBDTQuadrant> response = new Response<CBDTQuadrant>();
		response.setData(dentalImagingService.getCBDTQuadrantById(id));
		return response;
	}
	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_EDIT_CBDT_ARCH)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_CBDT_ARCH, notes = PathProxy.DentalImagingUrl.ADD_EDIT_CBDT_ARCH)
	public Response<CBDTArch> addEditCBDTArch(@RequestBody CBDTArch cbdtArch) {
		if (cbdtArch == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CBDTArch> response = new Response<CBDTArch>();
		response.setData(dentalImagingService.addeditCBDTArch(cbdtArch));
		return response;
	}
	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_CBDT_ARCHS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_CBDT_ARCHS, notes = PathProxy.DentalImagingUrl.GET_CBDT_ARCHS)
	public Response<CBDTArch> getCBDTArchs(@RequestParam(required = false, value ="searchTerm") String searchTerm ,  @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size  ) {
		Response<CBDTArch> response = new Response<CBDTArch>();
		response.setDataList(dentalImagingService.getCBDTArchs(searchTerm, page, size));
		return response;
	}
	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_CBDT_ARCH_BY_ID)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_CBDT_ARCH_BY_ID, notes = PathProxy.DentalImagingUrl.GET_CBDT_ARCH_BY_ID)
	public Response<CBDTArch> getCBDTArchById(@PathVariable("id") String id) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CBDTArch> response = new Response<CBDTArch>();
		response.setData(dentalImagingService.getCBDTArchById(id));
		return response;
	}
	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_EDIT_FOV)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_FOV, notes = PathProxy.DentalImagingUrl.ADD_EDIT_FOV)
	public Response<FOV> addEditFOV(@RequestBody FOV fov) {
		if (fov == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<FOV> response = new Response<FOV>();
		response.setData(dentalImagingService.addeditFOV(fov));
		return response;
	}
	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_FOVS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_FOVS, notes = PathProxy.DentalImagingUrl.GET_FOVS)
	public Response<FOV> getFOVs(@RequestParam(required = false, value ="searchTerm") String searchTerm ,  @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size  ) {
		Response<FOV> response = new Response<FOV>();
		response.setDataList(dentalImagingService.getFOVs(searchTerm, page, size));
		return response;
	}
	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_FOV_BY_ID)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_FOV_BY_ID, notes = PathProxy.DentalImagingUrl.GET_FOV_BY_ID)
	public Response<FOV> getFOVById(@PathVariable("id") String id) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<FOV> response = new Response<FOV>();
		response.setData(dentalImagingService.getFOVById(id));
		return response;
	}
	
}
