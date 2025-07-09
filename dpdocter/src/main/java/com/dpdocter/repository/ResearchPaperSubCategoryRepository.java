package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ResearchPaperSubCategoryCollection;

public interface ResearchPaperSubCategoryRepository extends MongoRepository<ResearchPaperSubCategoryCollection, ObjectId>  {

}
