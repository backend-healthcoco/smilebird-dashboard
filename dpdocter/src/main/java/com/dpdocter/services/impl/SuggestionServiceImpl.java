package com.dpdocter.services.impl;

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

import com.dpdocter.beans.Suggestion;
import com.dpdocter.collections.SuggestionCollection;
import com.dpdocter.enums.SuggestionState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.SuggestionRepository;
import com.dpdocter.services.SuggestionService;

import common.util.web.DPDoctorUtils;

@Service
public class SuggestionServiceImpl implements SuggestionService {
	private static Logger logger = LogManager.getLogger(SuggestionServiceImpl.class.getName());

	@Autowired
	private SuggestionRepository suggestionRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Suggestion> getSuggestion(int page, int size, String userId, String suggetionType, String state,
			String searchTerm) {
		List<Suggestion> response = null;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria = criteria.and("userId").is(new ObjectId(userId));
			if (!DPDoctorUtils.anyStringEmpty(suggetionType)) {
				criteria = criteria.and("suggetionType").is(suggetionType);
			}
			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria = criteria.and("state").is(state);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<Suggestion> results = mongoTemplate.aggregate(aggregation, SuggestionCollection.class,
					Suggestion.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while Getting Suggestion " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while while Getting Suggestion  " + e.getMessage());
		}

		return response;
	}

	@Override
	public Suggestion updateSuggestionState(String suggestionId, SuggestionState state) {
		Suggestion response = null;
		try {
			SuggestionCollection suggestionCollection = suggestionRepository.findById(new ObjectId(suggestionId)).orElse(null);
			if (suggestionCollection != null) {
				suggestionCollection.setState(state);
				;
				suggestionCollection = suggestionRepository.save(suggestionCollection);
				response = new Suggestion();
				BeanUtil.map(suggestionCollection, response);

			} else {
				throw new BusinessException(ServiceError.Unknown, "Invalid suggestionId");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

}
