package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FollowupCommunicationCollection;

public interface FollowupCommunicationRepository extends MongoRepository<FollowupCommunicationCollection, ObjectId>{

	FollowupCommunicationCollection findByUserId(ObjectId userId);
}
