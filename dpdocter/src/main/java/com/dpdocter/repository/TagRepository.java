package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.TagCollection;


@Repository
public interface TagRepository
		extends MongoRepository<TagCollection, ObjectId>, PagingAndSortingRepository<TagCollection, ObjectId> {

}
