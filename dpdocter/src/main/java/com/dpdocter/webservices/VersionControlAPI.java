package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.VersionControl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.VersionControlService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.VERSION_CONTROL_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.VERSION_CONTROL_BASE_URL, description = "Endpoint for version controliing")
public class VersionControlAPI {

	private static Logger logger = LogManager.getLogger(SignUpApi.class.getName());

	@Autowired
	VersionControlService versionControlService;

	/*
	 * @Path(value = PathProxy.VersionControlUrls.CHECK_VERSION)
	 * 
	 * @POST
	 * 
	 * @ApiOperation(value =PathProxy.VersionControlUrls.CHECK_VERSION, notes =
	 * PathProxy.VersionControlUrls.CHECK_VERSION) public Response<Integer>
	 * checkVersion(VersionControl versionControl) {
	 * 
	 * if (versionControl == null) { logger.warn("Request send  is NULL"); throw
	 * new BusinessException(ServiceError.InvalidInput,
	 * "Request send  is NULL"); } Integer versionControlCode =
	 * versionControlService.checkVersion(versionControl); Response<Integer>
	 * response = new Response<Integer>(); response.setData(versionControlCode);
	 * return response; }
	 */

	@PostMapping(value = PathProxy.VersionControlUrls.CHANGE_VERSION)
	@ApiOperation(value = PathProxy.VersionControlUrls.CHANGE_VERSION, notes = PathProxy.VersionControlUrls.CHANGE_VERSION)
	public Response<VersionControl> changeVersion(@RequestBody VersionControl versionControl) {

		if (versionControl == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		versionControl = versionControlService.changeVersion(versionControl);
		Response<VersionControl> response = new Response<VersionControl>();
		response.setData(versionControl);
		return response;
	}

	@GetMapping(value = PathProxy.VersionControlUrls.GET_VERSION)
	@ApiOperation(value = PathProxy.VersionControlUrls.GET_VERSION, notes = PathProxy.VersionControlUrls.GET_VERSION)
	public Response<Object> getVersion() {
		Response<Object> response = new Response<Object>();
		List<VersionControl> versionresponse = versionControlService.getVersion();
		response.setDataList(versionresponse);
		return response;
	}

}
