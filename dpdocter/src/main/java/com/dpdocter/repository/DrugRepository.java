package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository extends MongoRepository<DrugCollection, ObjectId>, PagingAndSortingRepository<DrugCollection, ObjectId> {
   
	@Query(value = "{'drugCode' : ?0}", count = true)
	Integer countByDrugCode(String drugCode);

	DrugCollection findByDrugCodeAndDoctorId(String drugCode, ObjectId doctorId);

}
