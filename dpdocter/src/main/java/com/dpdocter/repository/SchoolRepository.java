package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SchoolCollection;

public interface SchoolRepository extends MongoRepository<SchoolCollection, ObjectId>{

}
