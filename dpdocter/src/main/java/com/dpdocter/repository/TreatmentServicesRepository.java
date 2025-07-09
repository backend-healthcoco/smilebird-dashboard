package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.TreatmentServicesCollection;

public interface TreatmentServicesRepository extends MongoRepository<TreatmentServicesCollection, ObjectId> {
	public List<TreatmentServicesCollection> findByIdIn(List<ObjectId> specialityIds);

	public TreatmentServicesCollection findByTreatmentCodeAndDoctorId(String treatmentCode, ObjectId doctorId);

}
