package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.beans.Activity;
import com.dpdocter.collections.ActivityCollection;

public interface ActivityRepository extends MongoRepository<ActivityCollection, ObjectId>{

	Activity findById(String id);
}
