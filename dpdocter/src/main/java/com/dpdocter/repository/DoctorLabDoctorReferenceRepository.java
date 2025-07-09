package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorLabDoctorReferenceCollection;

@Repository
public interface DoctorLabDoctorReferenceRepository
		extends MongoRepository<DoctorLabDoctorReferenceCollection, ObjectId> {

}
