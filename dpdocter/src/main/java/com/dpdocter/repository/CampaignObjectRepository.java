package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.beans.CampaignObjectCollection;

public interface CampaignObjectRepository extends MongoRepository<CampaignObjectCollection, ObjectId>,
		PagingAndSortingRepository<CampaignObjectCollection, ObjectId> {

	List<CampaignObjectCollection> findByCampaignId(String id);

}
