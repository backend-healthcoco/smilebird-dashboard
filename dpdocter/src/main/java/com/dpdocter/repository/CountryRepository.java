package com.dpdocter.repository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.WellnessUserCollection;
import com.dpdocter.enums.PeriodEnums;



public interface CountryRepository extends MongoRepository<CountryCollection, ObjectId> {
	
	@Query("{'countryCode' : ?0}")
	CountryCollection findByCountryCode(String string);

}
