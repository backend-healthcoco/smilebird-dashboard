package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ShopifyOrderTransactionCollection;

public interface ShopifyOrderTransactionRepository extends MongoRepository<ShopifyOrderTransactionCollection,ObjectId>{

}
