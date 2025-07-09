package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.dpdocter.collections.UserCollection;

public interface UserRepositoryForAppointment extends MongoRepository<UserCollection, ObjectId> {

	// for appointment api as in UserRepository same method present with single response
    public List<UserCollection> findByMobileNumberAndUserState(String mobileNumber, String userState);
}
