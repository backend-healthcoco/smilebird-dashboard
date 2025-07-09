package com.dpdocter.webservices.v3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.FAQsResponse;
import com.dpdocter.collections.FAQsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.FAQsV3epository;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class FAQsV3ServiceImpl implements FAQsV3Service {
	private static Logger logger = LogManager.getLogger(AnalyticsV3ServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FAQsV3epository faqsV3epository;

	@Override
	public Boolean deleteFaqsById(String id, Boolean isDiscarded) {
		Boolean response = false;
		FAQsCollection dentalReasonsCollection = null;
		try {
			dentalReasonsCollection = faqsV3epository.findById(new ObjectId(id)).orElse(null);
			if (dentalReasonsCollection != null) {
				dentalReasonsCollection.setDiscarded(isDiscarded);
				dentalReasonsCollection.setUpdatedTime(new Date());
				faqsV3epository.save(dentalReasonsCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Response<Object> getFaqsList(int page, int size) {
		List<FAQsResponse> responseList = new ArrayList<FAQsResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			criteria.and("discarded").is(false);

			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					FAQsCollection.class, FAQsResponse.class).getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						FAQsCollection.class, FAQsResponse.class).getMappedResults();
			} else {

				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						FAQsCollection.class, FAQsResponse.class).getMappedResults();

			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public FAQsResponse addEditFaqsReasons(FAQsResponse request) {
		FAQsResponse response = null;
		FAQsCollection reasonsCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				reasonsCollection = faqsV3epository.findById(new ObjectId(request.getId())).orElse(null);
				request.setCreatedTime(reasonsCollection.getCreatedTime());
				BeanUtil.map(request, reasonsCollection);
				reasonsCollection.setUpdatedTime(new Date());
			} else {
				reasonsCollection = new FAQsCollection();
				BeanUtil.map(request, reasonsCollection);
				reasonsCollection.setUpdatedTime(new Date());
				reasonsCollection.setCreatedTime(new Date());
			}
			reasonsCollection = faqsV3epository.save(reasonsCollection);
			response = new FAQsResponse();
			BeanUtil.map(reasonsCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while add edit Reasons");
			throw new BusinessException(ServiceError.Unknown, "Error while add edit Reasons");
		}
		return response;
	}

}
