package com.dpdocter.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AlignerPackageCollection;
import com.dpdocter.collections.AlignerUserPlanCollection;

public interface AlignerUserPlanRepository extends MongoRepository<AlignerUserPlanCollection, ObjectId>,
		PagingAndSortingRepository<AlignerUserPlanCollection, ObjectId> {

	List<AlignerUserPlanCollection> findByUserId(ObjectId objectId);
}
