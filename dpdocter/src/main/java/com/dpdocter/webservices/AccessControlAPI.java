package com.dpdocter.webservices;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.AccessControlServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.ACCESS_CONTROL_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ACCESS_CONTROL_BASE_URL, description = "Endpoint for access control")
public class AccessControlAPI {
    @Autowired
    private AccessControlServices accessControlServices;

//    @Path(value = PathProxy.AccessControlUrls.GET_ACCESS_CONTROLS)
//    @GET
//    @ApiOperation(value = PathProxy.AccessControlUrls.GET_ACCESS_CONTROLS, notes = PathProxy.AccessControlUrls.GET_ACCESS_CONTROLS)
//    public Response<AccessControl> getAccessControls(@PathVariable(value = "roleOrUserId") String roleOrUserId, @PathVariable(value = "locationId") String locationId,
//	    @PathVariable(value = "hospitalId") String hospitalId) {
//	if (DPDoctorUtils.anyStringEmpty(roleOrUserId, locationId, hospitalId)) {
//	    throw new BusinessException(ServiceError.InvalidInput, "Role Or User Id, Location Id and Hospital Id Cannot Be Empty!");
//	}
//
//	AccessControl accessControl = accessControlServices.getAccessControls(roleOrUserId, locationId, hospitalId);
//
//	Response<AccessControl> response = new Response<AccessControl>();
//	response.setData(accessControl);
//	return response;
//    }

    @PostMapping(value = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS)
    @ApiOperation(value = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS, notes = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS)
    public Response<AccessControl> setAccessControls(@RequestBody AccessControl accessControl) {
	if (accessControl == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Access Control Request Cannot Be Empty!");
	}

	AccessControl setAccessControlResponse = accessControlServices.setAccessControls(accessControl);

	Response<AccessControl> response = new Response<AccessControl>();
	response.setData(setAccessControlResponse);
	return response;
    }
}
