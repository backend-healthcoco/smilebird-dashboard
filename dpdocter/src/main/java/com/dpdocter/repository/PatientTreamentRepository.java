package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PatientTreatmentCollection;

public interface PatientTreamentRepository extends MongoRepository<PatientTreatmentCollection, ObjectId> {

	public PatientTreatmentCollection findByIdAndDoctorIdAndLocationIdAndHospitalId(ObjectId treatmentId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);
}

