package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.beans.ESCityLandmarkLocalityResponse;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;

public interface ESCityService {

    boolean addCities(ESCityDocument solrCities);

    boolean addLocalityLandmark(ESLandmarkLocalityDocument esLandmarkLocalityDocument);

    List<ESCityLandmarkLocalityResponse> searchCityLandmarkLocality(String searchTerm, String latitude, String longitude);

    boolean activateDeactivateCity(String cityId, Boolean activate);

}
