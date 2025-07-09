package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.collections.ClinicContactUsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicContactUsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.ClinicContactUsService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicContactUsServiceImpl implements ClinicContactUsService {

	private static Logger logger = LogManager.getLogger(ClinicContactUsServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ClinicContactUsRepository clinicContactUsRepository;

	@Autowired
	private UserRepository userRepository;;

	@Value(value = "${doctor.welcome.message}")
	private String doctorWelcomeMessage;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Value(value = "${mail.contact.us.welcome.subject}")
	private String doctorWelcomeSubject;

	@Override
	public String submitClinicContactUSInfo(ClinicContactUs clinicContactUs) {
		String response = null;
		ClinicContactUsCollection clinicContactUsCollection = new ClinicContactUsCollection();

		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(clinicContactUs.getDoctorId())).orElse(null);
			if (userCollection != null) {
				clinicContactUsCollection = clinicContactUsRepository.save(clinicContactUsCollection);
				// String body = mailBodyGenerator.generateActivationEmailBody(
				// userCollection.getTitle() + " " +
				// userCollection.getFirstName(), null,
				// "doctorWelcomeTemplate.vm", null, null);
				// mailService.sendEmail(clinicContactUs.getEmailAddress(),
				// doctorWelcomeSubject, body, null);
				if (clinicContactUsCollection != null) {
					response = doctorWelcomeMessage;
				}
			} else
				throw new BusinessException(ServiceError.Unknown, " invalid doctorId");
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown,
					"An account already exists with this email address.Please use another email address to register.");
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while creating clinic ");
			throw new BusinessException(ServiceError.Unknown, "Error occured while creating clinic");
		}

		return response;
	}

	@Override
	public ClinicContactUs updateClinicContactState(String contactId, DoctorContactStateType contactState,
			long contactLaterOnDate) {
		ClinicContactUs response = null;
		if (contactId != null && !(contactId.isEmpty())) {
			try {
				ClinicContactUsCollection clinicContactUsCollection = clinicContactUsRepository
						.findById(new ObjectId(contactId)).orElse(null);
				if (clinicContactUsCollection != null) {
					clinicContactUsCollection.setContactState(contactState);
					if (contactLaterOnDate != 0)
						clinicContactUsCollection.setContactLaterOnDate(new Date(contactLaterOnDate));
					clinicContactUsCollection = clinicContactUsRepository.save(clinicContactUsCollection);
					if (clinicContactUsCollection != null) {
						response = new ClinicContactUs();
						BeanUtil.map(clinicContactUsCollection, response);
					}
				}
			} catch (Exception e) {
				logger.warn("Error while updating contact state :: " + e);
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,
						"Error while updating clinic contact state " + e.getMessage());
			}
		}
		return response;
	}

	@Override
	public List<ClinicContactUs> getClinicContactList(int page, int size, String searchTerm, String contactState) {
		List<ClinicContactUs> response = null;
		// String searchTerm = null;
		Criteria criteria = new Criteria();
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("locationName").regex("^" + searchTerm, "i"),
						new Criteria("city").regex("^" + searchTerm), new Criteria("city").regex("^" + searchTerm));
			if (!DPDoctorUtils.anyStringEmpty(contactState))
				criteria = criteria.and("contactState").is(contactState);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<ClinicContactUs> aggregationResults = mongoTemplate.aggregate(aggregation,
					ClinicContactUsCollection.class, ClinicContactUs.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting clinic contact  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting clinic contact List " + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countClinicContactList(String searchTerm, String contactState) {
		Integer response = 0;
		Criteria criteria = new Criteria();
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("locationName").regex("^" + searchTerm, "i"),
						new Criteria("city").regex("^" + searchTerm), new Criteria("city").regex("^" + searchTerm));
			if (!DPDoctorUtils.anyStringEmpty(contactState))
				criteria = criteria.and("contactState").is(contactState);
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					ClinicContactUsCollection.class, ClinicContactUsCollection.class).getMappedResults().size();
		} catch (Exception e) {
			logger.error("Error while count clinic contact List  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting count clinic contact List " + e.getMessage());
		}
		return response;
	}

}
