package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorLabReference;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.collections.DoctorLabDoctorReferenceCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.repository.DoctorLabDoctorReferenceRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.request.DoctorLabReferenceRequest;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class DoctorContactUSServiceImpl implements DoctorContactUsService {

	private static Logger logger = LogManager.getLogger(DoctorContactUSServiceImpl.class.getName());

	@Autowired
	DoctorContactUsRepository doctorContactUsRepository;

	@Autowired
	private DoctorLabDoctorReferenceRepository doctorLabDoctorReferenceRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${doctor.welcome.message}")
	private String doctorWelcomeMessage;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	
	@Autowired
	private TokenRepository tokenRepository;

	@Value(value = "${mail.contact.us.welcome.subject}")
	private String doctorWelcomeSubject;

	@Override
	@Transactional
	public String submitDoctorContactUSInfo(DoctorContactUs doctorContactUs) {
		String response = null;
		DoctorContactUsCollection doctorContactUsCollection = new DoctorContactUsCollection();
		if (doctorContactUs != null) {
			BeanUtil.map(doctorContactUs, doctorContactUsCollection);
			try {
				doctorContactUsCollection.setCreatedTime(new Date());
				doctorContactUsCollection.setUpdatedTime(new Date());
				doctorContactUsCollection.setUserName(doctorContactUs.getEmailAddress());
				doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);

				String body = mailBodyGenerator.generateActivationEmailBody(
						doctorContactUs.getTitle() + " " + doctorContactUs.getFirstName(), null,
						"doctorWelcomeTemplate.vm", null, null);
				mailService.sendEmail(doctorContactUs.getEmailAddress(), doctorWelcomeSubject, body, null);

				if (doctorContactUsCollection != null) {
					response = doctorWelcomeMessage;
				}
			} catch (DuplicateKeyException de) {
				logger.error(de);
				throw new BusinessException(ServiceError.Unknown,
						"An account already exists with this email address.Please use another email address to register.");
			} catch (BusinessException be) {
				logger.error(be);
				throw be;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error occured while creating doctor");
				throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
			}
		}
		return response;
	}

	@Override
	@Transactional
	public String resendDoctorWelcome(String emailAddress) {
		String response = null;
		DoctorContactUsCollection doctorContactUsCollection = null;

		try {

			if (!DPDoctorUtils.anyStringEmpty(emailAddress)) {
				doctorContactUsCollection = doctorContactUsRepository.findByUserNameRegexAndEmailAddressRegex(emailAddress);
			}

			if (doctorContactUsCollection != null) {

				TokenCollection tokenCollection = new TokenCollection();
				tokenCollection.setResourceId(doctorContactUsCollection.getId());
				tokenCollection.setCreatedTime(new Date());
				tokenCollection = tokenRepository.save(tokenCollection);

				String body = mailBodyGenerator.doctorWelcomeEmailBody(
						doctorContactUsCollection.getTitle() + " " + doctorContactUsCollection.getFirstName(),
						tokenCollection.getId(), "doctorWelcomeTemplate.vm", null, null);
				mailService.sendEmail(doctorContactUsCollection.getEmailAddress(), doctorWelcomeSubject, body, null);

				if (doctorContactUsCollection != null) {
					response = doctorWelcomeMessage;
				}
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while sending welcome mail");
			throw new BusinessException(ServiceError.Unknown, "Error occured while sending welcome mail");
		}

		return response;
	}
	
	@Override
	@Transactional
	public List<DoctorContactUs> getDoctorContactList(int page, int size, String searchTerm, String contactState) {
		List<DoctorContactUs> response = null;
		// String searchTerm = null;
		Criteria criteria = new Criteria();
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						(new Criteria("emailAddress").regex("^" + searchTerm, "i")));
			if (!DPDoctorUtils.anyStringEmpty(contactState))
				criteria = criteria.and("contactState").is(contactState.toUpperCase());
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<DoctorContactUs> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorContactUsCollection.class, DoctorContactUs.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting doctor contact List " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting doctor contact List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactUs updateDoctorContactState(String contactId, DoctorContactStateType contactState,
			long contactLaterOnDate) {
		DoctorContactUs response = null;

		if (contactId != null && !(contactId.isEmpty())) {
			try {
				DoctorContactUsCollection doctorContactUsCollection = doctorContactUsRepository
						.findById(new ObjectId(contactId)).orElse(null);
				if (doctorContactUsCollection != null) {
					doctorContactUsCollection.setContactState(contactState);
					if (contactLaterOnDate != 0)
						doctorContactUsCollection.setContactLaterOnDate(new Date(contactLaterOnDate));
					doctorContactUsCollection.setUpdatedTime(new Date());
					doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);
					if (doctorContactUsCollection != null) {
						response = new DoctorContactUs();
						BeanUtil.map(doctorContactUsCollection, response);
					}
				}
			} catch (Exception e) {
				logger.warn("Error while updating contact state :: " + e);
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,
						"Error while updating doctor contact state " + e.getMessage());
			}
		}
		return response;
	}

	@Override
	@Transactional
	public Integer countDoctorContactList(String searchTerm, String contactState) {
		Integer response = 0;
		// String searchTerm = null;
		Criteria criteria = new Criteria();
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						(new Criteria("emailAddress").regex("^" + searchTerm, "i")));

			if (!DPDoctorUtils.anyStringEmpty(contactState))
				criteria = criteria.and("contactState").is(contactState);
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					DoctorContactUsCollection.class, DoctorContactUsCollection.class).getMappedResults().size();
		} catch (Exception e) {
			logger.error("Error while getting doctor contact List " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting doctor contact List " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<DoctorLabReference> getdoctorReferences(int size, int page, String searchTerm, Boolean isContacted) {
		List<DoctorLabReference> doctorLabReferences = null;
		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("mobileNumber").regex(searchTerm, "i"),
						new Criteria("firstName").regex(searchTerm, "i"),
						new Criteria("locationName").regex(searchTerm, "i"),
						new Criteria("doctor.firstName").regex(searchTerm, "i"),
						new Criteria("location.locationName").regex(searchTerm, "i"));

			}

			if (isContacted) {
				criteria.and("isContacted").is(isContacted);
			} else {
				criteria.and("isContacted").is(isContacted);
			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$hospital").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
								Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$hospital").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}

			doctorLabReferences = mongoTemplate
					.aggregate(aggregation, DoctorLabDoctorReferenceCollection.class, DoctorLabReference.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error occured while getting Doctor Referance");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Doctor Referance");
		}
		return doctorLabReferences;
	}

	@Override
	public DoctorLabReference getdoctorReference(String referenceId) {
		DoctorLabReference doctorLabReference = null;
		try {

			Criteria criteria = new Criteria("id").is(new ObjectId(referenceId));
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$hospital").append("preserveNullAndEmptyArrays", true))));

			doctorLabReference = mongoTemplate
					.aggregate(aggregation, DoctorLabDoctorReferenceCollection.class, DoctorLabReference.class)
					.getUniqueMappedResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error occured while getting Doctor Referance by id");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Doctor Referance by id");
		}
		return doctorLabReference;
	}

	@Override
	public Boolean updateDoctorLabRefencence(DoctorLabReferenceRequest request) {
		Boolean response = false;
		try {
			DoctorLabDoctorReferenceCollection doctorLabDoctorReferenceCollection = doctorLabDoctorReferenceRepository
					.findById(new ObjectId(request.getId())).orElse(null);
			doctorLabDoctorReferenceCollection.setUpdatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(request.getNote())) {
				doctorLabDoctorReferenceCollection.setNote(request.getNote());
			}
			if (request.getIsContacted() != null) {
				doctorLabDoctorReferenceCollection.setIsContacted(request.getIsContacted());
			}
			doctorLabDoctorReferenceCollection = doctorLabDoctorReferenceRepository
					.save(doctorLabDoctorReferenceCollection);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error occured while updating Doctor Referance");
			throw new BusinessException(ServiceError.Unknown, "Error occured while updating Doctor Referance");
		}
		return response;
	}

}
