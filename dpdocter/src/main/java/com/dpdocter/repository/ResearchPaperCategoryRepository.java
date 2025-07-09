package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ResearchPaperCategoryCollection;

public interface ResearchPaperCategoryRepository extends MongoRepository<ResearchPaperCategoryCollection, ObjectId> {

}
