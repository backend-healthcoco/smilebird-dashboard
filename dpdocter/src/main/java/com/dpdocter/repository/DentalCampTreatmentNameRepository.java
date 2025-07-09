package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalCampTreatmentNameCollection;

public interface DentalCampTreatmentNameRepository extends  MongoRepository<DentalCampTreatmentNameCollection, ObjectId> {

}
