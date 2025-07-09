package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;

public interface ESInvestigationsRepository extends ElasticsearchRepository<ESInvestigationsDocument, String> {
//    @Query("investigation:?0*")
//    public List<SolrInvestigationsDocument> find(String searchTerm);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(Date date, Boolean discarded, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND investigation:?2*")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND investigation:?2*")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, Date date, Boolean discarded, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId:\"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId:\"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Sort sort);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND investigation:?3*) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND investigation:?3*)")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, Date date, Boolean discarded, String searchTerm,
//	    Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND investigation:?3*) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)  AND investigation:?3*)")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND investigation:?5*) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId: \"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND investigation:?5*)")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND investigation:?5*) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId: \"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND investigation:?5*)")
//    public List<SolrInvestigationsDocument> findCustomGlobalInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findGlobalInvestigations(Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findGlobalInvestigations(Date date, Boolean discarded, Sort sort);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND investigation:?2*")
//    public List<SolrInvestigationsDocument> findGlobalInvestigations(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("(doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND investigation:?2*")
//    public List<SolrInvestigationsDocument> findGlobalInvestigations(Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, Date date, Boolean discarded, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, Date date, Boolean discarded, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    Sort sort);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND investigation:?3*")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND investigation:?3*")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND investigation:?5*")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Pageable pageRequest);
//
//    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND investigation:?5*")
//    public List<SolrInvestigationsDocument> findCustomInvestigations(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded,
//	    String searchTerm, Sort sort);
}
