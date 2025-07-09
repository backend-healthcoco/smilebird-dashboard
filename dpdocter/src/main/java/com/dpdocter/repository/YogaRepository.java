package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.YogaCollection;

public interface YogaRepository extends MongoRepository<YogaCollection, ObjectId>{

}
