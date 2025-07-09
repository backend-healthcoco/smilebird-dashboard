package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RoleCollection;

/**
 * @author veeraj
 */
@Repository
public interface RoleRepository extends MongoRepository<RoleCollection, ObjectId> {

	public RoleCollection findByRole(String role);
	
	public List<RoleCollection> findByLocationIdIsNullAndHospitalIdIsNullAndUpdatedTimeGreaterThan(Date date, Pageable pageRequest);

	public List<RoleCollection> findByLocationIdIsNullAndHospitalIdIsNullAndUpdatedTimeGreaterThan(Date date, Sort sort);

	public List<RoleCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThan(ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageRequest);

	public List<RoleCollection> findByLocationIdIsNullAndHospitalIdIsNullAndUpdatedTimeGreaterThan(ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

	public List<RoleCollection> findByUpdatedTimeGreaterThan(Date date, Pageable pageRequest);

	public List<RoleCollection> findByUpdatedTimeGreaterThan(Date date, Sort sort);

	@Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}} , {'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2}}]}")
	public List<RoleCollection> findCustomGlobal(ObjectId locationId, ObjectId hospitalId, Date date,
			Pageable pageRequest);

	@Query("{'$or': [{'locationId': ?0, 'hospitalId': ?1, 'updatedTime': {'$gt': ?2}} , {'locationId': null, 'hospitalId': null, 'updatedTime': {'$gt': ?2}}]}")
	public List<RoleCollection> findCustomGlobal(ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

	public List<RoleCollection> findByLocationIdAndHospitalId(ObjectId locationId, ObjectId hospitalId);

	@Query(value = "{'id':{$in :?0}, 'role': ?1}", count = true)
	public Integer findCountByIdAndRole(Collection<ObjectId> roleIds, String role);

	public List<RoleCollection> findByRoleInAndLocationIdAndHospitalId(List<String> roles, ObjectId locationId, ObjectId hospitalId);

    public RoleCollection findByRoleAndLocationIdIsNullAndHospitalIdIsNull(String role);

}
