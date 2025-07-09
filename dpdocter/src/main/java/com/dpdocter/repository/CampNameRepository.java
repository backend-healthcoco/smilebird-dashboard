package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.CampNameCollection;

public interface CampNameRepository extends MongoRepository<CampNameCollection, ObjectId>,
		PagingAndSortingRepository<CampNameCollection, ObjectId> {

	List<CampNameCollection> findByCampNameIn(String[] campName);

	CampNameCollection findByCampName(String string);


	List<CampNameCollection> findByAssociateDoctorIdsIn(List<ObjectId> objectIds);

}
