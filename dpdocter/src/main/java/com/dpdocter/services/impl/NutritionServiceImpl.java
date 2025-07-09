package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.GenericCodesAndReaction;
import com.dpdocter.beans.NutritionAppointment;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.beans.PlanPriceDescription;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.Testimonial;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.collections.NutritionAppointmentCollection;
import com.dpdocter.collections.NutritionPlanCollection;
import com.dpdocter.collections.NutritionRDACollection;
import com.dpdocter.collections.SubscriptionNutritionPlanCollection;
import com.dpdocter.collections.TestimonialCollection;
import com.dpdocter.collections.UserNutritionSubscriptionCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.LifeStyleType;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.NutritionAppointmentRepository;
import com.dpdocter.repository.NutritionPlanRepository;
import com.dpdocter.repository.NutritionRDARepository;
import com.dpdocter.repository.SubscritptionNutritionPlanRepository;
import com.dpdocter.repository.TestimonialRepository;
import com.dpdocter.request.AddEditTestimonialRequest;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.request.PlanPricesPutRemoveRequest;
import com.dpdocter.response.NutritionPlanNameWithCategoryResponse;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.dpdocter.services.NutritionService;
import com.mongodb.BasicDBObject;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class NutritionServiceImpl implements NutritionService {

	private static Logger logger = LogManager.getLogger(NutritionServiceImpl.class.getName());

	@Autowired
	private SubscritptionNutritionPlanRepository subscritptionNutritionPlanRepository;

	@Autowired
	private NutritionPlanRepository nutritionPlanRepository;

	@Autowired
	private NutritionAppointmentRepository nutritionAppointmentRepository;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private TestimonialRepository testimonialRepository;

	@Autowired
	private NutritionRDARepository nutritionRDARepository;
	
	@Value(value = "${image.path}")
	private String imagePath;

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Override
	public List<NutritionPlanType> getPlanType() {

		return Arrays.asList(NutritionPlanType.values());
	}

	@Override
	public NutritionPlan addEditNutritionPlan(NutritionPlan request) {
		NutritionPlan response = null;
		try {

			NutritionPlanCollection collection = null;
			NutritionPlanCollection oldNutritionPlanCollection = null;
			collection = new NutritionPlanCollection();
			BeanUtil.map(request, collection);
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				oldNutritionPlanCollection = nutritionPlanRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (oldNutritionPlanCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Nutrition Plan Not found with Id");
				}
				collection.setCreatedTime(oldNutritionPlanCollection.getCreatedTime());
				collection.setAdminCreatedTime(oldNutritionPlanCollection.getAdminCreatedTime());
				collection.setCreatedBy(oldNutritionPlanCollection.getCreatedBy());
			} else {
				collection.setCreatedTime(new Date());
				collection.setAdminCreatedTime(new Date());
				collection.setCreatedBy("admin");
			}
			if (!DPDoctorUtils.anyStringEmpty(collection.getPlanImage())) {
				collection.setPlanImage(collection.getPlanImage().replace(imagePath, ""));
			}
			if (!DPDoctorUtils.anyStringEmpty(collection.getBannerImage())) {
				collection.setBannerImage(collection.getBannerImage().replace(imagePath, ""));
			}

			switch (NutritionPlanType.valueOf(collection.getType().toString().toUpperCase())) {

			case WEIGHT_LOSS_PLAN: {
				collection.setRank(1);
				break;
			}
			case CONDITION_SPECIFIC: {
				collection.setRank(4);
				break;
			}
			case SPECIAL_PLANS: {
				collection.setRank(5);
				break;
			}
			case NUTRIENT_IMPROVEMENT: {
				collection.setRank(2);
				break;
			}
			case STAY_HEALTHY: {
				collection.setRank(3);
				break;
			}

			}
			collection = nutritionPlanRepository.save(collection);
			response = new NutritionPlan();
			BeanUtil.map(collection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getPlanImage())) {
				response.setPlanImage(getFinalImageURL(response.getPlanImage()));
			}
			if (!DPDoctorUtils.anyStringEmpty(response.getBannerImage())) {
				response.setBannerImage(getFinalImageURL(response.getBannerImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while addedit nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public NutritionPlan deleteNutritionPlan(String id) {
		NutritionPlan response = null;
		try {
			NutritionPlanCollection nutritionPlanCollection = nutritionPlanRepository.findById(new ObjectId(id)).orElse(null);
			nutritionPlanCollection.setUpdatedTime(new Date());
			nutritionPlanCollection.setDiscarded(!nutritionPlanCollection.getDiscarded());
			nutritionPlanCollection = nutritionPlanRepository.save(nutritionPlanCollection);
			response = new NutritionPlan();
			BeanUtil.map(nutritionPlanCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getPlanImage())) {
				response.setPlanImage(getFinalImageURL(response.getPlanImage()));
			}
			if (!DPDoctorUtils.anyStringEmpty(response.getBannerImage())) {
				response.setBannerImage(getFinalImageURL(response.getBannerImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while deleting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while deleting nutrition Plan " + e.getMessage());
		}
		return response;
	}

	@Override
	public NutritionPlanResponse getNutritionPlan(String id) {
		NutritionPlanResponse response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").is(new ObjectId(id)).and("subscriptionNutritionPlan.discarded")
					.is(false);

			aggregation = Aggregation.newAggregation(
				/*	Aggregation.lookup("subscription_nutrition_plan_cl", "_id", "nutritionPlanId",
							"subscriptionNutritionPlan"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$subscriptionNutritionPlan").append("preserveNullAndEmptyArrays",
									true))),*/
					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlanResponse> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlanResponse.class);
			response = results.getUniqueMappedResult();
			if (!DPDoctorUtils.anyStringEmpty(response.getPlanImage())) {
				response.setPlanImage(getFinalImageURL(response.getPlanImage()));
			}
			if (!DPDoctorUtils.anyStringEmpty(response.getBannerImage())) {
				response.setBannerImage(getFinalImageURL(response.getBannerImage()));
			}

		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<NutritionPlan> getNutritionPlans(List<ObjectId> idList) {
		List<NutritionPlan> response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(idList).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlan> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlan.class);
			response = results.getMappedResults();
			for (NutritionPlan plan : response) {
				if (!DPDoctorUtils.anyStringEmpty(plan.getPlanImage())) {
					plan.setPlanImage(getFinalImageURL(plan.getPlanImage()));
				}
				if (!DPDoctorUtils.anyStringEmpty(plan.getBannerImage())) {
					plan.setBannerImage(getFinalImageURL(plan.getBannerImage()));
				}
			}
		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan List" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition Plan List " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<NutritionPlan> getNutritionPlans(int page, int size, String type, long updatedTime,
			boolean discareded) {
		List<NutritionPlan> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria = criteria.and("type").is(type);
			}
			if (updatedTime > 0) {
				criteria = criteria.and("createdTime").gte(new Date(updatedTime));
			}

			criteria.and("discarded").is(discareded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<NutritionPlan> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlan.class);
			response = results.getMappedResults();
			for (NutritionPlan nutritionPlan : response) {
				if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
					nutritionPlan.setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
				}
				if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
					nutritionPlan.setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
				}
			}

		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public SubscriptionNutritionPlan addEditSubscriptionPlan(SubscriptionNutritionPlan request) {
		SubscriptionNutritionPlan response = null;
		try {
			SubscriptionNutritionPlanCollection oldsubscriptionNutritionPlanCollection = null;
			SubscriptionNutritionPlanCollection subscriptionNutritionPlanCollection = new SubscriptionNutritionPlanCollection();
			BeanUtil.map(request, subscriptionNutritionPlanCollection);
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				oldsubscriptionNutritionPlanCollection = subscritptionNutritionPlanRepository
						.findById(new ObjectId(request.getId())).orElse(null);

				if (oldsubscriptionNutritionPlanCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Nutrition Plan Not found with Id");
				}
				subscriptionNutritionPlanCollection.setCreatedBy("admin");
				subscriptionNutritionPlanCollection
						.setCreatedTime(oldsubscriptionNutritionPlanCollection.getCreatedTime());
				subscriptionNutritionPlanCollection
						.setAdminCreatedTime(oldsubscriptionNutritionPlanCollection.getAdminCreatedTime());
			} else {
				subscriptionNutritionPlanCollection.setCreatedTime(new Date());
				subscriptionNutritionPlanCollection.setAdminCreatedTime(new Date());
				subscriptionNutritionPlanCollection.setCreatedBy("admin");
			}
			if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlanCollection.getBackgroundImage())) {
				subscriptionNutritionPlanCollection.setBackgroundImage(
						subscriptionNutritionPlanCollection.getBackgroundImage().replace(imagePath, ""));
			}
			subscriptionNutritionPlanCollection = subscritptionNutritionPlanRepository
					.save(subscriptionNutritionPlanCollection);
			if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlanCollection.getBackgroundImage())) {
				subscriptionNutritionPlanCollection
						.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlanCollection.getBackgroundImage()));
			}
			response = new SubscriptionNutritionPlan();
			BeanUtil.map(subscriptionNutritionPlanCollection, response);
		} catch (BusinessException e) {

			logger.error("Error while addEdit Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public SubscriptionNutritionPlan deleteSubscritionPlan(String id) {
		SubscriptionNutritionPlan response = null;
		try {
			SubscriptionNutritionPlanCollection subscriptionNutritionPlanCollection = subscritptionNutritionPlanRepository
					.findById(new ObjectId(id)).orElse(null);
			subscriptionNutritionPlanCollection.setUpdatedTime(new Date());

			subscriptionNutritionPlanCollection.setDiscarded(!subscriptionNutritionPlanCollection.getDiscarded());
			subscritptionNutritionPlanRepository.save(subscriptionNutritionPlanCollection);
			if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlanCollection.getBackgroundImage())) {
				subscriptionNutritionPlanCollection
						.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlanCollection.getBackgroundImage()));
			}
			response = new SubscriptionNutritionPlan();
			BeanUtil.map(subscriptionNutritionPlanCollection, response);

		} catch (BusinessException e) {

			logger.error("Error while deleting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while deleting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public SubscriptionNutritionPlan getSubscritionPlan(String id) {
		SubscriptionNutritionPlan response = null;
		try {
			SubscriptionNutritionPlanCollection subscriptionNutritionPlanCollection = subscritptionNutritionPlanRepository
					.findById(new ObjectId(id)).orElse(null);
			response = new SubscriptionNutritionPlan();
			if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlanCollection.getBackgroundImage())) {
				subscriptionNutritionPlanCollection
						.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlanCollection.getBackgroundImage()));
			}
			BeanUtil.map(subscriptionNutritionPlanCollection, response);

		} catch (BusinessException e) {

			logger.error("Error while getting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<SubscriptionNutritionPlan> getSubscritionPlans(List<ObjectId> idList) {
		List<SubscriptionNutritionPlan> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(idList).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<SubscriptionNutritionPlan> results = mongoTemplate.aggregate(aggregation,
					SubscriptionNutritionPlanCollection.class, SubscriptionNutritionPlan.class);
			response = results.getMappedResults();
			for (SubscriptionNutritionPlan subscriptionNutritionPlan : response) {
				if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlan.getBackgroundImage())) {
					subscriptionNutritionPlan
							.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlan.getBackgroundImage()));
				}
			}

		} catch (BusinessException e) {

			logger.error("Error while getting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<SubscriptionNutritionPlan> getSubscritionPlans(int page, int size, String nutritionplanId,
			Boolean discarded) {
		List<SubscriptionNutritionPlan> response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(nutritionplanId)) {
				criteria = criteria.and("nutritionPlanId").is(new ObjectId(nutritionplanId));
			}

			criteria.and("discarded").is(discarded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<SubscriptionNutritionPlan> results = mongoTemplate.aggregate(aggregation,
					SubscriptionNutritionPlanCollection.class, SubscriptionNutritionPlan.class);
			response = results.getMappedResults();
			for (SubscriptionNutritionPlan nutritionPlan : response) {
				if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBackgroundImage())) {
					nutritionPlan.setBackgroundImage(getFinalImageURL(nutritionPlan.getBackgroundImage()));
				}
			}

		} catch (BusinessException e) {

			logger.error("Error while getting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<NutritionPlanWithCategoryResponse> getNutritionPlanByCategory(NutritionPlanRequest request) {
		List<NutritionPlanWithCategoryResponse> response = null;
		try {
			Aggregation aggregation = null;

			CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("nutritionPlan.title", "$title")
							.append("nutritionPlan.planImage", new BasicDBObject("$cond",
									new BasicDBObject("if", new BasicDBObject("eq", Arrays.asList("$planImage", null)))
											.append("then",
													new BasicDBObject("$concat",
															Arrays.asList(imagePath, "$planImage")))
											.append("else", null)))
							.append("nutritionPlan.bannerImage",
									new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("eq", Arrays.asList("$bannerImage", null)))
															.append("then",
																	new BasicDBObject("$concat",
																			Arrays.asList(imagePath, "$bannerImage")))
															.append("else", null)))
							.append("category", "$type").append("nutritionPlan.type", "$type")

							.append("nutritionPlan._id", "$_id")
							.append("nutritionPlan.backgroundColor", "$backgroundColor")
							.append("nutritionPlan.planDescription", "$planDescription")
							.append("nutritionPlan.nutrientDescriptions", "$nutrientDescriptions")
							.append("nutritionPlan.recommendedFoods", "$recommendedFoods")
							.append("nutritionPlan.amount", "$amount").append("nutritionPlan.discarded", "$discarded")
							.append("nutritionPlan.adminCreatedTime", "$adminCreatedTime")
							.append("nutritionPlan.createdTime", "$createdTime")
							.append("nutritionPlan.updatedTime", "$updatedTime")
							.append("nutritionPlan.createdBy", "$createdBy")));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id", "$category").append("category", new BasicDBObject("$first", "$category"))
							.append("nutritionPlan", new BasicDBObject("$first", "$nutritionPlan"))));
			Criteria criteria = new Criteria();
			if (request != null) {
				if (request.getTypes() != null && !request.getTypes().isEmpty()) {
					criteria = criteria.and("type").in(request.getTypes());
				}
				if (request.getUpdatedTime() > 0) {
					criteria = criteria.and("createdTime").gte(new Date(request.getUpdatedTime()));
				}

				criteria.and("discarded").is(request.getDiscarded());
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), projectOperation, groupOperation,

					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlanWithCategoryResponse> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlanWithCategoryResponse.class);
			response = results.getMappedResults();

		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public UserNutritionSubscription getUserSubscritionPlan(String id) {
		UserNutritionSubscription response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			criteria.and("id").is(new ObjectId(id));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

					Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
							"subscriptionPlan"),
					Aggregation.unwind("subscriptionPlan"),
					Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "NutritionPlan"),
					Aggregation.unwind("NutritionPlan"), Aggregation.lookup("user_cl", "userId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<UserNutritionSubscription> results = mongoTemplate.aggregate(aggregation,
					UserNutritionSubscriptionCollection.class, UserNutritionSubscription.class);
			response = results.getUniqueMappedResult();
			SubscriptionNutritionPlan subscriptionNutritionPlan = null;
			if (response != null) {
				NutritionPlan nutritionPlan = response.getNutritionPlan();
				if (nutritionPlan != null) {
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
						response.getNutritionPlan().setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
						response.getNutritionPlan().setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
					}
				}
				subscriptionNutritionPlan = response.getSubscriptionPlan();
				if (subscriptionNutritionPlan != null) {
					if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlan.getBackgroundImage())) {
						response.getSubscriptionPlan()
								.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlan.getBackgroundImage()));
					}
				}

			}
		} catch (BusinessException e) {

			logger.error("Error while getting User Nutrition Subscrition " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting User Nutrition Subscrition " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<UserNutritionSubscriptionResponse> getUserSubscritionPlans(int page, int size, long updatedTime,
			boolean discarded, boolean isExpired) {
		List<UserNutritionSubscriptionResponse> response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (updatedTime > 0) {
				criteria = criteria.and("updatedTime").gte(new Date(updatedTime));
			}
			criteria.and("isExpired").is(isExpired);
			criteria.and("discarded").is(discarded);
			CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("user", "$user").append("userId", "$userId")
							.append("userNutritionSubscription._id", "$_id")
							.append("userNutritionSubscription.userId", "$userId")
							.append("userNutritionSubscription.nutritionPlan", "$nutritionPlan")
							.append("userNutritionSubscription.subscriptionPlan", "$subscriptionPlan")
							.append("userNutritionSubscription.orderId", "$orderId")
							.append("userNutritionSubscription.transactionStatus", "$transactionStatus")
							.append("userNutritionSubscription.discount", "$discount")
							.append("userNutritionSubscription.amount", "$amount")
							.append("userNutritionSubscription.discountAmount", "$discountAmount")
							.append("userNutritionSubscription.fromDate", "$fromDate")
							.append("userNutritionSubscription.toDate", "$toDate")
							.append("userNutritionSubscription.discarded", "$discarded")
							.append("userNutritionSubscription.isExpired", "$isExpired")
							.append("userNutritionSubscription.adminCreatedTime", "$adminCreatedTime")
							.append("userNutritionSubscription.createdTime", "$createdTime")
							.append("userNutritionSubscription.updatedTime", "$updatedTime")
							.append("userNutritionSubscription.createdBy", "$createdBy")));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id", "$userId").append("user", new BasicDBObject("$first", "$user")).append(
							"userNutritionSubscription", new BasicDBObject("$push", "$userNutritionSubscription"))));

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
								"subscriptionPlan"),
						Aggregation.unwind("subscriptionPlan"),
						Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "NutritionPlan"),
						Aggregation.unwind("NutritionPlan"), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), projectOperation, groupOperation,
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
								"subscriptionPlan"),
						Aggregation.unwind("subscriptionPlan"),
						Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "NutritionPlan"),
						Aggregation.unwind("NutritionPlan"), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), projectOperation, groupOperation,
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<UserNutritionSubscriptionResponse> results = mongoTemplate.aggregate(aggregation,
					UserNutritionSubscriptionCollection.class, UserNutritionSubscriptionResponse.class);
			response = results.getMappedResults();

		} catch (

		BusinessException e) {

			logger.error("Error while getting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<NutritionAppointment> getNutritionAppointments(int page, int size, long updatedTime,
			boolean discarded) {
		List<NutritionAppointment> response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (updatedTime > 0) {
				criteria = criteria.and("updatedTime").gte(updatedTime);
			}

			criteria.and("discarded").is(discarded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<NutritionAppointment> results = mongoTemplate.aggregate(aggregation,
					NutritionAppointmentCollection.class, NutritionAppointment.class);
			response = results.getMappedResults();

		} catch (BusinessException e) {

			logger.error("Error while getting Nutrition Appointment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting  Nutrition Appointment " + e.getMessage());

		}
		return response;
	}

	@Override
	public NutritionAppointment updateNutritionAppointmentStatus(String appointmentId, AppointmentState status) {
		NutritionAppointment response = null;
		try {
			NutritionAppointmentCollection appointmentCollection = nutritionAppointmentRepository
					.findById(new ObjectId(appointmentId)).orElse(null);
			if (appointmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Nutrition Appointment Not found with Id");
			}
			appointmentCollection.setState(status);
			appointmentCollection.setUpdatedTime(new Date());
			appointmentCollection = nutritionAppointmentRepository.save(appointmentCollection);
			response = new NutritionAppointment();
			BeanUtil.map(appointmentCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while update Nutrition Appointment status " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Nutrition Appointment status" + e.getMessage());
		}
		return response;

	}
	
	@Override
	public Testimonial addEditTestimonial(AddEditTestimonialRequest request)
	{
		TestimonialCollection testimonialCollection = null;
		Testimonial response = null;
		
		try {
			if(!DPDoctorUtils.anyStringEmpty(request.getId()))
			{
				testimonialCollection = testimonialRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			else
			{
				testimonialCollection = new TestimonialCollection();
				testimonialCollection.setCreatedTime(new Date());
				testimonialCollection.setCreatedBy("ADMIN");
			}
			
			if(testimonialCollection == null)
			{
				throw new BusinessException(ServiceError.NoRecord,"Record not found");
			}
			
			BeanUtil.map(request, testimonialCollection);
			testimonialCollection.setMedia(request.getMedia());
			testimonialCollection = testimonialRepository.save(testimonialCollection);
			if(testimonialCollection != null)
			{
				response = new Testimonial();
				BeanUtil.map(testimonialCollection, response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
		
	}
	
	
	@Override
	public Boolean discardTestimonial(String id , Boolean discarded)
	{
		TestimonialCollection testimonialCollection = null;
		Boolean response = false;
		
		try {
			if(!DPDoctorUtils.anyStringEmpty(id))
			{
				testimonialCollection = testimonialRepository.findById(new ObjectId(id)).orElse(null);
			}
			
			if(testimonialCollection == null)
			{
				throw new BusinessException(ServiceError.NoRecord,"Record not found");
			}
			
			testimonialCollection.setDiscarded(discarded);
			testimonialCollection = testimonialRepository.save(testimonialCollection);
			response = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
		
	}
	
	
	@Override
	public List<Testimonial> getTestimonialsByPlanId(String planId, int size , int page) {
		List<Testimonial> response = null;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria("planId").is(new ObjectId(planId));
			criteria.and("discarded").is(false);
			
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.sort(Sort.Direction.DESC, "createdTime"),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<Testimonial> results = mongoTemplate.aggregate(aggregation,
					TestimonialCollection.class, Testimonial.class);
			response = results.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}
	
	@Override
	public List<NutritionPlanNameWithCategoryResponse> getNutritionPlanNameByCategory(NutritionPlanRequest request) {
		List<NutritionPlanNameWithCategoryResponse> response = null;
		try {
			Aggregation aggregation = null;

			CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("nutritionPlan.title", "$title")
							.append("category", "$type").append("nutritionPlan.type", "$type")
							.append("nutritionPlan._id", "$_id")
							.append("nutritionPlan.adminCreatedTime", "$adminCreatedTime")
							.append("nutritionPlan.createdTime", "$createdTime")
							.append("nutritionPlan.updatedTime", "$updatedTime")
							.append("nutritionPlan.createdBy", "$createdBy")));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id", "$category").append("category", new BasicDBObject("$first", "$category"))
							.append("nutritionPlan", new BasicDBObject("$first", "$nutritionPlan"))));
			Criteria criteria = new Criteria();
			if (request != null) {
				if (request.getTypes() != null && !request.getTypes().isEmpty()) {
					criteria = criteria.and("type").in(request.getTypes());
				}
				if (request.getUpdatedTime() > 0) {
					criteria = criteria.and("createdTime").gte(new Date(request.getUpdatedTime()));
				}

				criteria.and("discarded").is(request.getDiscarded());
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), projectOperation, groupOperation,

					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlanNameWithCategoryResponse> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlanNameWithCategoryResponse.class);
			response = results.getMappedResults();

		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}
	
	@Override
	@Transactional
	public Boolean putRemovePlanPrices(PlanPricesPutRemoveRequest request)
	{
		Boolean response = false;
		NutritionPlanCollection nutritionPlanCollection = null;
		try {
			if(request.getPlanId() != null)
			{
				nutritionPlanCollection = nutritionPlanRepository.findById(new ObjectId(request.getPlanId())).orElse(null);
			}
			if(nutritionPlanCollection == null)
			{
				throw new BusinessException(ServiceError.NoRecord, "Plan not found");
			}
			if(request.getIsRemove() == Boolean.FALSE)
			{
				if(nutritionPlanCollection.getPlanPriceDescription() != null)
				{
					nutritionPlanCollection.getPlanPriceDescription().put(request.getCountryCode(), request.getPlanPriceDescription());
				}
				else
				{
					Map<String, PlanPriceDescription> map = new HashMap<>();
					map.put(request.getCountryCode(), request.getPlanPriceDescription());
					nutritionPlanCollection.setPlanPriceDescription(map);
				}
			}
			else if(request.getIsRemove() == Boolean.TRUE)
			{
				nutritionPlanCollection.getPlanPriceDescription().remove(request.getCountryCode());
			}
			
			nutritionPlanCollection = nutritionPlanRepository.save(nutritionPlanCollection);
			
			response = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	@Override
	public NutritionPlan getNutritionPlanById(String id) {
		NutritionPlan response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").is(new ObjectId(id));

			aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlan> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlan.class);
			response = results.getUniqueMappedResult();
			if (response != null) {
				if (!DPDoctorUtils.anyStringEmpty(response.getPlanImage())) {
					response.setPlanImage(getFinalImageURL(response.getPlanImage()));
				}
				if (!DPDoctorUtils.anyStringEmpty(response.getBannerImage())) {
					response.setBannerImage(getFinalImageURL(response.getBannerImage()));
				}
			}

		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public NutritionRDA addEditNutritionRDA(NutritionRDA request) {
		NutritionRDA response = null;
		try {

			NutritionRDACollection nutritionRDACollection = null;
			
			if(!DPDoctorUtils.anyStringEmpty(request.getId()))nutritionRDACollection = nutritionRDARepository.findById(new ObjectId(request.getId())).orElse(null);
			
			if(nutritionRDACollection == null)nutritionRDACollection = nutritionRDARepository.findByCountryIdAndGenderAndType(request.getCountryId(),
					request.getGender(), (request.getType()!=null ? request.getType().getType() : null));
			
			if(nutritionRDACollection == null) {
				nutritionRDACollection = new NutritionRDACollection();
				BeanUtil.map(request, nutritionRDACollection);
				nutritionRDACollection.setUpdatedTime(new Date());
				nutritionRDACollection.setAdminCreatedTime(new Date());
				nutritionRDACollection.setCreatedTime(new Date());
				nutritionRDACollection.setCreatedBy(!DPDoctorUtils.anyStringEmpty(request.getCreatedBy()) ? request.getCreatedBy() : "ADMIN");
			}else {
				request.setId(nutritionRDACollection.getId().toString());
				request.setAdminCreatedTime(nutritionRDACollection.getAdminCreatedTime());
				request.setCreatedTime(nutritionRDACollection.getCreatedTime());
				request.setCreatedBy(nutritionRDACollection.getCreatedBy());
				nutritionRDACollection.setPregnancyCategory(null);
				nutritionRDACollection.setVitaminNutrients(null);
				nutritionRDACollection.setCarbNutrients(null);
				nutritionRDACollection.setLipidNutrients(null);
				nutritionRDACollection.setGeneralNutrients(null);
				nutritionRDACollection.setMineralNutrients(null);
				nutritionRDACollection.setOtherNutrients(null);
				nutritionRDACollection.setProteinAminoAcidNutrients(null);
				BeanUtil.map(request, nutritionRDACollection);		
				nutritionRDACollection.setUpdatedTime(new Date());
			}
			
			double fromYears = nutritionRDACollection.getFromAge().getYears() 
								+ (double)nutritionRDACollection.getFromAge().getMonths()/12
								+ (double)nutritionRDACollection.getFromAge().getDays()/365; 
			
			nutritionRDACollection.setFromAgeInYears(fromYears);
			
			double toYears = nutritionRDACollection.getToAge().getYears() 
					+ (double)nutritionRDACollection.getToAge().getMonths()/12
					+ (double)nutritionRDACollection.getToAge().getDays()/365; 

			nutritionRDACollection.setToAgeInYears(toYears);

			nutritionRDACollection = nutritionRDARepository.save(nutritionRDACollection);
			response = new NutritionRDA();
			BeanUtil.map(nutritionRDACollection, response);

		} catch (BusinessException e) {

			logger.error("Error while adding nutrition RDA " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding nutrition RDA " + e.getMessage());

		}
		return response;
	}

	@Override
	public Response<NutritionRDA> getNutritionRDA(String country, String countryId, String gender, String type, int page, int size, Boolean discarded) {
		Response<NutritionRDA> response = new Response<>();
		try {

			Criteria criteria = new Criteria("discarded").is(discarded);			
			if(!DPDoctorUtils.allStringsEmpty(country))criteria.and("country").is(country);
			if(!DPDoctorUtils.allStringsEmpty(countryId))criteria.and("countryId").is(countryId);
			if(!DPDoctorUtils.allStringsEmpty(gender))criteria.and("gender").is(gender);
			if(!DPDoctorUtils.allStringsEmpty(type))criteria.and("type").is(type);
			
			int count = (int) mongoTemplate.count(new Query(criteria), NutritionRDACollection.class);
			if(count > 0) {
				if(size > 0) {
					response.setDataList(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip(page*size), Aggregation.limit(size)), NutritionRDACollection.class, NutritionRDA.class).getMappedResults());
				}else {
					response.setDataList(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)), NutritionRDACollection.class, NutritionRDA.class).getMappedResults());
				}
			}
			response.setCount(count);
			
			
		} catch (BusinessException e) {
			logger.error("Error while getting nutrition RDA " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition RDA " + e.getMessage());
		}
		return response;
	}

	@Override
	public NutritionRDA getNutritionRDAById(String id) {
		NutritionRDA response = null;
		try {

			NutritionRDACollection nutritionRDACollection = nutritionRDARepository.findById(new ObjectId(id)).orElse(null);
			if(nutritionRDACollection != null) {
				response = new NutritionRDA();
				BeanUtil.map(nutritionRDACollection, response);
			}
		} catch (BusinessException e) {
			logger.error("Error while getting nutrition rda " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition rda " + e.getMessage());
		}
		return response;
	}

	@Override
	public NutritionRDA discardNutritionRDA(String id, Boolean discarded) {
		NutritionRDA response = null;
		try {
			NutritionRDACollection nutritionRDACollection = nutritionRDARepository.findById(new ObjectId(id)).orElse(null);
			if (nutritionRDACollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Nutrient RDA Not found with Id");
			}
			nutritionRDACollection.setDiscarded(discarded);
			nutritionRDACollection.setUpdatedTime(new Date());
			nutritionRDACollection = nutritionRDARepository.save(nutritionRDACollection);
			
			response = new NutritionRDA();
			BeanUtil.map(nutritionRDACollection, response);
			
		} catch (BusinessException e) {
			logger.error("Error while discarding nutrition rda " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discarding nutrition rda " + e.getMessage());
		}
		return response;
	}
	@Override
	public Boolean uploadNutritionRDA(FormDataBodyPart file) {
		Boolean response = false;
		try {
			String line = null;

			if (file != null) {
				InputStream fis = file.getEntityAs(InputStream.class);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
				int i = 0;
				while ((line = reader.readLine()) != null) {
					if (i != 0) {
						String[] data = line.split(",");
						
						NutritionRDACollection nutritionRDACollection = new NutritionRDACollection();
						
						//gender
						nutritionRDACollection.setGender(data[0]);
						
						//LifeStyleType
						nutritionRDACollection.setType(LifeStyleType.valueOf(data[1].toUpperCase()));
						
						//From Age
						String from = data[3];
						Age fromAge = new Age();
						fromAge.setYears(Integer.parseInt(from.substring(0, from.indexOf("y"))));
						fromAge.setMonths(Integer.parseInt(from.substring(from.indexOf("y") +1, from.indexOf("m"))));
						fromAge.setDays(Integer.parseInt(from.substring(from.indexOf("m") + 1, from.indexOf("d"))));
						nutritionRDACollection.setFromAge(fromAge);
						
						double fromYears = nutritionRDACollection.getFromAge().getYears() 
								+ (double)nutritionRDACollection.getFromAge().getMonths()/12
								+ (double)nutritionRDACollection.getFromAge().getDays()/365; 
						nutritionRDACollection.setFromAgeInYears(fromYears);
						
						//To Age
						String to = data[4];
						Age toAge = new Age();
						toAge.setYears(Integer.parseInt(to.substring(0, to.indexOf("y"))));
						toAge.setMonths(Integer.parseInt(to.substring(to.indexOf("y") +1, to.indexOf("m"))));
						toAge.setDays(Integer.parseInt(to.substring(to.indexOf("m") + 1, to.indexOf("d"))));
						nutritionRDACollection.setToAge(toAge);
						
						double toYears = nutritionRDACollection.getToAge().getYears() 
								+ (double)nutritionRDACollection.getToAge().getMonths()/12
								+ (double)nutritionRDACollection.getToAge().getDays()/365; 
						nutritionRDACollection.setToAgeInYears(toYears);
						
						//Country
						nutritionRDACollection.setCountry(data[5]);
						
						//pregnancyCategory
						if(!DPDoctorUtils.anyStringEmpty(data[6])) {
							nutritionRDACollection.setPregnancyCategory(Arrays.asList(data[6].split("+")));
						}
						
						//generalNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[7])) {
							String[] generalNutrients = data[7].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : generalNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setGeneralNutrients(map);
						}

						//generalNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[7])) {
							String[] generalNutrients = data[7].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : generalNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setGeneralNutrients(map);
						}
						
						//carbNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[8])) {
							String[] carbNutrients = data[8].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : carbNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setCarbNutrients(map);
						}
						
						//lipidNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[9])) {
							String[] lipidNutrients = data[9].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : lipidNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setLipidNutrients(map);
						}
						
						//proteinAminoAcidNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[10])) {
							String[] proteinAminoAcidNutrients = data[10].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : proteinAminoAcidNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setProteinAminoAcidNutrients(map);
						}
						
						//vitaminNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[11])) {
							String[] vitaminNutrients = data[11].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : vitaminNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setVitaminNutrients(map);
						}
						
						//mineralNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[12])) {
							String[] mineralNutrients = data[12].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : mineralNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setMineralNutrients(map);
						}
						
						//otherNutrients
						if(!DPDoctorUtils.anyStringEmpty(data[13])) {
							String[] otherNutrients = data[13].split("+");
							Map<String, String> map = new HashMap<>();
							for(String nutrients : otherNutrients) {
								map.put(nutrients.split(":")[0], nutrients.split(":")[1]);
							}
							nutritionRDACollection.setOtherNutrients(map);
						}
						
						nutritionRDACollection.setAdminCreatedTime(new Date());
						nutritionRDACollection.setCreatedBy("ADMIN");
						nutritionRDACollection.setCreatedTime(new Date());
						nutritionRDACollection.setUpdatedTime(new Date());
						
						nutritionRDACollection = nutritionRDARepository.save(nutritionRDACollection);
						response = true;
					}
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}	
}
