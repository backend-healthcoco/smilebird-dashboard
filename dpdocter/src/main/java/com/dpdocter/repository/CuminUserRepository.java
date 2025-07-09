package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CuminUserCollection;


public interface CuminUserRepository  extends MongoRepository<CuminUserCollection,ObjectId> {

}
