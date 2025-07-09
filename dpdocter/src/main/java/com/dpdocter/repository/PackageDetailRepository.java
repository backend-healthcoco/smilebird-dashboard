package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PackageDetailCollection;


public interface PackageDetailRepository extends MongoRepository<PackageDetailCollection, ObjectId> {

}
