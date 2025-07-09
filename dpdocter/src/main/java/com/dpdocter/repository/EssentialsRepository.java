package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.EssentialCollection;

public interface EssentialsRepository extends MongoRepository<EssentialCollection, ObjectId>{

}
