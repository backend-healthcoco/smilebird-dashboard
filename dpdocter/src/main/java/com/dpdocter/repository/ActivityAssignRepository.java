package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.beans.ActivityAssign;
import com.dpdocter.collections.ActivityAssignCollection;

public interface ActivityAssignRepository extends MongoRepository<ActivityAssignCollection, ObjectId>{

}
