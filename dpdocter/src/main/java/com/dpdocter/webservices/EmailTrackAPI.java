package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.EmailTrack;
import com.dpdocter.services.EmailTackService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.EMAIL_TRACK_BASE_URL, produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.EMAIL_TRACK_BASE_URL, description = "Endpoint for email track")
public class EmailTrackAPI {

//    private static Logger logger = LogManager.getLogger(EmailTrackAPI.class.getName());

    @Autowired
    private EmailTackService emailTackService;

    @GetMapping
    @ApiOperation(value = "GET_EMAIL_DETAILS", notes = "GET_EMAIL_DETAILS")
    public Response<EmailTrack> getEmailDetails(@RequestParam(required = false, value = "patientId") String patientId, @RequestParam(required = false, value = "doctorId") String doctorId,
	    @RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "hospitalId") String hospitalId, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
	    @RequestParam(required = false, value ="size", defaultValue = "0") int size) {

	List<EmailTrack> emailTrackList = emailTackService.getEmailDetails(patientId, doctorId, locationId, hospitalId, page, size);
	Response<EmailTrack> response = new Response<EmailTrack>();
	response.setDataList(emailTrackList);
	return response;
    }

}
