package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;
import com.dpdocter.elasticsearch.services.impl.ESDentalLabServiceImpl;
import com.dpdocter.enums.LabType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.services.DentalLabService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.DENTAL_LAB_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_LAB_BASE_URL, description = "Endpoint for dental lab")
public class DentalLabAPI {

	private static Logger logger = LogManager.getLogger(DentalLabAPI.class.getName());
	
	@Autowired
	private DentalLabService dentalLabService;
	
	@Autowired
	private ESDentalLabServiceImpl esDentalLabServiceImpl;
	
	@PostMapping(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	public Response<DentalWork> addEditPickupRequest(@RequestBody AddEditCustomWorkRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DentalWork dentalWork = new DentalWork();
		dentalWork = dentalLabService.addEditCustomWork(request);
		Response<DentalWork> response = new Response<DentalWork>();
		if(dentalWork != null)
		{
			response.setData(dentalWork);
			ESDentalWorksDocument dentalWorksDocument = new ESDentalWorksDocument();
			BeanUtil.map(dentalWork, dentalWorksDocument);
			esDentalLabServiceImpl.addDentalWorks(dentalWorksDocument);
		}
		return response;
	}

	@GetMapping(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	public Response<DentalWork> getDentalWorks(@RequestParam(required = false, value ="locationId") String locationId,
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="searchTerm") String searchTerm) {
		Response<DentalWork> response = new Response<DentalWork>();
		response.setDataList(dentalLabService.getCustomWorks(page, size, searchTerm));
		return response;
	}
	
	@DeleteMapping(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	@ApiOperation(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	public Response<DentalWork> deleteDentalWork(@RequestParam(required = false, value ="id") String id,
			@RequestParam(required = false, value ="discarded") boolean discarded) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DentalWork dentalWork = new DentalWork();
		dentalWork = dentalLabService.deleteCustomWork(id, discarded);
		Response<DentalWork> response = new Response<DentalWork>();
		if(dentalWork != null)
		{
			response.setData(dentalWork);
			ESDentalWorksDocument dentalWorksDocument = new ESDentalWorksDocument();
			BeanUtil.map(dentalWork, dentalWorksDocument);
			esDentalLabServiceImpl.addDentalWorks(dentalWorksDocument);
		}
		return response;
	}
	
	@GetMapping(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	@ApiOperation(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE, notes = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	public Response<Boolean> changeLabType(@RequestParam(required = false, value ="doctorId") String doctorId,@RequestParam(required = false, value ="locationId") String locationId,
			@RequestParam(required = false, value ="labType") LabType labType) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.changeLabType(doctorId, locationId, labType));
		return response;
	}
	
	@PostMapping(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<Boolean> addEditDentalLabDoctorAssociation(@RequestBody List<DentalLabDoctorAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.addEditDentalLabDoctorAssociation(request));
		return response;
	}
	

	@GetMapping(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<DentalLabDoctorAssociation> getDentalLabDoctorAssociation(@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="searchTerm") String searchTerm) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabDoctorAssociation> response = new Response<DentalLabDoctorAssociation>();
		response.setDataList(dentalLabService.getDentalLabDoctorAssociations(doctorId, page, size, searchTerm));
		return response;
	}

	
}
