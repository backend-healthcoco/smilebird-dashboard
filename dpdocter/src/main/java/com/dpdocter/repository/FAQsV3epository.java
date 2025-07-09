package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FAQsCollection;

public interface FAQsV3epository extends MongoRepository<FAQsCollection, ObjectId> {

}
