package com.dpdocter.repository;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorClinicProfileCollection;

public interface DoctorClinicProfileRepository extends MongoRepository<DoctorClinicProfileCollection, ObjectId> {

	List<DoctorClinicProfileCollection> findByLocationId(ObjectId locationId);

	DoctorClinicProfileCollection findByDoctorIdAndLocationId(ObjectId doctorId, ObjectId locationId);

	List<DoctorClinicProfileCollection> findByDoctorIdAndIsActivateIsTrue(ObjectId doctorId);

	List<DoctorClinicProfileCollection> findByDoctorId(ObjectId doctorId);

	List<DoctorClinicProfileCollection> findByLocationId(ObjectId locationId, Pageable pageRequest);

	List<DoctorClinicProfileCollection> findByLocationId(ObjectId locationId, Sort sort);

	@Query(value = "{'doctorId': ?0, 'locationId': ?1}", count = true)
	Integer countByUserIdAndLocationId(ObjectId id, ObjectId objectId);

	List<DoctorClinicProfileCollection> findByLocationIdIn(Set<ObjectId> locationIds);

	@Query(value = "{'doctorSlugURL':{$regex:'?0',$options:'i'}}", count = true)
	Integer countBySlugUrl(String slugUrl);

	List<DoctorClinicProfileCollection> findByIsSendBirthdaySMS(Boolean b);

	List<DoctorClinicProfileCollection> findByLocationIdAndIsActivate(ObjectId id, Boolean active);
}
