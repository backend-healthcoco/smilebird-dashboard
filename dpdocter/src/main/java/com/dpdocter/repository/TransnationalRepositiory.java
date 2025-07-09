package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.TransactionalCollection;

public interface TransnationalRepositiory extends MongoRepository<TransactionalCollection, ObjectId> {

    TransactionalCollection findByResourceIdAndResource(ObjectId resourceId, String resource);

}
