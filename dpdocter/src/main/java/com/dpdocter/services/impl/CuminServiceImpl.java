package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.CuminGroupCollection;
import com.dpdocter.collections.CuminUserCollection;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CuminRepository;
import com.dpdocter.repository.CuminUserRepository;
import com.dpdocter.request.CuminLinkRequest;
import com.dpdocter.request.RegistrationUserRequest;
import com.dpdocter.services.CuminService;
import com.dpdocter.services.MailService;

import common.util.web.DPDoctorUtils;

@Service
public class CuminServiceImpl implements CuminService {


	@Autowired
	private CuminRepository cuminRepository;
	
	@Autowired
	private CuminUserRepository cuminUserRepository;
	
	@Autowired
	private MailService mailService;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Boolean joinGroup() {
		Boolean response = null;
		try {
//			CuminGroupCollection CuminGroupCollection = cuminRepository.findById(new ObjectId(id)).orElse(null);
//			if (CuminGroupCollection == null) {
//				throw new BusinessException(ServiceError.NotFound, "Error no such id");
//			}
////			response = ;
//			BeanUtil.map(CuminGroupCollection, response);

		} catch (BusinessException e) {
//			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public Boolean addEditGroupLink(CuminLinkRequest request) {
		Boolean response = false;

		try {
			List<CuminGroupCollection> cuminGroupCollection = cuminRepository.findAll();

//			System.out.println(cuminGroupCollection.get(0).getLink());
			if (!DPDoctorUtils.anyStringEmpty(cuminGroupCollection.get(0).getId())) {
				BeanUtil.map(request, cuminGroupCollection.get(0));
				cuminGroupCollection.get(0).setId(cuminGroupCollection.get(0).getId());
			}
	
			 cuminRepository.save(cuminGroupCollection.get(0));
			response = true;

		} catch (BusinessException e) {
//			logger.error("Error while add/edit Boolean  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit grup link " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean userRegistration(RegistrationUserRequest request) {
		Boolean response = false;
		try {
			CuminUserCollection collection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				collection = cuminUserRepository.findById(new ObjectId(request.getId())).orElse(null);
				BeanUtil.map(request, collection);
				collection.setUpdatedTime(new Date());
			} else {
				collection = new CuminUserCollection();
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setCreatedBy(request.getName());
				collection.setUpdatedTime(new Date());
			}

			collection.setUserState(UserState.USERSTATEINCOMPLETE);
			collection.setIsProfileComplete(false);
			cuminUserRepository.save(collection);
			
			String body = "New Regitrstion with name "+collection.getName()+
					" Mobile "+collection.getMobileNumber()+" email "+collection.getEmailAddress()+" city "
					+collection.getCity();
			mailService.sendEmail("nikita.patil@healthcoco.com", "New Regitrstion on Cumin", body, null);

			response = true;
		} catch (BusinessException | MessagingException e) {
			throw new BusinessException(ServiceError.Unknown, "Error While creating user " + e.getMessage());
		}
		return response;
	}
	
	@Override
	public List<RegistrationUserRequest> getCuminUserList(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<RegistrationUserRequest> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
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
			response = mongoTemplate.aggregate(aggregation, CuminUserCollection.class, RegistrationUserRequest.class).getMappedResults();

		} catch (BusinessException e) {
//			logger.error("Error while getting Country " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting cumin user list " + e.getMessage());

		}
		return response;

	}

	@Override
	public Boolean updateDoctorToCuminExpert(String locationId, String doctorId, Boolean isCuminExpert) {
		// TODO Auto-generated method stub
		return null;
	}
}
