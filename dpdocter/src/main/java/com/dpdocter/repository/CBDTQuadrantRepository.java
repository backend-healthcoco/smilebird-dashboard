package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CBDTQuadrantCollection;

public interface CBDTQuadrantRepository extends MongoRepository<CBDTQuadrantCollection, ObjectId>{

}
