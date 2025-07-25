package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorPatientReceiptCollection;

@Repository
public interface DoctorPatientReceiptRepository extends MongoRepository<DoctorPatientReceiptCollection, ObjectId> {
    // Custom queries for receipts can be added here if needed
} 