package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ShopifyCustomerCollection;

public interface ShopifyCustomerRepository extends MongoRepository<ShopifyCustomerCollection,ObjectId>{

}
