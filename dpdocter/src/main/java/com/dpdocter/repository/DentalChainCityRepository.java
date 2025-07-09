package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DentalChainCityCollection;

@Repository
public interface DentalChainCityRepository extends MongoRepository<DentalChainCityCollection, ObjectId>,
		PagingAndSortingRepository<DentalChainCityCollection, ObjectId> {

}
