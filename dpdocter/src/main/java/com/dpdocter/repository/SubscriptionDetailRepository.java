package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.SubscriptionDetailCollection;

public interface SubscriptionDetailRepository extends MongoRepository<SubscriptionDetailCollection, ObjectId>,
		PagingAndSortingRepository<SubscriptionDetailCollection, ObjectId> {

	public SubscriptionDetailCollection findByDoctorId(ObjectId doctorId);
	
	public List<SubscriptionDetailCollection> findByDoctorIdAndLicenseId(ObjectId doctorId , ObjectId licenseId);

}
