package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.elasticsearch.document.ESCityDocument;

@Repository
public interface ESCityRepository extends ElasticsearchRepository<ESCityDocument, String> {
    
	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"city\": \"?0\"}}]}}")
    List<ESCityDocument> findByQueryAnnotation(String searchTerm, Pageable pageable);

//    @Query("city:?0* AND isActivated:true AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
//    List<ESCityDocument> findByQueryAnnotation(String searchTerm, double latitude, double longitude);
//
//    @Query("isActivated:true AND !geofilt sfield='geoLocation'  pt=?0,?1 d=10")
//    List<ESCityDocument> findByQueryAnnotation(double latitude, double longitude);
//
	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"city\": \"?0\"}}]}}")
    ESCityDocument findByName(String city);
}
