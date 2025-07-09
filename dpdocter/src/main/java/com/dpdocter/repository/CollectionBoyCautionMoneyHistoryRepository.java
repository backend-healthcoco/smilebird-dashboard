package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CollectionBoyCautionMoneyHistoryCollection;

public interface CollectionBoyCautionMoneyHistoryRepository extends MongoRepository<CollectionBoyCautionMoneyHistoryCollection, ObjectId>{

	CollectionBoyCautionMoneyHistoryCollection findByCollectionBoyId(ObjectId userId);
	
}
