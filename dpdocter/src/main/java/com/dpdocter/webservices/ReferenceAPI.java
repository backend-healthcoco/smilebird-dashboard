package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DoctorLabReference;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorLabReferenceRequest;
import com.dpdocter.services.DoctorContactUsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.REFERENCE_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REFERENCE_BASE_URL, description = "Endpoint for reference")
public class ReferenceAPI {
	private static Logger logger = LogManager.getLogger(ReferenceAPI.class.getName());

	@Autowired
	private DoctorContactUsService doctorContactUsService;

	@GetMapping(value = PathProxy.ReferenceUrls.GET_DOCTOR_LAB_REFERNCE)
	@ApiOperation(value = PathProxy.ReferenceUrls.GET_DOCTOR_LAB_REFERNCE, notes = PathProxy.ReferenceUrls.GET_DOCTOR_LAB_REFERNCE)
	public Response<DoctorLabReference> getDoctorLabReference(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DoctorLabReference> response = new Response<DoctorLabReference>();

		response.setData(doctorContactUsService.getdoctorReference(id));

		return response;

	}

	@GetMapping(value = PathProxy.ReferenceUrls.GET_DOCTOR_LAB_REFERNCES)
	@ApiOperation(value = PathProxy.ReferenceUrls.GET_DOCTOR_LAB_REFERNCES, notes = PathProxy.ReferenceUrls.GET_DOCTOR_LAB_REFERNCES)
	public Response<DoctorLabReference> getDoctorLabReferences(@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="searchTerm") String searchTerm,
			@RequestParam(required = false, value ="isContacted", defaultValue = "false") Boolean isContacted) {

		Response<DoctorLabReference> response = new Response<DoctorLabReference>();

		response.setDataList(doctorContactUsService.getdoctorReferences(size, page, searchTerm, isContacted));

		return response;

	}

	@PutMapping(value = PathProxy.ReferenceUrls.UPDATE_DOCTOR_LAB_REFERNCE)
	@ApiOperation(value = PathProxy.ReferenceUrls.UPDATE_DOCTOR_LAB_REFERNCE, notes = PathProxy.ReferenceUrls.UPDATE_DOCTOR_LAB_REFERNCE)
	public Response<Boolean> UpdateDoctorLabReference(@RequestBody DoctorLabReferenceRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorContactUsService.updateDoctorLabRefencence(request));

		return response;

	}

}
