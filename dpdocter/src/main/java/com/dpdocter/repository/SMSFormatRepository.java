package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SMSFormatCollection;

public interface SMSFormatRepository extends MongoRepository<SMSFormatCollection, ObjectId> {

    SMSFormatCollection findByDoctorIdAndLocationIdAndHospitalIdAndType(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String type);

    List<SMSFormatCollection> findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

}
