package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LocaleCollection;

public interface LocaleRepository extends MongoRepository<LocaleCollection, ObjectId> {

	public LocaleCollection findByContactNumber(String contactNumber);

	@Query(value = "{'pharmacySlugUrl':{$regex:'^?0*',$options:'i'}}", count = true)
	Integer countBySlugUrl(String locationSlugUrl);

	@Query(value = "{'isLocaleListed':?0}", count = true)
	Integer countListed(boolean isLocaleListed);

}
