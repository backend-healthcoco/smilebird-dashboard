package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SuperStarHealthCampCollection;

public interface SuperStarHealthCampRepository extends MongoRepository<SuperStarHealthCampCollection, ObjectId>{

}
