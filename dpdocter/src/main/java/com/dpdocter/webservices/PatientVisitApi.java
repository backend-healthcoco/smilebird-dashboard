package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value=PathProxy.PATIENT_VISIT_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_VISIT_BASE_URL, description = "Endpoint for patient visit")
public class PatientVisitApi {

    private static Logger logger = LogManager.getLogger(PatientVisitApi.class.getName());

}
