package com.dpdocter.services.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorAppFeedback;
import com.dpdocter.collections.DoctorAppFeedbackCollection;
import com.dpdocter.collections.PatientFeedbackCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorAppFeedbackRepository;
import com.dpdocter.repository.PatientFeedbackRepository;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.services.FeedbackService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	private Logger logger = LogManager.getLogger(FeedbackServiceImpl.class);

	@Autowired
	DoctorAppFeedbackRepository doctorAppFeedbackRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	private PatientFeedbackRepository patientFeedbackRepository;

	@Override
	@Transactional
	public DoctorAppFeedback saveDoctorAppFeedback(DoctorAppFeedback feedback) {
		DoctorAppFeedback response = null;
		try {

			if (feedback != null) {
				if (feedback.getId() != null) {
					throw new BusinessException(ServiceError.Forbidden, "Editing feedback not allowed");
				}
			}
			DoctorAppFeedbackCollection doctorAppFeedbackCollection = new DoctorAppFeedbackCollection();
			BeanUtil.map(feedback, doctorAppFeedbackCollection);
			doctorAppFeedbackCollection = doctorAppFeedbackRepository.save(doctorAppFeedbackCollection);
			if (doctorAppFeedbackCollection != null) {
				response = new DoctorAppFeedback();
				BeanUtil.map(doctorAppFeedbackCollection, response);
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DoctorAppFeedback> getList(int size, int page) {
		List<DoctorAppFeedback> response = null;
		Aggregation aggregation = null;

		try {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation
						.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<DoctorAppFeedback> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorAppFeedbackCollection.class, DoctorAppFeedback.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientFeedbackResponse> getPatientFeedbackList(int size, int page, String patientId, String doctorId,
			String localeId, String locationId, String hospitalId, String type, List<String> services,
			Boolean discarded, Boolean isApproved) {
		List<PatientFeedbackResponse> feedbackResponses = null;
		Aggregation aggregation = null;

		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("feedbackType").is(type);
			}
			if (!DPDoctorUtils.anyStringEmpty(localeId)) {
				criteria.and("localeId").is(new ObjectId(localeId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (services != null && !services.isEmpty()) {
				criteria.and("services.service").in(services);
			}
			if (discarded != null) {
				criteria.and("isDiscarded").is(discarded);
			}
			if (isApproved != null) {
				criteria.and("isApproved").is(isApproved);
			}
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("locationName", new BasicDBObject("$first", "$location.locationName"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("patientName", new BasicDBObject("$first", "$patient.firstName"))
							.append("localeId", new BasicDBObject("$first", "$localeId"))
							.append("localeName", new BasicDBObject("$first", "$locale.localeName"))
							.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
							.append("prescriptionId", new BasicDBObject("$first", "$prescriptionId"))
							.append("isRecommended", new BasicDBObject("$first", "$isRecommended"))
							.append("isAppointmentStartedOnTime",
									new BasicDBObject("$first", "$isAppointmentStartedOnTime"))
							.append("howLateWasAppointmentInMinutes",
									new BasicDBObject("$first", "$howLateWasAppointmentInMinutes"))
							.append("overallExperience", new BasicDBObject("$first", "$overallExperience"))
							.append("isDiscarded", new BasicDBObject("$first", "$isDiscarded"))
							.append("isMedicationOnTime", new BasicDBObject("$first", "$isMedicationOnTime"))
							.append("questionAnswers", new BasicDBObject("$push", "$questionAnswers"))
							.append("medicationEffectType", new BasicDBObject("$first", "$medicationEffectType"))
							.append("reply", new BasicDBObject("$first", "$reply"))
							.append("experience", new BasicDBObject("$first", "$experience"))
							.append("feedbackType", new BasicDBObject("$first", "$feedbackType"))
							.append("services", new BasicDBObject("$push", "$services.service"))
							.append("appointmentTiming", new BasicDBObject("$first", "$appointmentTiming"))
							.append("printPdfProvided", new BasicDBObject("$first", "$printPdfProvided"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("services_cl", "services", "_id", "services"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$location")
												.append("preserveNullAndEmptyArrays", true))),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$doctor")
												.append("preserveNullAndEmptyArrays", true))),
								Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$patient")
												.append("preserveNullAndEmptyArrays", true))),
								Aggregation.lookup("locale_cl", "localeId", "_id", "locale"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays",
												true))),
								aggregationOperation, Aggregation.skip(page * size), Aggregation.limit(size),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			else {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("services_cl", "services", "_id", "services"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$location")
												.append("preserveNullAndEmptyArrays", true))),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$doctor")
												.append("preserveNullAndEmptyArrays", true))),
								Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$patient")
												.append("preserveNullAndEmptyArrays", true))),
								Aggregation.lookup("locale_cl", "localeId", "_id", "locale"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays",
												true))),
								aggregationOperation, Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			feedbackResponses = mongoTemplate
					.aggregate(aggregation, PatientFeedbackCollection.class, PatientFeedbackResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return feedbackResponses;
	}

	@Override
	@Transactional
	public Integer countPatientFeedbackList(String patientId, String doctorId, String localeId, String locationId,
			String hospitalId, String type, List<String> services, Boolean discarded, Boolean isApproved) {
		Integer feedbackResponses = null;
		Aggregation aggregation = null;

		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("feedbackType").is(type);
			}
			if (!DPDoctorUtils.anyStringEmpty(localeId)) {
				criteria.and("localeId").is(new ObjectId(localeId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (services != null && !services.isEmpty()) {
				criteria.and("services.service").in(services);
			}
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (isApproved != null) {
				criteria.and("isApproved").is(isApproved);
			}
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(
					new Document("$group", new BasicDBObject("_id", "$_id")));

			aggregation = Aggregation.newAggregation(
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("services_cl", "services", "_id", "services"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(criteria), aggregationOperation);

			feedbackResponses = mongoTemplate
					.aggregate(aggregation, PatientFeedbackCollection.class, PatientFeedbackResponse.class)
					.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return feedbackResponses;
	}

	@Override
	@Transactional
	public Boolean approvePatientFeedback(String id, Boolean isApproved) {

		Boolean response = false;
		try {
			PatientFeedbackCollection collection = patientFeedbackRepository.findById(new ObjectId(id)).orElse(null);
			if (collection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Feedback not found");
			}
			collection.setIsApproved(isApproved);
			patientFeedbackRepository.save(collection);
			response=true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}
