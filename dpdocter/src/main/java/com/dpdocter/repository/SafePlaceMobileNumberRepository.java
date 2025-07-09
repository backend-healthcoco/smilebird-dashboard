package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SafePlaceMobileNumberCollection;

public interface SafePlaceMobileNumberRepository extends MongoRepository<SafePlaceMobileNumberCollection, ObjectId>{

}
