package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SchoolBranchCollection;

public interface SchoolBranchRepository extends MongoRepository<SchoolBranchCollection, ObjectId>{

}
