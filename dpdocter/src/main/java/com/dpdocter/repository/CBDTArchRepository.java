package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CBDTArchCollection;

public interface CBDTArchRepository extends MongoRepository<CBDTArchCollection, ObjectId>{

}
