package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DentalCampCollection;

public interface DentalCampRepository extends MongoRepository<DentalCampCollection, ObjectId>,
		PagingAndSortingRepository<DentalCampCollection, ObjectId> {

}
