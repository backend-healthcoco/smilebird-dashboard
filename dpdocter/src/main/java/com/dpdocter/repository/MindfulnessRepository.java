package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MindfulnessCollection;

public interface MindfulnessRepository extends MongoRepository<MindfulnessCollection, ObjectId>{

}
