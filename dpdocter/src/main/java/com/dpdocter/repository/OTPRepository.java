package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.OTPCollection;

public interface OTPRepository extends MongoRepository<OTPCollection, ObjectId>, PagingAndSortingRepository<OTPCollection, ObjectId> {
    OTPCollection findByMobileNumberAndOtpNumberAndGeneratorId(String mobileNumber, String otpNumber, String generatorId);
}
