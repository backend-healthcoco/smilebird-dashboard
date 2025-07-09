package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.YogaSessionCollection;

public interface YogaSessionRepository extends MongoRepository<YogaSessionCollection, ObjectId>{

}
