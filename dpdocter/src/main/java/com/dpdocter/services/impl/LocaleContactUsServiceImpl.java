package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.LocaleContactUs;
import com.dpdocter.collections.LocaleContactUsCollection;
import com.dpdocter.enums.LocaleContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocaleContactUsRepository;
import com.dpdocter.services.LocaleContactUsService;
import com.mongodb.DuplicateKeyException;

import common.util.web.DPDoctorUtils;

@Service
public class LocaleContactUsServiceImpl implements LocaleContactUsService {

	private static Logger logger = LogManager.getLogger(LocaleContactUsServiceImpl.class.getName());

	@Autowired
	LocaleContactUsRepository localeContactUsRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	@Transactional
	public List<LocaleContactUs> getLocaleContactList(int page, int size, String searchTerm, String contactState) {
		List<LocaleContactUs> response = null;
		// String searchTerm = null;
		Criteria criteria = new Criteria();
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("localeName").regex("^" + searchTerm, "i"),
						(new Criteria("contactNumber").regex("^" + searchTerm, "i")));
			if (!DPDoctorUtils.anyStringEmpty(contactState))
				criteria = criteria.and("contactStateType").is(LocaleContactStateType.valueOf(contactState));
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<LocaleContactUs> aggregationResults = mongoTemplate.aggregate(aggregation,
					LocaleContactUsCollection.class, LocaleContactUs.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting locales " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting locale contact List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public LocaleContactUs updateLocaleContactState(String contactId, LocaleContactStateType contactState) {
		LocaleContactUs response = null;

		if (contactId != null && !(contactId.isEmpty())) {
			try {
				LocaleContactUsCollection localeContactUsCollection = localeContactUsRepository
						.findById(new ObjectId(contactId)).orElse(null);
				if (localeContactUsCollection != null) {
					localeContactUsCollection.setContactStateType(contactState);
					// localeContactUsCollection.setContactLaterOnDate(new
					// Date(contactLaterOnDate));
					localeContactUsCollection.setUpdatedTime(new Date());
					localeContactUsCollection = localeContactUsRepository.save(localeContactUsCollection);
					if (localeContactUsCollection != null) {
						response = new LocaleContactUs();
						BeanUtil.map(localeContactUsCollection, response);
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
	public Integer countLocaleContactList(String searchTerm, String contactState) {
		Integer response = 0;
		// String searchTerm = null;
		Criteria criteria = new Criteria();
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("localeName").regex("^" + searchTerm, "i"),
						(new Criteria("contactNumber").regex("^" + searchTerm, "i")));
			if (!DPDoctorUtils.anyStringEmpty(contactState))
				criteria = criteria.and("contactState").is(contactState);
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					LocaleContactUsCollection.class, LocaleContactUsCollection.class).getMappedResults().size();
		} catch (Exception e) {
			logger.error("Error while getting Pharmacy contact List " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Pharmacy contact List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean submitContact(LocaleContactUs contactUs) {

		Boolean response = false;
		try {

			LocaleContactUsCollection localeContactUsCollection = localeContactUsRepository
					.findByContactNumber(contactUs.getContactNumber());
			if (localeContactUsCollection == null) {
				localeContactUsCollection = new LocaleContactUsCollection();
				BeanUtil.map(contactUs, localeContactUsCollection);
				localeContactUsCollection.setCreatedTime(new Date());
				localeContactUsCollection.setUpdatedTime(new Date());
				localeContactUsCollection = localeContactUsRepository.save(localeContactUsCollection);
				response = true;

			} else {
				throw new BusinessException(ServiceError.Unknown, "Already registered using these Contact Number ");
			}
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown,
					"Already contacted. our representative will contact you shortly");
		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;

	}

}
