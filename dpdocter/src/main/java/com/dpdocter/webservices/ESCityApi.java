package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.beans.ESCityLandmarkLocalityResponse;
import com.dpdocter.elasticsearch.services.ESCityService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_CITY_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_CITY_BASE_URL, description = "Endpoint for solr city")
public class ESCityApi {

    @Autowired
    private ESCityService esCityService;

    @GetMapping(value = PathProxy.SolrCityUrls.SEARCH_LOCATION)
    @ApiOperation(value = PathProxy.SolrCityUrls.SEARCH_LOCATION, notes = PathProxy.SolrCityUrls.SEARCH_LOCATION)
    public Response<ESCityLandmarkLocalityResponse> searchLocation(@RequestParam(required = false, value = "searchTerm") String searchTerm,
	    @RequestParam(required = false, value = "latitude") String latitude, @RequestParam(required = false, value = "longitude") String longitude) {

	List<ESCityLandmarkLocalityResponse> searchResonse = esCityService.searchCityLandmarkLocality(searchTerm, latitude, longitude);
	Response<ESCityLandmarkLocalityResponse> response = new Response<ESCityLandmarkLocalityResponse>();
	response.setDataList(searchResonse);
	return response;
    }

}
