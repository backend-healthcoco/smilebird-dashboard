package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.beans.AppointmentSearchResponse;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.elasticsearch.services.ESAppointmentService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_APPOINTMENT_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_APPOINTMENT_BASE_URL, description = "Endpoint for solr appointment")
public class ESAppointmentApi {

    @Autowired
    private ESAppointmentService solrAppointmentService;

    @Value(value = "${image.path}")
    private String imagePath;

    @Path(value = PathProxy.SolrAppointmentUrls.SEARCH)
    @GET
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.SEARCH, notes = PathProxy.SolrAppointmentUrls.SEARCH)
    public Response<AppointmentSearchResponse> search(@RequestParam(required = false, value ="city") String city, 
    		@RequestParam(required = false, value ="location") String location,
	    @RequestParam(required = false, value = "latitude") String latitude, 
	    @RequestParam(required = false, value = "longitude") String longitude, @RequestParam(required = false, value ="searchTerm") String searchTerm) {

	List<AppointmentSearchResponse> appointmentSearchResponses = solrAppointmentService.search(city, location, latitude, longitude, searchTerm);

	Response<AppointmentSearchResponse> response = new Response<AppointmentSearchResponse>();
	response.setDataList(appointmentSearchResponses);
	return response;
    }

    @GetMapping(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS, notes = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
    public Response<ESDoctorDocument> getDoctors(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="city") String city,
	    @RequestParam(required = false, value ="location") String location, @RequestParam(required = false, value = "latitude") String latitude, @RequestParam(required = false, value = "longitude") String longitude,
	    @RequestParam(required = false, value ="speciality") String speciality, @RequestParam(required = false, value ="symptom") String symptom, @RequestParam(required = false, value ="booking") Boolean booking,
	    @RequestParam(required = false, value ="calling") Boolean calling, @RequestParam(required = false, value ="minFee") int minFee, @RequestParam(required = false, value ="maxFee") int maxFee,
	    @RequestParam(required = false, value ="minTime") int minTime, @RequestParam(required = false, value ="maxTime") int maxTime, @MatrixParam("days") List<String> days,
	    @RequestParam(required = false, value ="gender") String gender, @RequestParam(required = false, value ="minExperience") int minExperience, @RequestParam(required = false, value ="maxExperience") int maxExperience) {

	List<ESDoctorDocument> doctors = solrAppointmentService.getDoctors(page, size, city, location, latitude, longitude, speciality, symptom, booking,
		calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience, maxExperience);

	Response<ESDoctorDocument> response = new Response<ESDoctorDocument>();
	response.setDataList(doctors);
	return response;
    }

    @GetMapping(value = PathProxy.SolrAppointmentUrls.GET_LABS)
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LABS, notes = PathProxy.SolrAppointmentUrls.GET_LABS)
    public Response<LabResponse> getLabs(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="city") String city, @RequestParam(required = false, value ="location") String location,
	    @RequestParam(required = false, value = "latitude") String latitude, @RequestParam(required = false, value = "longitude") String longitude, @RequestParam(required = false, value ="test") String test,
	    @RequestParam(required = false, value ="booking") Boolean booking, @RequestParam(required = false, value ="calling") Boolean calling,
	    @RequestParam(required = false, value ="minTime") int minTime, @RequestParam(required = false, value ="maxTime") int maxTime, @MatrixParam("days") List<String> days) {

	List<LabResponse> doctors = solrAppointmentService.getLabs(page, size, city, location, latitude, longitude, test, booking, calling, minTime, maxTime, days);

	if (doctors != null && !doctors.isEmpty()) {
	    for (LabResponse doctorDocument : doctors) {
		if (doctorDocument.getImages() != null && !doctorDocument.getImages().isEmpty()) {
			List<String> images = new ArrayList<String>();
			for (String clinicImage : doctorDocument.getImages()) {
			    images.add(clinicImage);
			}
			doctorDocument.setImages(images);
		}
		if (doctorDocument.getLogoUrl() != null)
		    doctorDocument.setLogoUrl(getFinalImageURL(doctorDocument.getLogoUrl()));
	    }
	}
	Response<LabResponse> response = new Response<LabResponse>();
	response.setDataList(doctors);
	return response;
    }

//    @Path(value = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY)
//    @POST
//    @ApiOperation(value = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY, notes = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY)
//    public Response<Boolean> addSpeciality(List<SolrSpecialityDocument> request) {
//	if (request == null || request.isEmpty()) {
//	    throw new BusinessException(ServiceError.InvalidInput, "Specialities Cannot Be Empty");
//	}
//
//	boolean addSpecializationResponse = solrAppointmentService.addSpeciality(request);
//
//	Response<Boolean> response = new Response<Boolean>();
//	response.setData(addSpecializationResponse);
//	return response;
//    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;

    }
    
    @GetMapping(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR)
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR, notes = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR)
    public Response<Boolean> sendSMStoDoctor() {

		Boolean status = solrAppointmentService.sendSMSToDoctors();	
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
    }
    

    @GetMapping(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_PHARMACY)
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_PHARMACY, notes = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_PHARMACY)
    public Response<Boolean> sendSMStoPharmacy() {

		Boolean status = solrAppointmentService.sendSMSToLocale();	
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
    }
}
