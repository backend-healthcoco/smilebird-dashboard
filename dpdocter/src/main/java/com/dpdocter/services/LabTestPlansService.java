package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.LabTestRequest;
import com.dpdocter.enums.DisplayTypeThyrocare;
import com.dpdocter.response.LabTestCustomerResponse;
import com.dpdocter.response.LabTestResponse;

import common.util.web.Response;

public interface LabTestPlansService {

	LabTestResponse addEditLabTestPlan(LabTestRequest request);

	LabTestResponse getLabTestPlanById(String planId);

	Integer countPlans(String labPartner, boolean isDiscarded, String searchTerm);

	List<LabTestResponse> getLabTestPlans(int page, int size, String labPartner, boolean isDiscarded, String searchTerm);

	Boolean deleteLabTestPlan(String planId, boolean isDiscarded);

	LabTestResponse getLabTestPlanBySlugUrl(String slugURL, String planUId);

	Response<Object> getHealthiensPlans();

	List<LabTestCustomerResponse> getLabTestUsersList(int page, int size, String labPartner, String searchTerm);

	LabTestCustomerResponse getLabTestPlanCustomerById(String labTestAppointmentId);

	Integer countLabTestPlans(String labPartner, boolean isDiscarded, String searchTerm);

	List<?> getLabTestPayment(int page, int size, String labPartner, boolean isDiscarded, String searchTerm);

	Response<Object> getThyrocarePlans(DisplayTypeThyrocare display_type);

	Response<Object> getHealthiensPlansProdData();

	Boolean uploadProdDealDetail();

	Response<Object> getBookingStatus(String booking_id);

	Response<Object> getBookingStatusForThyrocare(String loginMobile, String ref_orderId);

	

}
