package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorAppFeedbackCollection;

public interface DoctorAppFeedbackRepository extends MongoRepository<DoctorAppFeedbackCollection, ObjectId>{

}
