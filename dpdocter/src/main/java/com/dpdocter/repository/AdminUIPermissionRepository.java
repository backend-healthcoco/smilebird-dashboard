package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.AdminUIPermissionCollection;

public interface AdminUIPermissionRepository extends MongoRepository<AdminUIPermissionCollection, ObjectId>{
	 AdminUIPermissionCollection findByAdminId(ObjectId adminId);
}
