package com.dpdocter.webservices.v3;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.ANALYTICS_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ANALYTICS_BASE_URL, description = "Endpoint for analytics")
public class AnalyticsV3API {
	private static Logger logger = LogManager.getLogger(AnalyticsV3API.class.getName());

	@Autowired
	private AnalyticsV3Service analyticsService;

	@GetMapping(value = PathProxy.AnalyticsUrls.GET_LEADS_ANALYTICS_DATA)
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_LEADS_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_LEADS_ANALYTICS_DATA)
	public Response<Object> getLeadsAnalyticData(@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "analyticType") String analyticType,
			@RequestParam(required = false, value = "dentalStudioId") String dentalStudioId,
			@RequestParam(required = false, value = "campaignId") List<String> campaignId) {
		Response<Object> response = analyticsService.getLeadsAnalyticData(fromDate, toDate, city, campaignId,
				analyticType,dentalStudioId);
		return response;
	}

	@GetMapping(value = PathProxy.AnalyticsUrls.GET_CAMPAIGN_ANALYTICS_DATA)
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_CAMPAIGN_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_CAMPAIGN_ANALYTICS_DATA)
	public Response<Object> getCampaignAnalyticData(@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "analyticType") String analyticType,
			@RequestParam(required = false, value = "campaignId") List<String> campaignId) {
		Response<Object> response = analyticsService.getCampaignAnalyticData(fromDate, toDate, city, campaignId,
				analyticType);
		return response;
	}

	@GetMapping(value = PathProxy.AnalyticsUrls.GET_ANALYTICS_DATA)
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_ANALYTICS_DATA)
	public Response<Object> getAnalyticDetaileData(@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "campaignId") List<String> campaignId) {
		Response<Object> response = analyticsService.getAnalyticDetaileData(fromDate, toDate,city, campaignId);
		return response;
	}

	@GetMapping(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA)
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA)
	public Response<Object> getPatientAnalyticData(@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "analyticType") String analyticType,
			@RequestParam(required = false, value = "smileBuddyId") String smileBuddyId) {
		Response<Object> response = analyticsService.getPatientAnalyticData(fromDate, toDate, city, smileBuddyId,
				analyticType);
		return response;
	}

	@GetMapping(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DATA)
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DATA)
	public Response<Object> getAppointmentAnalyticData(
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "analyticType") String analyticType,
			@RequestParam(required = false, value = "smileBuddyId") String smileBuddyId) {
		Response<Object> response = analyticsService.getAppointmentAnalyticData(fromDate, toDate, locationId, doctorId,
				smileBuddyId, analyticType);
		return response;
	}

	@GetMapping(value = PathProxy.AnalyticsUrls.GET_MONTHLY_ANALYTICS_DATA)
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_MONTHLY_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_MONTHLY_ANALYTICS_DATA)
	public Response<Object> getMonthlyAnalyticsData(@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate,
			@RequestParam(required = false, value = "analyticType") List<String> analyticType) {
		Response<Object> response = analyticsService.getMonthlyAnalyticsData(fromDate, toDate, analyticType);
		return response;
	}
	
	@GetMapping(value = PathProxy.AnalyticsUrls.GET_CHAT_GPT_RESPONSE)
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_CHAT_GPT_RESPONSE, notes = PathProxy.AnalyticsUrls.GET_CHAT_GPT_RESPONSE)
	public Response<Object> getChatGPTData(
			@RequestParam(required = false, value = "input") String input) {
		String string = analyticsService.getChatGPTData(input);
		Response<Object> response =new Response<>();
		response.setData(string);
		return response;
	}

}
