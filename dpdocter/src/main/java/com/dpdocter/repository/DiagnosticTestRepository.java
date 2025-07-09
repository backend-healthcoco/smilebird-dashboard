package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DiagnosticTestCollection;

public interface DiagnosticTestRepository extends MongoRepository<DiagnosticTestCollection, ObjectId> {

	List<DiagnosticTestCollection> findByUpdatedTimeGreaterThanAndDiscardedInAndTestNameRegex(Date date, List<Boolean> discards, String searchTerm, Sort sort);

	DiagnosticTestCollection findByTestNameRegexAndLocationId(String testName, ObjectId locationId);

}
