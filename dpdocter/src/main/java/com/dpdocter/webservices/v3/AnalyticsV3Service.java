package com.dpdocter.webservices.v3;

import java.util.List;

import common.util.web.Response;

public interface AnalyticsV3Service {

	Response<Object> getLeadsAnalyticData(String fromDate, String toDate, String city, List<String> campaignId,
			String analyticType, String dentalStudioId);

	Response<Object> getPatientAnalyticData(String fromDate, String toDate, String city, String smileBuddyId,
			String analyticType);

	Response<Object> getAppointmentAnalyticData(String fromDate, String toDate, String locationId, String doctorId,
			String smileBuddyId, String analyticType);

	Response<Object> getCampaignAnalyticData(String fromDate, String toDate, String city, List<String> campaignId,
			String analyticType);

	Response<Object> getAnalyticDetaileData(String fromDate, String toDate, String city, List<String> campaignId);

	Response<Object> getMonthlyAnalyticsData(String fromDate, String toDate, List<String> analyticType);

	String getChatGPTData(String input);

}
