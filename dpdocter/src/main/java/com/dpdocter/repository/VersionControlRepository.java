package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.VersionControlCollection;

public interface VersionControlRepository extends MongoRepository<VersionControlCollection, String> {
	
	    public VersionControlCollection findByAppTypeAndDeviceType(String appType , String deviceType);
	 
	 @Query("{'$or': [{'appType' : {$regex : '^?0*', $options : 'i'}},{'deviceType' : {$regex : '^?0*', $options : 'i'}}]}")
	    public List<VersionControlCollection> findByApplicationTypeAndDeviceType (String appType , String deviceType);

}
