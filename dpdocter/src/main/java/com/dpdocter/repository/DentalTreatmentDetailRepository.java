package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DentalTreatmentDetailCollection;

@Repository
public interface DentalTreatmentDetailRepository extends MongoRepository<DentalTreatmentDetailCollection, ObjectId> {

	DentalTreatmentDetailCollection findByTreatmentName(String string);

}
