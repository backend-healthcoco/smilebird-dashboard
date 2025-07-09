package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.services.impl.TreatmentRatelistCollection;

@Repository
public interface TreatmentRatelistRepository extends MongoRepository<TreatmentRatelistCollection, ObjectId> {

}
