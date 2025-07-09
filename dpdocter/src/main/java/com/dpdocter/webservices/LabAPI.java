package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
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

import com.dpdocter.beans.LabAssociation;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Specimen;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LocationServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;

@RestController
@RequestMapping(value=PathProxy.LAB_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LAB_BASE_URL, description = "Endpoint for location services")

public class LabAPI {

	@Autowired
	private LocationServices locationService;

	private static final Logger logger = LogManager.getLogger(LabAPI.class);

	@PostMapping(PathProxy.LabUrls.ADD_ASSOCIATED_LABS)
	public Response<Boolean> addAssociatedLabs(@RequestBody List<LabAssociation> labAssociations) {
		Boolean status = false;
		Response<Boolean> response = null;
		try {
			if (labAssociations == null) {
				throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
			}
			status = locationService.addAssociatedLabs(labAssociations);
			response = new Response<Boolean>();
			response.setData(status);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(PathProxy.LabUrls.GET_ASSOCIATED_LABS)
	public Response<Location> getAssociatedLabs(@PathVariable("locationId") String locationId , @RequestParam(required = false, value ="isParent", defaultValue = "false") Boolean isParent) {
		List<Location> locations = null;
		Response<Location> response = null;

		try {
			if (DPDoctorUtils.anyStringEmpty(locationId)) {
				throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
			}
			locations = locationService.getAssociatedLabs(locationId, isParent);
			response = new Response<Location>();
			response.setDataList(locations);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(PathProxy.LabUrls.EDIT_PARENT_STATUS)
	public Response<Location> editParentStatus(@PathVariable("locationId") String locationId,
			@RequestParam(required = false, value ="isParent", defaultValue = "false") Boolean isParent) {
		Location location = null;
		Response<Location> response = null;

		try {
			if (DPDoctorUtils.anyStringEmpty(locationId)) {
				throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
			}
			location = locationService.addEditParentLab(locationId, isParent);
			response = new Response<Location>();
			response.setData(location);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}
	
	@GetMapping(PathProxy.LabUrls.GET_SPECIMEN_LIST)
	public Response<Specimen> getSpecimen(@RequestParam(required = false, value ="page", defaultValue = "0") int page ,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size , 
			@RequestParam(required = false, value ="searchTerm") String searchTerm) {
		List<Specimen> specimens = null;
		Response<Specimen> response = null;

		try {
			specimens = locationService.getSpecimenList(page, size,searchTerm);
			response = new Response<Specimen>();
			response.setDataList(specimens);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}
	
	@PostMapping(PathProxy.LabUrls.ADD_SPECIMEN_LIST)
	public Response<Specimen> addSpecimen(@RequestBody Specimen request){
		Specimen specimen = null;
		Response<Specimen> response = null;

		try {
			specimen = locationService.addEditSpecimen(request);
			response = new Response<Specimen>();
			response.setData(specimen);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

}
