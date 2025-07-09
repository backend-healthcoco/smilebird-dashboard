package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorSignupRequestCollection;

public interface DoctorSignupRepository extends MongoRepository<DoctorSignupRequestCollection,ObjectId>{

}
