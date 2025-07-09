package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorHospitalDentalImagingAssociationCollection;

public interface DoctorHospitalDentalImagingAssociationRepository extends MongoRepository<DoctorHospitalDentalImagingAssociationCollection, ObjectId>{

	public DoctorHospitalDentalImagingAssociationCollection findByDoctorIdAndDoctorLocationIdAndHospitalId(ObjectId doctorId, ObjectId doctorLocationId, ObjectId hospitalId);

}
