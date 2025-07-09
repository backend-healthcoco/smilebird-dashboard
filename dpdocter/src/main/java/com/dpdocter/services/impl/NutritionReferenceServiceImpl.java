package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionReference;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionGoalStatusStampingCollection;
import com.dpdocter.collections.NutritionReferenceCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.RegularityStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionGoalStatusStampingRepository;
import com.dpdocter.repository.NutritionPlanRepository;
import com.dpdocter.repository.NutritionReferenceRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.NutritionReferenceService;
import com.dpdocter.services.NutritionService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionReferenceServiceImpl implements NutritionReferenceService {

	@Autowired
	private NutritionReferenceRepository nutritionReferenceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private MongoOperations mongoOperations;

	@Autowired
	private NutritionGoalStatusStampingRepository nutritionGoalStatusStampingRepository;

	@Autowired
	private NutritionService nutritionService;

	@Autowired
	private NutritionPlanRepository nutritionPlanRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	@Autowired
	private MailService mailService;

	@Value(value = "${image.path}")
	private String imagePath;

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Override
	@Transactional
	public List<NutritionReference> getNutritionReferenceList(String doctorId, String locationId, int page, int size,
			String searchTerm, String status, Long fromDate, Long toDate) {
		List<NutritionReference> nutritionReferenceResponses = null;

		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {

				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {

				criteria.and("locationId").is(new ObjectId(locationId));

			}
			criteria.and("goalStatus").is(status);

			if (fromDate > 0 && toDate > 0) {

				criteria.and("createdTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else if (toDate > 0) {
				criteria.and("createdTime").lte(new Date(toDate));
			} else if (fromDate > 0) {
				criteria.and("createdTime").gte(new Date(fromDate));
			}
			
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("localPatientName").regex("^" + searchTerm, "i"),
						new Criteria("localPatientName").regex("^" + searchTerm));
			if (size > 0) {
				nutritionReferenceResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "nutritionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$nutritionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
										"subscriptionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$subscriptionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip(page * size), Aggregation.limit(size)),
						NutritionReferenceCollection.class, NutritionReference.class).getMappedResults();
			} else {
				nutritionReferenceResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "nutritionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$nutritionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
										"subscriptionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$subscriptionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						NutritionReferenceCollection.class, NutritionReference.class).getMappedResults();
			}
			for (NutritionReference nutritionReferenceResponse : nutritionReferenceResponses) {

				if (nutritionReferenceResponse.getNutritionPlan() != null) {
					if (!DPDoctorUtils.anyStringEmpty(nutritionReferenceResponse.getNutritionPlan().getPlanImage())) {
						nutritionReferenceResponse.getNutritionPlan().setPlanImage(
								getFinalImageURL(nutritionReferenceResponse.getNutritionPlan().getPlanImage()));
					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionReferenceResponse.getNutritionPlan().getBannerImage())) {
						nutritionReferenceResponse.getNutritionPlan().setBannerImage(
								getFinalImageURL(nutritionReferenceResponse.getNutritionPlan().getBannerImage()));
					}
				}
				if (nutritionReferenceResponse.getSubscriptionPlan() != null) {
					nutritionReferenceResponse.getSubscriptionPlan().setBackgroundImage(
							getFinalImageURL(nutritionReferenceResponse.getSubscriptionPlan().getBackgroundImage()));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition refference " + e.getMessage());
		}
		return nutritionReferenceResponses;
	}

	@Override
	@Transactional
	public NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, Long fromDate, Long toDate) {
		NutritionGoalAnalytics nutritionGoalAnalytics = null;
		try {
			nutritionGoalAnalytics = new NutritionGoalAnalytics();
			nutritionGoalAnalytics.setReferredCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.REFERRED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setAcceptedCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.ADOPTED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setOnHoldCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.ON_HOLD.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setRejectedCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.REJECTED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setCompletedCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.COMPLETED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setMetGoalCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.MET_GOALS.getType(), fromDate, toDate));
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition goal status " + e.getMessage());
		}
		return nutritionGoalAnalytics;
	}

	@Override
	public Boolean changeStatus(String id, String regularityStatus, String goalStatus) {
		Boolean response = false;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				nutritionReferenceCollection = nutritionReferenceRepository.findById(new ObjectId(id)).orElse(null);
				if (nutritionReferenceCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(regularityStatus)) {
						nutritionReferenceCollection.setRegularityStatus(RegularityStatus.valueOf(regularityStatus));
					}
					if (!DPDoctorUtils.anyStringEmpty(goalStatus)) {
						nutritionReferenceCollection.setGoalStatus(GoalStatus.valueOf(goalStatus));
						NutritionGoalStatusStampingCollection nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
								.findByPatientIdAndDoctorIdAndLocationIdAndHospitalIdAndGoalStatus(
										nutritionReferenceCollection.getPatientId(),
										nutritionReferenceCollection.getDoctorId(),
										nutritionReferenceCollection.getLocationId(),
										nutritionReferenceCollection.getHospitalId(), goalStatus);

						if (nutritionGoalStatusStampingCollection != null) {
							nutritionGoalStatusStampingCollection.setUpdatedTime(new Date());
							nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
									.save(nutritionGoalStatusStampingCollection);
						} else {
							nutritionGoalStatusStampingCollection = new NutritionGoalStatusStampingCollection();
							nutritionGoalStatusStampingCollection
									.setDoctorId(nutritionReferenceCollection.getDoctorId());
							nutritionGoalStatusStampingCollection
									.setLocationId(nutritionReferenceCollection.getLocationId());
							nutritionGoalStatusStampingCollection
									.setHospitalId(nutritionReferenceCollection.getHospitalId());
							nutritionGoalStatusStampingCollection
									.setPatientId(nutritionReferenceCollection.getPatientId());
							nutritionGoalStatusStampingCollection.setGoalStatus(GoalStatus.valueOf(goalStatus));
							nutritionGoalStatusStampingCollection.setCreatedTime(new Date());
							nutritionGoalStatusStampingCollection.setUpdatedTime(new Date());
							UserCollection userCollection = userRepository
									.findById(nutritionReferenceCollection.getDoctorId()).orElse(null);
							nutritionGoalStatusStampingCollection.setCreatedBy(userCollection.getCreatedBy());
							nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
									.save(nutritionGoalStatusStampingCollection);
						}
					}
					response = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while Change nutrition goal status " + e.getMessage());
		}
		return response;
	}

	@Override
	public NutritionReferenceResponse getNutritionReferenceById(String id) {
		NutritionReferenceResponse response = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(id))),
								Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "nutritionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$nutritionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
										"subscriptionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$subscriptionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1")))),
						NutritionReferenceCollection.class, NutritionReferenceResponse.class).getUniqueMappedResult();
				if (response != null) {
					LocationCollection locationCollection = locationRepository
							.findById(new ObjectId(response.getHospitalId())).orElse(null);
					if (locationCollection != null) {
						response.setLocationName(locationCollection.getLocationName());
					}
					UserCollection userCollection = userRepository.findById(new ObjectId(response.getDoctorId())).orElse(null);
					if (userCollection != null) {
						response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());
					}
					PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
							new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()),
							new ObjectId(response.getHospitalId()));
					if (patientCollection != null) {
						UserCollection patient = userRepository.findById(patientCollection.getUserId()).orElse(null);
						PatientShortCard patientCard = new PatientShortCard();
						BeanUtil.map(patient, patientCard);
						BeanUtil.map(patientCollection, patientCard);
						response.setPatient(patientCard);
					}

					if (response.getNutritionPlan() != null) {
						if (!DPDoctorUtils.anyStringEmpty(response.getNutritionPlan().getPlanImage())) {
							response.getNutritionPlan()
									.setPlanImage(getFinalImageURL(response.getNutritionPlan().getPlanImage()));
						}
						if (!DPDoctorUtils.anyStringEmpty(response.getNutritionPlan().getBannerImage())) {
							response.getNutritionPlan()
									.setBannerImage(getFinalImageURL(response.getNutritionPlan().getBannerImage()));
						}
					}
					if (response.getSubscriptionPlan() != null) {
						response.getSubscriptionPlan().setBackgroundImage(
								getFinalImageURL(response.getSubscriptionPlan().getBackgroundImage()));
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition reference " + e.getMessage());
		}
		return response;
	}

	private Long getGoalStatusCount(String doctorId, String locationId, String status, Long fromDate, Long toDate) {

		Long count = 0l;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {

				criteria.and("doctorId").is(new ObjectId(doctorId));

			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {

				criteria.and("locationId").is(new ObjectId(locationId));

			}

			criteria.and("goalStatus").is(status);

			if (fromDate > 0 && toDate > 0) {

				criteria.and("updatedTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else if (toDate > 0) {
				criteria.and("updatedTime").lte(new Date(toDate));
			} else if (fromDate > 0) {
				criteria.and("updatedTime").gte(new Date(fromDate));
			}
			Query query = new Query();
			query.addCriteria(criteria);
			count = mongoOperations.count(query, NutritionGoalStatusStampingCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition goal status " + e.getMessage());
		}
		return count;
	}

}
