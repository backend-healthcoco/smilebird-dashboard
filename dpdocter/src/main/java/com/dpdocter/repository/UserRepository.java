package com.dpdocter.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.UserCollection;

/**
 * @author veeraj
 */
@Repository
public interface UserRepository extends MongoRepository<UserCollection, ObjectId> {
	public UserCollection findByUserName(String userName);

	public Optional<UserCollection> findByEmailAddress(String userName);

	public List<UserCollection> findByEmailAddressStartsWithIgnoreCase(String emailAddress);

	public List<UserCollection> findByMobileNumber(String mobileNumber);

	public UserCollection findByUserNameAndEmailAddress(String userName, String emailAddress);

	public List<UserCollection> findByIsActive(boolean isActive, Pageable pageRequest);

	public List<UserCollection> findByIsActive(boolean isActive, Sort sort);

	@Query(value = "{'isActive' : ?0}", count = true)
	public Integer countInactiveDoctors(boolean isActive);
	
	public UserCollection findByMobileNumberAndUserState(String mobileNumber, String userState);

	public List<UserCollection> findByUserState(String string2);

	@Query(value = "{'id' : ?0}")
	public UserCollection findDoctorByDoctorId(String doctorId);

	public List<UserCollection> findByEmailAddressAndUserState(String mobileNumberValue, String state);

	public UserCollection findByIdAndIsActive(ObjectId doctorId, Boolean active);
	
}
