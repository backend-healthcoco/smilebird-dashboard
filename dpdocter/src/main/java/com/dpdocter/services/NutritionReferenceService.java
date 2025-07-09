package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionReference;
import com.dpdocter.response.NutritionReferenceResponse;

public interface NutritionReferenceService {

	List<NutritionReference> getNutritionReferenceList(String doctorId, String locationId, int page, int size,
			String searchTerm, String status, Long fromDate, Long toDate);

	NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, Long fromDate, Long toDate);

	public NutritionReferenceResponse getNutritionReferenceById(String id);

	public Boolean changeStatus(String id, String regularityStatus, String goalStatus);

}