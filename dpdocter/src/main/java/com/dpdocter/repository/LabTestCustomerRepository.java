package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LabTestCustomerdetailCollection;

public interface LabTestCustomerRepository  extends MongoRepository<LabTestCustomerdetailCollection, ObjectId> {

}

