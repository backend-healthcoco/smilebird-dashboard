package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.dpdocter.repository.MasterBabyImmunizationRepository;

public class PaediatricServiceImpl{
	
	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	MasterBabyImmunizationRepository masterBabyImmunizationRepository;
	

	/*@Override
	@Transactional
	public MasterVaccineResponse getVaccineById(String id) {
		MasterVaccineResponse response = null;
		MasterBabyImmunizationCollection vaccineCollection = null;
		try {
			vaccineCollection = masterBabyImmunizationRepository.findById(new ObjectId(id));
			if (vaccineCollection != null) {
				response = new VaccineResponse();
				BeanUtil.map(vaccineCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}
	
	
	
	@Override
	@Transactional
	public List<MasterVaccineResponse> getMasterVaccineList(String searchTerm, Boolean isChartVaccine, int page,
			int size) {
		List<MasterVaccineResponse> responses = null;
		try {
			// Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (isChartVaccine != null) {
				criteria.and("isChartVaccine").is(isChartVaccine);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria)
						);
			}

			AggregationResults<MasterVaccineResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					MasterBabyImmunizationCollection.class, MasterVaccineResponse.class);
			responses = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return responses;
	}
	
	*/
}
