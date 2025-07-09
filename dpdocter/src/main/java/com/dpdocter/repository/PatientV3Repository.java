package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientCollection;
@Repository
public interface PatientV3Repository extends MongoRepository<PatientCollection, ObjectId> {
		List<PatientCollection> findByUserId(ObjectId userId);

		PatientCollection findByUserIdAndDoctorIdAndLocationIdAndHospitalId(ObjectId userId, ObjectId doctorId,
				ObjectId locationId, ObjectId hospitalId);

		List<PatientCollection> findByUserIdAndLocationIdAndHospitalId(ObjectId userId, ObjectId locationId, ObjectId hospitalId);

		@Query(value = "{'locationId':?0, 'hospitalId':?1, 'registrationDate' : {'$gt' : ?2, '$lte' : ?3}}", count = true)
		Integer findTodaysRegisteredPatient(ObjectId locationId, ObjectId hospitalId, Long start, Long end);
		
		@Query("{'locationId': ?0, 'hospitalId': ?1, 'PNUM': {$ne: null}}")
		List<PatientCollection> findByLocationIdAndHospitalIdAndPNUMNotNull(ObjectId locationObjectId, ObjectId hospitalObjectId, Pageable pageRequest);
		
		@Query(value = "{'doctorId':?0, 'locationId':?1, 'hospitalId': ?2, 'PID':?3, 'userId': {'$ne' : ?4}}", count = true)
		Integer findPatientByPID(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String generatedId, ObjectId userId);

		@Query(value = "{'doctorId':?0, 'locationId':?1, 'hospitalId': ?2, 'PID':?3}", count = true)
		Integer findPatientByPID(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String generatedId);

}
