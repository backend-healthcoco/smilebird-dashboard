package com.dpdocter.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SpecialityCollection;

public interface SpecialityRepository extends MongoRepository<SpecialityCollection, ObjectId> {

	List<SpecialityCollection> findBySuperSpecialityIn(List<String> speciality);

	List<SpecialityCollection> findBySpecialityIn(List<String> speciality);

	List<SpecialityCollection> findByIdIn(Collection<ObjectId> specialities);

}
