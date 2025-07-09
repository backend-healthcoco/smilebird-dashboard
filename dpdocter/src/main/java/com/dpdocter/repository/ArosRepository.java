package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ArosCollection;

public interface ArosRepository extends MongoRepository<ArosCollection, ObjectId> {

}
