package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.AcosCollection;
@Repository
public interface AcosRepository extends MongoRepository<AcosCollection, ObjectId> {

	List<AcosCollection> findByIdIn(List<ObjectId> acosIds);

}
