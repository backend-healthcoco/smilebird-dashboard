package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LocaleContactUsCollection;

public interface LocaleContactUsRepository extends MongoRepository<LocaleContactUsCollection, ObjectId> {
	 LocaleContactUsCollection findByContactNumber( String contactNumber);
}
