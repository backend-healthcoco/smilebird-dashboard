package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CollectionBoyCautionMoneyCollection;

public interface CollectionBoyCautionMoneyRepository extends MongoRepository<CollectionBoyCautionMoneyCollection, ObjectId>{

	CollectionBoyCautionMoneyCollection findByCollectionBoyId(ObjectId userId);
}
