package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UploadFileCollection;

public interface UploadFileRepository extends MongoRepository<UploadFileCollection, ObjectId> {

}
