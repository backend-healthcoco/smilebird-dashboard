package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MindfulnessAssignCollection;

public interface MindfulnessAssignRepository extends MongoRepository<MindfulnessAssignCollection, ObjectId>{

}
