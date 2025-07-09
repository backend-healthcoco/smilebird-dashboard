package com.dpdocter.webservices;

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

import com.dpdocter.beans.AdminUIPermission;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.AdminUIPermissionServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.ADMIN_UI_PERMISSION_BASE_URL, produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_UI_PERMISSION_BASE_URL, description = "Endpoint for uipermission")
public class AdminUIPermissionAPI {

	private static Logger logger = LogManager.getLogger(AdminUIPermissionAPI.class.getName());

	@Autowired
	private AdminUIPermissionServices adminUIPermissionServices;

	@PostMapping(value = PathProxy.AdminUIPermissionUrls.ADD_EDIT_ADMIN_UI_PERMISSION)
	@ApiOperation(value = PathProxy.AdminUIPermissionUrls.ADD_EDIT_ADMIN_UI_PERMISSION, notes = PathProxy.AdminUIPermissionUrls.ADD_EDIT_ADMIN_UI_PERMISSION)
	public Response<AdminUIPermission> addEditAdminUIPermission(@RequestBody AdminUIPermission request) {
		if (request == null) {
			logger.warn("Empty Request");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getAdminId())) {
			logger.warn("adminId should not be null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<AdminUIPermission> response = new Response<AdminUIPermission>();
		response.setData(adminUIPermissionServices.addPermission(request));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSIONS)
	@ApiOperation(value = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSIONS, notes = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSIONS)
	public Response<AdminUIPermission> getAdminUIPermissions(@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="page", defaultValue = "0") int page) {

		Response<AdminUIPermission> response = new Response<AdminUIPermission>();
		response.setDataList(adminUIPermissionServices.getUIPermission(page, size));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSION_BY_ADMINID)
	@ApiOperation(value = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSION_BY_ADMINID, notes = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSION_BY_ADMINID)
	public Response<AdminUIPermission> getAdminUIPermissionsByAdminId(@PathVariable("adminId") String adminId) {

		Response<AdminUIPermission> response = new Response<AdminUIPermission>();
		response.setData(adminUIPermissionServices.getUIPermissionByAdminId(adminId));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSION)
	@ApiOperation(value = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSION, notes = PathProxy.AdminUIPermissionUrls.GET_ADMIN_UI_PERMISSION)
	public Response<AdminUIPermission> getAdminUIPermissionsById(@PathVariable("id") String id) {

		Response<AdminUIPermission> response = new Response<AdminUIPermission>();
		response.setData(adminUIPermissionServices.getUIPermissionById(id));
		return response;
	}
}
