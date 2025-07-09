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

import com.dpdocter.beans.CommunicationDoctorTeamRequest;
import com.dpdocter.beans.Country;
import com.dpdocter.beans.DoctorResponseStatus;
import com.dpdocter.collections.CommunicationCollection;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CommunicationRepository;
import com.dpdocter.services.CommunicationService;

import common.util.web.DPDoctorUtils;

@Service
public class CommunicationServiceImpl implements CommunicationService {

	private static Logger logger = LogManager.getLogger(CommunicationServiceImpl.class.getName());

	@Autowired
	CommunicationRepository communicationRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public CommunicationDoctorTeamRequest addEditCommunication(CommunicationDoctorTeamRequest request) {
		CommunicationDoctorTeamRequest response = null;

		try {
			CommunicationCollection communicationCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				communicationCollection = communicationRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (communicationCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Communication Not found with Id");
				}
				request.setUpdatedTime(new Date());
				communicationCollection.setCommunication(null);
				request.setCreatedBy(communicationCollection.getCreatedBy());
				request.setCreatedTime(communicationCollection.getCreatedTime());
				BeanUtil.map(request, communicationCollection);

			} else {

				if (request.getDoctorId() != null) {
					CommunicationCollection communicationCollectionCk = communicationRepository
							.findByDoctorId(new ObjectId(request.getDoctorId()));
					if(communicationCollectionCk != null){
					throw new BusinessException(ServiceError.NotFound,
							"Communication for this doctor already present  with Id"
									+ communicationCollectionCk.getId());
					}
				}
				communicationCollection = new CommunicationCollection();
				BeanUtil.map(request, communicationCollection);
				communicationCollection.setCreatedBy("ADMIN");
				communicationCollection.setUpdatedTime(new Date());
				communicationCollection.setCreatedTime(new Date());
			}
			communicationCollection = communicationRepository.save(communicationCollection);
			response = new CommunicationDoctorTeamRequest();
			BeanUtil.map(communicationCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit  Communication " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Communication " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countDetailsList(DoctorResponseStatus status, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("doctorName").regex("^" + searchTerm, "i"),
						new Criteria("doctorName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (status != null) {
				criteria.and("status").is(status);
			} else {
				criteria.and("status").ne("CONVERTED");
			}

			response = (int) mongoTemplate.count(new Query(criteria), CommunicationCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while details " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<CommunicationDoctorTeamRequest> getDetailsLists(int size, int page, DoctorResponseStatus status,
			String searchTerm) {
		List<CommunicationDoctorTeamRequest> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("doctorName").regex("^" + searchTerm, "i"),
						new Criteria("doctorName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (status != null) {
				criteria.and("status").is(status);
			} else {
				criteria.and("status").ne("CONVERTED");
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, CommunicationCollection.class, CommunicationDoctorTeamRequest.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting details " + e.getMessage());

		}
		return response;
	}

	@Override
	public CommunicationDoctorTeamRequest getByDoctorId(String doctorId) {
		CommunicationDoctorTeamRequest response = null;
		try {
			CommunicationCollection communicationCollection = communicationRepository.findByDoctorId(new ObjectId(doctorId));
			if (communicationCollection == null) {
//				throw new BusinessException(ServiceError.NotFound, "Error no such id");
					return response;

			}
			response = new CommunicationDoctorTeamRequest();
			BeanUtil.map(communicationCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

}
