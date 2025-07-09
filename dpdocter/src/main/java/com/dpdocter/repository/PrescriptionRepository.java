package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrescriptionCollection;

public interface PrescriptionRepository extends MongoRepository<PrescriptionCollection, ObjectId>,
		PagingAndSortingRepository<PrescriptionCollection, ObjectId> {

	List<PrescriptionCollection> findByPatientIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId patientId, Date date, List<Boolean> discards,
			Pageable pageRequest);

	List<PrescriptionCollection> findByPatientIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId patientId, Date date, List<Boolean> discards, Sort sort);

	PrescriptionCollection findByUniqueEmrIdAndPatientId(String uniqueEmrId, ObjectId patientId);

	 @Query(value = "{'patientId': ?0, 'hospitalId' : {'$ne' : ?1}, 'locationId' : {'$ne' : ?2}}", count = true)
	    Integer getPrescriptionCountForOtherLocations(ObjectId patientId, ObjectId hospitalId, ObjectId locationId);
	 
	@Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
	Integer getPrescriptionCountForOtherDoctors(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId,
			ObjectId locationId);
}
