package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CompanyDetailCollection;

//for covid project

public interface CompanyDetailRepository extends MongoRepository<CompanyDetailCollection, ObjectId> {
	public CompanyDetailCollection findBycompanyEmailId(String companyEmailId);

}
