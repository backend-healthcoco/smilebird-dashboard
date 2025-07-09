package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Subscription;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.SubscriptionHistoryCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.SubscriptionHistoryRepository;
import com.dpdocter.services.SubscriptionHistoryServices;

import common.util.web.DPDoctorUtils;


@Service
public class SubscriptionHistoryServiceImpl implements SubscriptionHistoryServices{
	
	private static Logger logger = LogManager.getLogger(SubscriptionHistoryServiceImpl.class.getName());

	@Autowired
	SubscriptionHistoryRepository subscriptionHistoryRepository;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Subscription addEditSubscriptionHistory(Subscription request) {
		Subscription response = null;
		try {
			SubscriptionHistoryCollection subscriptionHistoryCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				subscriptionHistoryCollection = subscriptionHistoryRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (subscriptionHistoryCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Subscription Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(subscriptionHistoryCollection.getCreatedBy());	
				request.setCreatedTime(subscriptionHistoryCollection.getCreatedTime());
				BeanUtil.map(request, subscriptionHistoryCollection);

			} else {
				subscriptionHistoryCollection = new SubscriptionHistoryCollection();
				BeanUtil.map(request, subscriptionHistoryCollection);
				subscriptionHistoryCollection.setCreatedBy("ADMIN");
				subscriptionHistoryCollection.setUpdatedTime(new Date());
				subscriptionHistoryCollection.setCreatedTime(new Date());
			}
			subscriptionHistoryCollection = subscriptionHistoryRepository.save(subscriptionHistoryCollection);
			response = new Subscription();

			BeanUtil.map(subscriptionHistoryCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Subscription  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Subscription " + e.getMessage());

		}
		return response;

	}
	
	@Override
	public List<Subscription> getSubscriptionHistory(int size, int page, Boolean isDiscarded, String searchTerm,String doctorId) {
		List<Subscription> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded).and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, SubscriptionHistoryCollection.class, Subscription.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Subscription " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscription " + e.getMessage());

		}
		return response;

	}

	@Override
	public Integer countSubscriptionHistory(Boolean isDiscarded, String searchTerm,String doctorId) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded).and("doctorId").is(new ObjectId(doctorId));
			criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
					new Criteria("packageName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), SubscriptionHistoryCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Subscription " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while Subscription " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean discardSubscriptionHistory(String id, Boolean isDiscarded) {
		Boolean response = null;
		try {
			SubscriptionHistoryCollection subscriptionHistoryCollection = subscriptionHistoryRepository.findById(new ObjectId(id)).orElse(null);
			if (subscriptionHistoryCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			subscriptionHistoryCollection.setIsDiscarded(isDiscarded);
			subscriptionHistoryCollection.setUpdatedTime(new Date());
			subscriptionHistoryCollection = subscriptionHistoryRepository.save(subscriptionHistoryCollection);
			response = true;
//			BeanUtil.map(countryCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while deleting the Subscription History  " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while deleting the Subscription History");
		}

		return response;
	}

}
