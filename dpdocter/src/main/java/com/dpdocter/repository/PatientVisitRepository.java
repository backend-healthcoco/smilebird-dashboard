package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.enums.VisitedFor;

@Repository
public interface PatientVisitRepository extends MongoRepository<PatientVisitCollection, ObjectId>, PagingAndSortingRepository<PatientVisitCollection, ObjectId> {

    PatientVisitCollection findByRecordId(ObjectId recordId);

    PatientVisitCollection findByPrescriptionId(ObjectId prescriptionId);

    PatientVisitCollection findByClinicalNotesId(ObjectId clinicalNotesId);

    @Query(value = "{'doctorId': ?0, 'patientId': ?1, 'hospitalId':?2, 'locationId': ?3, 'visitedFor': {$in : ?4}, 'discarded': ?5}", count = true)
    Integer getVisitCount(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId, List<VisitedFor> visitedFors, boolean discarded);

    @Query(value = "{'patientId':{$in: ?0}, 'doctorId': ?1, 'hospitalId':?2, 'locationId': ?3}", count = true)
    Integer getVisitCount(List<ObjectId> patientIds, ObjectId doctorId, ObjectId hospitalId, ObjectId locationId);

    @Query(value = "{'patientId': ?0, 'visitedFor': {$in : ?1}, 'discarded': ?2}", count = true)
    Integer getVisitCount(ObjectId patientId, List<VisitedFor> visitedFors, boolean discarded);

	PatientVisitCollection findByAppointmentId(String appointmentId);

}
