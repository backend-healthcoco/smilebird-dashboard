package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalDiagnosticServiceCollection;

public interface DentalDiagnosticServiceRepository extends MongoRepository<DentalDiagnosticServiceCollection, ObjectId>{

}
