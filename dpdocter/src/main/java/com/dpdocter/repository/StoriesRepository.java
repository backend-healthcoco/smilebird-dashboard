package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.StoriesCollection;

public interface StoriesRepository extends MongoRepository<StoriesCollection, ObjectId>{

}
