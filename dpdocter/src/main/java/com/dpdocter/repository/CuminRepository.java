package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CuminGroupCollection;

public interface CuminRepository extends MongoRepository<CuminGroupCollection, ObjectId> {

}
