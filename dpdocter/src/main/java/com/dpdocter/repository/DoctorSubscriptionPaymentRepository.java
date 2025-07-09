package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorSubscriptionPaymentCollection;

public interface DoctorSubscriptionPaymentRepository
		extends MongoRepository<DoctorSubscriptionPaymentCollection, ObjectId> {

}
