package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorConferenceCollection;

public interface DoctorConferenceRepository extends MongoRepository<DoctorConferenceCollection, ObjectId>  {

}
