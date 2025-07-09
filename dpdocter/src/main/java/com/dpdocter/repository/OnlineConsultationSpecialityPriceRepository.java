package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.dpdocter.collections.OnlineConsultationSpecialityPriceCollection;

public interface OnlineConsultationSpecialityPriceRepository extends MongoRepository<OnlineConsultationSpecialityPriceCollection, ObjectId>{

}
