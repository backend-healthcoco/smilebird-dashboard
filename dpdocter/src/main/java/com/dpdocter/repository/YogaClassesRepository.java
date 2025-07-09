package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.YogaClassesCollection;

public interface YogaClassesRepository extends MongoRepository<YogaClassesCollection, ObjectId>{

}
