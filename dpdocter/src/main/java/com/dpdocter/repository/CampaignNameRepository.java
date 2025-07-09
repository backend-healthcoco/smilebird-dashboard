package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.CampaignNameCollection;

public interface CampaignNameRepository extends MongoRepository<CampaignNameCollection, ObjectId>,
		PagingAndSortingRepository<CampaignNameCollection, ObjectId> {

	List<CampaignNameCollection> findByIdIn(List<ObjectId> campaignObjectIds);

}
