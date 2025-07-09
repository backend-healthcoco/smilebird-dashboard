package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SessionTopicCollection;

public interface SessionTopicRepository extends MongoRepository<SessionTopicCollection, ObjectId> {
	
	List<SessionTopicCollection> findByTopicIn(List<String> topics);
}
