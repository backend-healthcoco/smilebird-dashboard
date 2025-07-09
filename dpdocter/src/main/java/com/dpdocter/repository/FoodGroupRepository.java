package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FoodGroupCollection;

public interface FoodGroupRepository extends MongoRepository<FoodGroupCollection, ObjectId>{

}
