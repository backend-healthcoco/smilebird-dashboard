package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;

public interface HistoryServices {

    List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request);

    DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request);

    DiseaseAddEditResponse deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId, Boolean discarded);

    List<DiseaseListResponse> getDiseases(String range, int page, int size, String doctorId, String hospitalId, String locationId, String updatedTime,
	    Boolean discarded, Boolean isAdmin, String searchTerm);

    List<DiseaseListResponse> getDiseasesByIds(List<ObjectId> diseasesIds);
}
