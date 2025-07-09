package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorSchoolAssociationCollection;

public interface DoctorSchoolAssociationRepository extends MongoRepository<DoctorSchoolAssociationCollection, ObjectId> {

}
