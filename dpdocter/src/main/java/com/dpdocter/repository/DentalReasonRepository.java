package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalReasonsCollection;

public interface DentalReasonRepository extends MongoRepository<DentalReasonsCollection, ObjectId> {

}
