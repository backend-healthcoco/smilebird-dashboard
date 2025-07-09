package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ConfexUserCollection;

public interface ConfexUserRepository extends MongoRepository<ConfexUserCollection, ObjectId>,
		PagingAndSortingRepository<ConfexUserCollection, ObjectId> {

	public ConfexUserCollection findByUserName(String userName);

	
	public List<ConfexUserCollection> findByConferenceIdAndUserType(ObjectId conferenceId, String userType);

	public ConfexUserCollection findByCompanyId(ObjectId companyId);


	public ConfexUserCollection findByEmailAddress(String companyEmailId);

}
