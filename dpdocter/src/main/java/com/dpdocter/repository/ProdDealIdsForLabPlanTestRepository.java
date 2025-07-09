package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LabTestCustomerdetailCollection;
import com.dpdocter.collections.ProdDealIdsForLabPlanTestCollection;

public interface ProdDealIdsForLabPlanTestRepository
		extends MongoRepository<ProdDealIdsForLabPlanTestCollection, ObjectId> {

}
