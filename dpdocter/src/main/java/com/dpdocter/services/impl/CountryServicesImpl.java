package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Country;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DrugDetailInformationCollection;
import com.dpdocter.collections.GenericCodeCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CountryRepository;
import com.dpdocter.services.CountryService;

import common.util.web.DPDoctorUtils;

@Service
public class CountryServicesImpl implements CountryService {

	private static Logger logger = LogManager.getLogger(CountryServicesImpl.class.getName());

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Country addEditCountry(Country request) {
		Country response = null;

		try {
			CountryCollection countryCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				countryCollection = countryRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (countryCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Country Not found with Id");
				}
				request.setUpdatedTime(new Date());
				countryCollection.setTaxValue(null);
				request.setCreatedBy(countryCollection.getCreatedBy());
				BeanUtil.map(request, countryCollection);

			} else {
				countryCollection = countryRepository.findByCountryCode(request.getCountryCode());
				if (countryCollection != null) {
					throw new BusinessException(ServiceError.NotFound, "Country Code already present");
				}
				countryCollection = new CountryCollection();
				BeanUtil.map(request, countryCollection);
				countryCollection.setCreatedBy("ADMIN");
				countryCollection.setUpdatedTime(new Date());
				countryCollection.setCreatedTime(new Date());
			}
			countryCollection = countryRepository.save(countryCollection);
			response = new Country();
			BeanUtil.map(countryCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Country  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Country " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<Country> getCountry(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<Country> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("countryName").regex("^" + searchTerm, "i"),
						new Criteria("countryName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, CountryCollection.class, Country.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Country " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Country " + e.getMessage());

		}
		return response;

	}

	@Override
	public Integer countCountry(Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			criteria = criteria.orOperator(new Criteria("countryName").regex("^" + searchTerm, "i"),
					new Criteria("countryName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), CountryCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Country " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while Country " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean deleteCountry(String id, Boolean isDiscarded) {
		Boolean response = null;
		try {
			CountryCollection countryCollection = countryRepository.findById(new ObjectId(id)).orElse(null);
			if (countryCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			countryCollection.setIsDiscarded(isDiscarded);
			countryCollection.setUpdatedTime(new Date());
			countryCollection = countryRepository.save(countryCollection);
			response = true;
//			BeanUtil.map(countryCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while deleting the Country  " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while deleting the Country");
		}

		return response;
	}

	@Override
	public Country getCountryById(String id) {
		Country response = null;
		try {
			CountryCollection countryCollection = countryRepository.findById(new ObjectId(id)).orElse(null);
			if (countryCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new Country();
			BeanUtil.map(countryCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;

	}
	
	@Override
	public List<GenericCodeCollection> getgenericcollection(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<GenericCodeCollection> response = null;
		try {
			Criteria criteria = new Criteria();
//			criteria = criteria.orOperator(new Criteria("code").regex("^" + searchTerm, "i"),
//					new Criteria("code").regex("^" + searchTerm));
			
			criteria = criteria.orOperator(new Criteria("code").regex("^" + searchTerm, "i"),
					new Criteria("code").regex("^" + searchTerm),
					new Criteria("name").regex("^" + searchTerm, "i"),
					new Criteria("name").regex("^" + searchTerm));
					
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, GenericCodeCollection.class, GenericCodeCollection.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Country " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Country " + e.getMessage());

		}
		return response;

	}
	
	@Override
	public List<DrugDetailInformationCollection> getDrugInfocollection(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<DrugDetailInformationCollection> response = null;
		try {
			Criteria criteria = new Criteria();
//			criteria = criteria.orOperator(new Criteria("code").regex("^" + searchTerm, "i"),
//					new Criteria("code").regex("^" + searchTerm));
			
			criteria = criteria.orOperator(new Criteria("GenericCode.name").regex("^" + searchTerm, "i"),
					new Criteria("GenericCode.name").regex("^" + searchTerm),
					new Criteria("description").regex("^" + searchTerm, "i"),
					new Criteria("description").regex("^" + searchTerm));
					
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, DrugDetailInformationCollection.class, DrugDetailInformationCollection.class).getMappedResults();
			System.out.println(aggregation);
		} catch (BusinessException e) {
			logger.error("Error while getting Country " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Country " + e.getMessage());

		}
		return response;

	}
}
