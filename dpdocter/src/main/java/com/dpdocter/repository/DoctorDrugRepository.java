package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorDrugCollection;

public interface DoctorDrugRepository extends MongoRepository<DoctorDrugCollection, ObjectId>, PagingAndSortingRepository<DoctorDrugCollection, ObjectId> {
}
