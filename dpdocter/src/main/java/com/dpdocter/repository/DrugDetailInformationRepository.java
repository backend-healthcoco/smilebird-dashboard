package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DrugDetailInformationCollection;

public interface DrugDetailInformationRepository extends MongoRepository<DrugDetailInformationCollection, ObjectId>{

}
