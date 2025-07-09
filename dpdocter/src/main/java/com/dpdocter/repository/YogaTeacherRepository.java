package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.YogaTeacherCollection;

public interface YogaTeacherRepository extends MongoRepository<YogaTeacherCollection,ObjectId>{

}
