package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PatientQueueCollection;

public interface PatientQueueRepository extends MongoRepository<PatientQueueCollection, ObjectId>, PagingAndSortingRepository<PatientQueueCollection, ObjectId> {

}
