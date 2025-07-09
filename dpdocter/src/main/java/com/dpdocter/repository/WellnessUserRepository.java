package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.WellnessUserCollection;

public interface WellnessUserRepository extends MongoRepository<WellnessUserCollection, ObjectId>{

	@Query("{'userName' : ?0}")
	WellnessUserCollection findByUserName(String userName);
	
}
