package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PharmaLicenseCollection;

public interface PharmaLicenseRepository extends MongoRepository<PharmaLicenseCollection, ObjectId>{

	
	
}
