package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserRoleCollection;

public interface UserRoleRepository extends MongoRepository<UserRoleCollection, ObjectId> {
    public List<UserRoleCollection> findByUserId(ObjectId userId);
    
	public UserRoleCollection findByUserIdAndLocationId(ObjectId doctorObjectId, ObjectId locationObjectId);

}
