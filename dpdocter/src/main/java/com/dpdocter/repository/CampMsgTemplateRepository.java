package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.webservices.v3.CampMsgTemplateCollection;

public interface CampMsgTemplateRepository extends MongoRepository<CampMsgTemplateCollection, ObjectId>,
		PagingAndSortingRepository<CampMsgTemplateCollection, ObjectId> {

	List<CampMsgTemplateCollection> findByIdIn(List<ObjectId> treatmentObjectIds);

}
