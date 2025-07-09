package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PresentingComplaintOralCavityCollection;

public interface PresentingComplaintOralCavityRepository extends MongoRepository<PresentingComplaintOralCavityCollection, ObjectId>{

}
