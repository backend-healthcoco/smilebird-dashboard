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

import com.dpdocter.beans.ExpenseType;
import com.dpdocter.collections.ExpenseTypeCollection;
import com.dpdocter.elasticsearch.document.ESExpenseTypeDocument;
import com.dpdocter.elasticsearch.services.ESExpenseTypeService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ExpenseTypeRepository;
import com.dpdocter.services.ExpenseTypeService;

import common.util.web.DPDoctorUtils;

@Service
public class ExpenseTypeServicesImpl implements ExpenseTypeService {
	private static Logger logger = LogManager.getLogger(ExpenseTypeServicesImpl.class);

	@Autowired
	private ExpenseTypeRepository expenseTypeRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ESExpenseTypeService esExpenseTypeService;

	@Override
	public ExpenseType addEditExpenseType(ExpenseType request) {
		ExpenseType response = null;
		try {
			ExpenseTypeCollection expenseCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				expenseCollection = expenseTypeRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (expenseCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Expense Type Not Found with Id");
				}
				request.setCreatedBy(expenseCollection.getCreatedBy());
				request.setCreatedTime(expenseCollection.getCreatedTime());
				expenseCollection = new ExpenseTypeCollection();
				BeanUtil.map(request, expenseCollection);

			} else {
				request.setCreatedBy("ADMIN");
				request.setCreatedTime(new Date());
				expenseCollection = new ExpenseTypeCollection();
				BeanUtil.map(request, expenseCollection);
			}
			expenseCollection = expenseTypeRepository.save(expenseCollection);
			ESExpenseTypeDocument esDocument = new ESExpenseTypeDocument();
			esExpenseTypeService.addEditExpenseType(esDocument);
			response = new ExpenseType();
			BeanUtil.map(expenseCollection, response);
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing Expense ", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while adding or editing Expense Type");
		}
		return response;
	}

	@Override
	public ExpenseType getExpenseType(String expenseTypeId) {
		ExpenseType response = null;
		try {

			ExpenseTypeCollection expenseCollection = expenseTypeRepository.findById(new ObjectId(expenseTypeId)).orElse(null);
			response = new ExpenseType();
			BeanUtil.map(expenseCollection, response);

		} catch (Exception e) {
			logger.error("Error occurred while getting Expense Type ", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while getting Expense Type");
		}
		return response;
	}

	@Override
	public List<ExpenseType> getExpenseType(int page, int size, String searchTerm, Boolean discarded) {
		List<ExpenseType> response = null;
		try {
			Criteria criteria = new Criteria();

			criteria.orOperator(
					new Criteria("doctorId").exists(false).and("locationId").exists(false).and("hospitalId")
							.exists(false),
					new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}

			AggregationResults<ExpenseType> results = mongoTemplate.aggregate(aggregation, ExpenseTypeCollection.class,
					ExpenseType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			logger.error("Error occurred while getting Expense ", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while getting Expense Type");
		}
		return response;
	}

	@Override
	public Boolean deleteExpenseType(String expenseId, Boolean discarded) {
		Boolean response = false;
		try {
			ExpenseTypeCollection expenseCollection = expenseTypeRepository.findById(new ObjectId(expenseId)).orElse(null);
			if (expenseCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Expense Type Not Found with Id");
			}
			expenseCollection.setUpdatedTime(new Date());
			expenseCollection.setDiscarded(discarded);
			expenseCollection = expenseTypeRepository.save(expenseCollection);
			ESExpenseTypeDocument esDocument = new ESExpenseTypeDocument();
			esExpenseTypeService.addEditExpenseType(esDocument);
			response = true;
		} catch (Exception e) {
			logger.error("Error occurred while getting Expense ", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while getting Expense Type");
		}
		return response;
	}
}
