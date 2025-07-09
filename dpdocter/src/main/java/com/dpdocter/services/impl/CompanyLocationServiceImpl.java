package com.dpdocter.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CompanyLocation;
import com.dpdocter.beans.Employee;
import com.dpdocter.collections.CompanyLocationCollection;
import com.dpdocter.collections.EmployeeCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CompanyLocationRepository;
import com.dpdocter.services.CompanyLocationService;

import common.util.web.DPDoctorUtils;


@Service
public class CompanyLocationServiceImpl implements CompanyLocationService {
	private static Logger logger = LogManager.getLogger(CompanyLocationServiceImpl.class.getName());

	@Autowired
	private CompanyLocationRepository companyLocationRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public CompanyLocation addCompanyLocation(CompanyLocation request) {
		CompanyLocation response = null;
		try {

			CompanyLocationCollection companyLocationCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				companyLocationCollection = companyLocationRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (companyLocationCollection == null) {

					throw new BusinessException(ServiceError.NotFound, "Comapany Location not found with Id");

				}
				companyLocationCollection.setUpdatedTime(new Date());
				BeanUtil.map(request, companyLocationCollection);
			}
			if (companyLocationCollection == null) {
				companyLocationCollection = new CompanyLocationCollection();
				BeanUtil.map(request, companyLocationCollection);
				companyLocationCollection.setCreatedTime(new Date());
				companyLocationCollection.setCreatedBy("ADMIN");
			}
			companyLocationCollection = companyLocationRepository.save(companyLocationCollection);
			response = new CompanyLocation();
			BeanUtil.map(companyLocationCollection, response);

		} catch (Exception e) {
			logger.error("Error while adding Comapany Location" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding Comapany Location" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<CompanyLocation> getCompanyLocationList(int page, int size, String searchTerm,
			Boolean isDiscarded,String companyId) {
		List<CompanyLocation> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(companyId)) 
				criteria.and("companyId").is(new ObjectId(companyId));
				
			if (isDiscarded != null)
				criteria.and("isDiscarded").is(isDiscarded);			

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("locationName").regex("^" + searchTerm, "i"),
						new Criteria("locationName").regex("^" + searchTerm),
						new Criteria("companyName").regex("^" + searchTerm, "i"),
						new Criteria("companyName").regex("^" + searchTerm));
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, CompanyLocationCollection.class, CompanyLocation.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting Company Location" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Company Location" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discardCompanyLocation(String id, Boolean isDiscarded) {
		Boolean response = false;
		try {
			CompanyLocationCollection categoryCollection = companyLocationRepository.findById(new ObjectId(id))
					.orElse(null);
			if (categoryCollection == null) {

				throw new BusinessException(ServiceError.NotFound, "Comapany Location not found with Id");

			}
			categoryCollection.setIsDiscarded(isDiscarded);
			companyLocationRepository.save(categoryCollection);
			response = true;
		} catch (Exception e) {
			logger.error("Error while getting Category" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Comapany Location" + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countCompanyLocation(String companyId, Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(companyId)) 
				criteria.and("companyId").is(new ObjectId(companyId));
				
			if (isDiscarded != null)
				criteria.and("isDiscarded").is(isDiscarded);			

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("locationName").regex("^" + searchTerm, "i"),
						new Criteria("locationName").regex("^" + searchTerm),
						new Criteria("companyName").regex("^" + searchTerm, "i"),
						new Criteria("companyName").regex("^" + searchTerm));
			response = (int) mongoTemplate.count(new Query(criteria), CompanyLocationCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Comapany Location " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while counting Comapany Location " + e.getMessage());

		}
		return response;
	}
	
	@Override
	public Integer getTotalEmployeeCountByLocationId(String companyId, String companylocationId) {
		Integer response = 0;
		try {

			Criteria criteria = new Criteria("companyId").is(new ObjectId(companyId)).and("isDiscarded").is(false);

			if (companylocationId != null) {
//				List<ObjectId> locationObjectIds = new ArrayList<ObjectId>();
//				for (String id : companylocationId)
//					locationObjectIds.add(new ObjectId(id));
				criteria.and("companyLocation.id").is(companylocationId);
			}
//			if(date != null) {
//				Date todate = new Date(Long.parseLong(date)) ;
//				criteria.and("date").in(todate);				
//			}
//			System.out.println("criteriaa" + criteria);

//			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//			DateTime fromDateTime = null;
//			if (!DPDoctorUtils.anyStringEmpty(date)) {
//				localCalendar.setTime(new Date(Long.parseLong(date)));
//				int currentDay = localCalendar.get(Calendar.DATE);
//				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
//				int currentYear = localCalendar.get(Calendar.YEAR);
//
//				fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
//						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
//			}
//
//			if (date != null)
//				criteria.and("date").gte(fromDateTime);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria));

			response = mongoTemplate.aggregate(aggregation, EmployeeCollection.class, Employee.class).getMappedResults()
					.size();

			System.out.println(response);
		} catch (Exception e) {

			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "exception occure while counting employee");

		}
		return response;
	}


}
