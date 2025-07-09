package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CuratedYogaSessionCollection;

public interface CuratedYogaSessionRepository extends MongoRepository<CuratedYogaSessionCollection, ObjectId>{

}
