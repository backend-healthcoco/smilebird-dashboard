package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PharmaCompanyCollection;

public interface PharmaCompanyRepository extends MongoRepository<PharmaCompanyCollection, ObjectId>{

}
