package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OnlineConsultionPaymentCollection;

public interface OnlineConsultationPaymentRepository extends MongoRepository<OnlineConsultionPaymentCollection, ObjectId>{

	String countByProblemDetailsId(ObjectId objectId);

	OnlineConsultionPaymentCollection findByDoctorId(ObjectId objectId);

	

	OnlineConsultionPaymentCollection findByUserIdAndReciept(ObjectId objectId, String receipt);

	OnlineConsultionPaymentCollection findByReciept(String receipt);

}
