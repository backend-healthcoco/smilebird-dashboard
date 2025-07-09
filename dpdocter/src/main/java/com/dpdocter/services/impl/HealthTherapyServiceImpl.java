package com.dpdocter.services.impl;


import java.io.IOException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.HealthPlan;
import com.dpdocter.beans.HealthPlanResponse;
import com.dpdocter.beans.HealthPlanTypeResponse;
import com.dpdocter.collections.HealthPlanCollection;
import com.dpdocter.enums.HealthPackagesPlanType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.HealthPlanRepository;
import com.dpdocter.response.HealthiansPlanObject;
import com.dpdocter.services.HealthTherapyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class HealthTherapyServiceImpl implements HealthTherapyService {

	private static Logger logger = LogManager.getLogger(HealthTherapyServiceImpl.class.getName());

	@Autowired
	HealthPlanRepository healthPlanRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${healthians_secret_key}")
	private String secret;


	@Override
	public List<HealthPlanResponse> getHealthTherapyPlans(int page, int size, String type, boolean isDiscarded,
			String searchTerm) {
		List<HealthPlanResponse> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (type != null) {
				criteria.and("type").is(type);
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, HealthPlanCollection.class, HealthPlanResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countPlans(String type, boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);

			criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
					new Criteria("title").regex("^" + searchTerm));

			if (type != null) {
				criteria.and("type").is(type);
			}

			response = (int) mongoTemplate.count(new Query(criteria), HealthPlanCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean deleteHealthTherapyPlan(String planId, boolean isDiscarded) {
		Boolean response = false;
		try {
			HealthPlanCollection healthPlanCollection = healthPlanRepository.findById(new ObjectId(planId))
					.orElse(null);
			if (healthPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			healthPlanCollection.setIsDiscarded(isDiscarded);
			healthPlanCollection.setUpdatedTime(new Date());
			healthPlanCollection = healthPlanRepository.save(healthPlanCollection);
			response = true;
//			BeanUtil.map(countryCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while deleting the plan  " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while deleting the plan");
		}

		return response;
	}

	@Override
	public HealthPlanResponse addEditHealthTherapyPlan(HealthPlan request) {
		HealthPlanResponse response = null;
		try {

			HealthPlanCollection collection = null;
			collection = new HealthPlanCollection();
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				collection = healthPlanRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (collection == null) {
					throw new BusinessException(ServiceError.NoRecord, " Plan Not found with Id");
				}
				collection.setAmount(null);
				request.setCreatedTime(collection.getCreatedTime());
				request.setAdminCreatedTime(collection.getAdminCreatedTime());
				request.setCreatedBy(collection.getCreatedBy());
				BeanUtil.map(request, collection);

			} else {
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setAdminCreatedTime(new Date());
				collection.setCreatedBy("Admin");
				collection.setPlanUId(
						UniqueIdInitial.HEALTH_THERAPY_PLAN.getInitial() + DPDoctorUtils.generateRandomId());
			}

			collection = healthPlanRepository.save(collection);
			response = new HealthPlanResponse();
			BeanUtil.map(collection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit  Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public HealthPlanResponse getHealthTherapyPlanById(String planId) {
		HealthPlanResponse response = null;
		try {
			HealthPlanCollection healthPlanCollection = healthPlanRepository.findById(new ObjectId(planId))
					.orElse(null);
			if (healthPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new HealthPlanResponse();
			BeanUtil.map(healthPlanCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;

	}

	@Override
	public HealthPlanResponse getHealthTherapyPlanBySlugUrl(String slugURL, String planUId) {
		HealthPlanResponse response = null;
		try {
			HealthPlanCollection healthPlanCollection = healthPlanRepository.findByPlanUId(planUId);
			if (healthPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new HealthPlanResponse();
			BeanUtil.map(healthPlanCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public Integer countPlansTitles(HealthPackagesPlanType type, boolean isDiscarded, String country) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);

			if (country != null) {
				criteria.and("country").is(country);
			}

			if (type != null) {
				criteria.and("type").is(type);
			}

			response = (int) mongoTemplate.count(new Query(criteria), HealthPlanCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<HealthPlanTypeResponse> getHealthTherapyPlansTitles(int page, int size, HealthPackagesPlanType type,
			boolean isDiscarded, String country) {
		List<HealthPlanTypeResponse> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);

			if (country != null) {
				criteria.and("country").is(country);
			}
			if (type != null) {
				criteria.and("type").is(type);
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, HealthPlanCollection.class, HealthPlanTypeResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public Response<Object> getHealthiensPlans(String partnerName, boolean isDiscarded) throws IOException {

		Response<Object> response = new Response<Object>();

		Unirest.setTimeouts(0, 0);
		HttpResponse<JsonNode> auth = null;
//		try {
////			auth = Unirest.get("http://t7crm.healthians.co.in/api/coco/getAccessToken")
////					.basicAuth(keyId, secret)
////					.asJson();
//						
//			System.out.println(auth.getBody());
//		} catch (UnirestException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		System.out.println(auth.getBody().getObject().get("access_token"));
		
		System.out.println(auth.getStatus());
		
		
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<JsonNode> serverResponse = Unirest.get("http://t7crm.healthians.co.in/api/coco/getPopularPackages")
					.header("accept", "application/json")
					.header("Authorization", "Bearer " + auth.getBody().getObject().get("access_token")).asJson();

			System.out.println("response 1  arr"+serverResponse.getBody().getArray() );
			System.out.println(serverResponse.getBody().getObject().get("packages"));
//			packageList =  (HealthiansPlanObject) serverResponse.getBody().getObject().get("packages");
//			ArrayList<HealthiansPlanObject> list = ArrayUtil.convert(serverResponse.getBody().getObject().get("packages"));
			ObjectMapper mapper = new ObjectMapper();
			List<HealthiansPlanObject> ppl2 = Arrays.asList(mapper.readValue(serverResponse.getBody().getObject().get("packages").toString(), HealthiansPlanObject[].class));

			System.out.println(serverResponse.getStatus());
			
			response.setDataList(ppl2);
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		  ObjectMapper mapper = new ObjectMapper();

//		response = (List<HealthiansPlanObject>) mapper.readValue(output.toString(), HealthiansPlanObject.class);

		return response;
	}

}
