package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RtpcrTestCollection;

public interface RtpcrTestRepository extends MongoRepository<RtpcrTestCollection,ObjectId> {

}
