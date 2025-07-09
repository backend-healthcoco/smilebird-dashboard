package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiagnosticTestPickUpSlotCollection;

public interface DiagnosticTestPickUpSlotRepository extends MongoRepository<DiagnosticTestPickUpSlotCollection, ObjectId>, PagingAndSortingRepository<DiagnosticTestPickUpSlotCollection, ObjectId> {

}
