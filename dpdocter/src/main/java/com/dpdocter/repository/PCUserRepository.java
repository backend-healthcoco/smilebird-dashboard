package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PCUserCollection;

/**
 * @author veeraj
 */
@Repository
public interface PCUserRepository extends MongoRepository<PCUserCollection, ObjectId> {
	public PCUserCollection findByUserName(String userName);

//	@Query("{'emailAddress' : {$regex : '^?0$', $options : 'i'}}")
	public List<PCUserCollection> findByEmailAddressIgnoreCase(String emailAddress);
	
	public PCUserCollection findByMrCode(String mrCode);

	

}
