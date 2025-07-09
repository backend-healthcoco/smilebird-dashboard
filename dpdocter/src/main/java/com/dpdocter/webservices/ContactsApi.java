package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.SendAppLink;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.ContactsService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CONTACTS_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CONTACTS_BASE_URL, description = "Endpoint for contacts")
public class ContactsApi {

    private static Logger logger = LogManager.getLogger(ContactsApi.class.getName());

    @Autowired
    private ContactsService contactsService;

	@PostMapping(value = PathProxy.ContactsUrls.SEND_APP_LINK)
	@ApiOperation(value = PathProxy.ContactsUrls.SEND_APP_LINK, notes = PathProxy.ContactsUrls.SEND_APP_LINK)
	public Response<Boolean> sendLink(@RequestBody SendAppLink request){
	    if (request == null || request.getAppType() == null) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	    }

	    Boolean sendLinkresponse = contactsService.sendLink(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(sendLinkresponse);
		return response;
	}

}
