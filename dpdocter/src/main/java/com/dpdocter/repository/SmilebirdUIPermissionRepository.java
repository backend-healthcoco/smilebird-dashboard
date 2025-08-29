package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SmilebirdUIPermissionCollection;

public interface SmilebirdUIPermissionRepository extends MongoRepository<SmilebirdUIPermissionCollection, ObjectId>{
	SmilebirdUIPermissionCollection findByAdminId(ObjectId adminId);
}
