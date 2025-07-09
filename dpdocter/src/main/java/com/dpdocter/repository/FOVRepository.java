package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FOVCollection;

public interface FOVRepository extends MongoRepository<FOVCollection, ObjectId>{

	
	
}
