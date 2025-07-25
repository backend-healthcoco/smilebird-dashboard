package com.dpdocter.webservices;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LoginService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.LOGIN_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LOGIN_BASE_URL, description = "Endpoint for login")
public class LoginApi {

    private static Logger logger = LogManager.getLogger(LoginApi.class.getName());

    @Autowired
    private LoginService loginService;

    @Value(value = "${image.path}")
    private String imagePath;

    @GetMapping(value = PathProxy.LoginUrls.LOGIN_ADMIN)
    @ApiOperation(value = PathProxy.LoginUrls.LOGIN_ADMIN, notes = PathProxy.LoginUrls.LOGIN_ADMIN, tags = PathProxy.LoginUrls.LOGIN_ADMIN)
    public Response<Boolean> dashboardLogin(@PathVariable(value = "mobileNumber") String mobileNumber) {
	if (mobileNumber == null || mobileNumber.isEmpty()) {
	    logger.warn("Mobile number is null");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Boolean loginResponse = loginService.adminLogin(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	if (response != null)
	    response.setData(loginResponse);
	return response;
    }
}
