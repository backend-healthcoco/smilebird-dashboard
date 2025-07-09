package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LabAssociationCollection;

public interface LabAssociationRepository extends MongoRepository<LabAssociationCollection, ObjectId> {

	public LabAssociationCollection findByParentLabIdAndDaughterLabId(ObjectId parentLabId, ObjectId daughterLabId);
	
}
