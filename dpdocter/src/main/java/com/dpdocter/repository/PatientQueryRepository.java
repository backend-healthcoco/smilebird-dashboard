package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PatientQueryCollection;

public interface PatientQueryRepository extends MongoRepository<PatientQueryCollection, ObjectId>{

}
