package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AlignerPackageCollection;

public interface AlignerPackageRepository extends MongoRepository<AlignerPackageCollection, ObjectId>,
		PagingAndSortingRepository<AlignerPackageCollection, ObjectId> {

}
