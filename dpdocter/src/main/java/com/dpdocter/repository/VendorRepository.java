package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VendorCollection;

public interface VendorRepository extends MongoRepository<VendorCollection, ObjectId>{

}
