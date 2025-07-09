package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RecordsCollection;

@Repository
public interface RecordsRepository
		extends MongoRepository<RecordsCollection, ObjectId>, PagingAndSortingRepository<RecordsCollection, ObjectId> {

	@Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
	Integer getRecordsForOtherDoctors(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId);

	@Query(value = "{'patientId': ?0, 'hospitalId' : {'$ne' : ?1}, 'locationId' : {'$ne' : ?2}}", count = true)
	Integer getRecordsForOtherLocations(ObjectId patientId, ObjectId hospitalId, ObjectId locationId);

	@Query(value = "{'patientId': ?0, 'hospitalId' : {'$ne' : ?1}, 'locationId' : {'$ne' : ?2}}", count = true)
	Integer getClinicalNotesCountForOtherLocations(ObjectId patientId, ObjectId hospitalId, ObjectId locationId);
}
