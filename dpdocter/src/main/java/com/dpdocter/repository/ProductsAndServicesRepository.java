package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ProductsAndServicesCollection;

public interface ProductsAndServicesRepository extends MongoRepository<ProductsAndServicesCollection, ObjectId> {

    public List<ProductsAndServicesCollection> findBySpecialityIdsIn(List<ObjectId> specialityIds);
}
