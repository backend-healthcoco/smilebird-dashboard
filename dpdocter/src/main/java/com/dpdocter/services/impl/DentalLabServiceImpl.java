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

import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.collections.DentalLabDoctorAssociationCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.LabType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DentalLabDoctorAssociationRepository;
import com.dpdocter.repository.DentalWorkRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.services.DentalLabService;

import common.util.web.DPDoctorUtils;

@Service
public class DentalLabServiceImpl implements DentalLabService {

	@Autowired
	private DentalWorkRepository dentalWorkRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private DentalLabDoctorAssociationRepository dentalLabDoctorAssociationRepository;

	private static Logger logger = LogManager.getLogger(DentalLabServiceImpl.class.getName());

	@Override
	@Transactional
	public DentalWork addEditCustomWork(AddEditCustomWorkRequest request) {
		DentalWork response = null;
		try {
			DentalWorkCollection dentalWorkCollection = new DentalWorkCollection();
			BeanUtil.map(request, dentalWorkCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getId())) {
				dentalWorkCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(dentalWorkCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						dentalWorkCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					dentalWorkCollection.setCreatedBy("ADMIN");
				}
			} else {
				DentalWorkCollection oldDentalWorkCollection = dentalWorkRepository
						.findById(dentalWorkCollection.getId()).orElse(null);
				dentalWorkCollection.setCreatedBy(oldDentalWorkCollection.getCreatedBy());
				dentalWorkCollection.setCreatedTime(oldDentalWorkCollection.getCreatedTime());
				dentalWorkCollection.setDiscarded(oldDentalWorkCollection.getDiscarded());
			}
			dentalWorkCollection = dentalWorkRepository.save(dentalWorkCollection);
			response = new DentalWork();
			BeanUtil.map(dentalWorkCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalWork> getCustomWorks(int page, int size, String searchTerm) {
		List<DentalWork> customWorks = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("workName").regex("^" + searchTerm, "i"),
						new Criteria("workName").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalWork> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorkCollection.class, DentalWork.class);
			customWorks = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting custom works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting custom works");
		}
		return customWorks;
	}

	@Override
	@Transactional
	public DentalWork deleteCustomWork(String id, boolean discarded) {
		DentalWork response = null;
		DentalWorkCollection customWorkCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				customWorkCollection = dentalWorkRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (customWorkCollection != null) {
				customWorkCollection.setDiscarded(discarded);
				customWorkCollection = dentalWorkRepository.save(customWorkCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalWork();
			BeanUtil.map(customWorkCollection, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean changeLabType(String doctorId, String locationId, LabType labType) {
		Boolean response = false;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection != null) {
				doctorClinicProfileCollection.setLabType(labType.getType());
				response = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDentalLabDoctorAssociation(List<DentalLabDoctorAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection = null;
		try {
			for (DentalLabDoctorAssociation dentalLabDoctorAssociation : request) {
				dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(new ObjectId(dentalLabDoctorAssociation.getDoctorId()), new ObjectId(dentalLabDoctorAssociation.getLocationId()),
						new ObjectId(dentalLabDoctorAssociation.getHospitalId()), new ObjectId(dentalLabDoctorAssociation.getDentalLabLocationId()), new ObjectId(dentalLabDoctorAssociation.getDentalLabHospitalId()));
				
						
				if (dentalLabDoctorAssociationCollection == null) {
					dentalLabDoctorAssociationCollection = new DentalLabDoctorAssociationCollection();
				} else {
					oldId = dentalLabDoctorAssociationCollection.getId();
				}
				BeanUtil.map(dentalLabDoctorAssociation, dentalLabDoctorAssociationCollection);
				dentalLabDoctorAssociationCollection.setId(oldId);
				dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository
						.save(dentalLabDoctorAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalLabDoctorAssociationLookupResponse> getDentalLabDoctorAssociations(String doctorId, int page,
			int size, String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria().and("doctorId").is(new ObjectId(doctorId));
			criteria.and("isActive").is(Boolean.TRUE);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalLab.locationName").regex("^" + searchTerm, "i"),
						new Criteria("dentalLab.locationName").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, DentalLabDoctorAssociationCollection.class,
					DentalLabDoctorAssociationLookupResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return response;
	}

}
