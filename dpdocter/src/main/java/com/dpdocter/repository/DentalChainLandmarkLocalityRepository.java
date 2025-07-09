package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalChainLandmarkLocalityCollection;

public interface DentalChainLandmarkLocalityRepository  extends MongoRepository<DentalChainLandmarkLocalityCollection, ObjectId> {

    List<DentalChainLandmarkLocalityCollection> findByCityId(ObjectId cityId);

}
