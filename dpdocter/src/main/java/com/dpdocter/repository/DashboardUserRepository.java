package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DashboardUserCollection;

public interface DashboardUserRepository extends MongoRepository<DashboardUserCollection, ObjectId> {

	public DashboardUserCollection findByCompanyId(String id);
}
