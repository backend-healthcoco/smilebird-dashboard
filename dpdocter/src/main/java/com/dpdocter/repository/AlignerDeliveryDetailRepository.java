package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AlignerDeliveryDetailCollection;

public interface AlignerDeliveryDetailRepository extends MongoRepository<AlignerDeliveryDetailCollection, ObjectId>,
		PagingAndSortingRepository<AlignerDeliveryDetailCollection, ObjectId> {

}
