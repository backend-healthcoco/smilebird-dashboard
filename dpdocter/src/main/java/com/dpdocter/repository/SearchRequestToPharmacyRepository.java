package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.SearchRequestToPharmacyCollection;

public interface SearchRequestToPharmacyRepository extends MongoRepository<SearchRequestToPharmacyCollection, ObjectId>,
		PagingAndSortingRepository<SearchRequestToPharmacyCollection, ObjectId> {
	
	@Query(value = "{'localeId' : ?0 }", count = true)
	public Integer getCountBylocaleId(ObjectId localeId);

	@Query(value = "{'localeId' : ?0 ,'replyType':?1 }", count = true)
	public Integer getCountBylocaleIdandreplyType(ObjectId localeId, String replyType);
	
	@Query(value = "{'localeId' : ?0 ,'replyType':{'exists':false} }", count = true)
	public Integer getCountByNOreply(ObjectId localeId);

}