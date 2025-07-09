package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalLabDoctorAssociationCollection;

public interface DentalLabDoctorAssociationRepository extends MongoRepository<DentalLabDoctorAssociationCollection, ObjectId>{

	public DentalLabDoctorAssociationCollection findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(ObjectId doctorId, ObjectId locationId,ObjectId hospitalId,ObjectId dentalLabLocationId,ObjectId dentalLabHospitalId);

}
