package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESObservationsDocument;

public interface ESObservationsRepository extends ElasticsearchRepository<ESObservationsDocument, String> {
//    @Query("observation:?0*")
//    public List<SolrObservationsDocument> find(String searchTerm);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(Date date, Boolean discarded, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND observation:?2*")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND observation:?2*")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, Date date, Boolean discarded, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Sort sort);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND observation:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND observation:?3*)")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND observation:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND observation:?3*)")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND observation:?5*) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND observation:?5*)")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND observation:?5*) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND observation:?5*)")
//    public List<SolrObservationsDocument> findCustomGlobalObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrObservationsDocument> findGlobalObservations(Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrObservationsDocument> findGlobalObservations(Date date, Boolean discarded, Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND observation:?2*")
//    public List<SolrObservationsDocument> findGlobalObservations(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND observation:?2*")
//    public List<SolrObservationsDocument> findGlobalObservations(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, Date date, Boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND observation:?3*")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND observation:?3*")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND observation:?5*")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND observation:?5*")
//    public List<SolrObservationsDocument> findCustomObservations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Sort sort);
}
