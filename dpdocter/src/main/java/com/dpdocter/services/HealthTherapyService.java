package com.dpdocter.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;


import com.dpdocter.beans.HealthPlan;
import com.dpdocter.beans.HealthPlanResponse;
import com.dpdocter.beans.HealthPlanTypeResponse;
import com.dpdocter.enums.HealthPackagesPlanType;
import com.dpdocter.response.HealthiansPlanObject;

import common.util.web.Response;

public interface HealthTherapyService {

	List<HealthPlanResponse> getHealthTherapyPlans(int page, int size, String type,  boolean isDiscarded,String searchTerm);

	Boolean deleteHealthTherapyPlan(String planId,boolean isDiscarded);

	HealthPlanResponse addEditHealthTherapyPlan(HealthPlan request);

	HealthPlanResponse getHealthTherapyPlanById(String planId);

	Integer countPlans(String type, boolean isDiscarded, String searchTerm);

	HealthPlanResponse getHealthTherapyPlanBySlugUrl(String slugURL, String planUId);

	Integer countPlansTitles(HealthPackagesPlanType type, boolean isDiscarded, String country);

	List<HealthPlanTypeResponse> getHealthTherapyPlansTitles(int page, int size, HealthPackagesPlanType type, boolean isDiscarded, String country);

	Response<Object> getHealthiensPlans(String partnerName, boolean isDiscarded) throws MalformedURLException, IOException;


}
