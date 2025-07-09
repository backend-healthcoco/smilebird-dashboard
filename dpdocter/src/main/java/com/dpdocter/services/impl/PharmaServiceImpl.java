package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DrugCompany;
import com.dpdocter.beans.PharmaLicense;
import com.dpdocter.collections.DrugCompanyCollection;
import com.dpdocter.collections.PCUserCollection;
import com.dpdocter.collections.PharmaCompanyCollection;
import com.dpdocter.collections.PharmaLicenseCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PCUserRepository;
import com.dpdocter.repository.PharmaCompanyRepository;
import com.dpdocter.repository.PharmaLicenseRepository;
import com.dpdocter.request.EditPharmaCompanyRequest;
import com.dpdocter.response.PharmaCompanyResponse;
import com.dpdocter.response.PharmaLicenseResponse;
import com.dpdocter.services.PharmaService;

import common.util.web.DPDoctorUtils;

@Service
public class PharmaServiceImpl implements PharmaService{
	
	@Autowired
	private PharmaLicenseRepository pharmaLicenseRepository;
	
	@Autowired
	private PharmaCompanyRepository pharmaCompanyRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private PCUserRepository pcUserRepository;
	
	@Value(value = "${image.path}")
	private String imagePath;


	@Override
	@Transactional
	public PharmaLicenseResponse addeditPharmaLicense(PharmaLicense pharmaLicense)
	{
		PharmaLicenseResponse response = null;
		PharmaLicenseCollection pharmaLicenseCollection = null;
		try {
			if(pharmaLicense.getId() != null){
				pharmaLicenseCollection = pharmaLicenseRepository.findById(new ObjectId(pharmaLicense.getId())).orElse(null);
				BeanUtil.map(pharmaLicense, pharmaLicenseCollection);
			}
			else
			{
				pharmaLicenseCollection=new PharmaLicenseCollection();
				pharmaLicenseCollection.setCreatedTime(new Date());
				BeanUtil.map(pharmaLicense, pharmaLicenseCollection);
				pharmaLicenseCollection.setAvailable(pharmaLicense.getQuantity());
				pharmaLicenseCollection.setConsumed(0l);
			}
			
			pharmaLicenseCollection = pharmaLicenseRepository.save(pharmaLicenseCollection);
			if(pharmaLicenseCollection != null)
			{
				response = new PharmaLicenseResponse();
				BeanUtil.map(pharmaLicenseCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	@Override
	@Transactional
	public List<PharmaLicenseResponse> getLicenses(String companyId, int page, int size) {
		List<PharmaLicenseResponse> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(companyId)) {
				criteria.and("companyId").is(new ObjectId(companyId));
			}
			
			criteria.and("discarded").is(false);

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<PharmaLicenseResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PharmaLicenseCollection.class, PharmaLicenseResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			//logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public PharmaLicenseResponse discardPharmaLicense(String id , Boolean discarded)
	{
		PharmaLicenseResponse response = null;
		PharmaLicenseCollection pharmaLicenseCollection = null;
		try {
			if(id != null){
				pharmaLicenseCollection = pharmaLicenseRepository.findById(new ObjectId(id)).orElse(null);
			}
			if(pharmaLicenseCollection != null)
			{
				pharmaLicenseCollection.setDiscarded(discarded);
				pharmaLicenseCollection = pharmaLicenseRepository.save(pharmaLicenseCollection);
				response = new PharmaLicenseResponse();
				BeanUtil.map(pharmaLicenseCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public List<PharmaCompanyResponse> getPharmaCompanyList(String searchTerm ,int page, int size) {
		List<PharmaCompanyResponse> response = null;

		try {
			
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}
		
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<PharmaCompanyResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PharmaCompanyCollection.class, PharmaCompanyResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			//logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	
	
	@Override
	@Transactional
	public PharmaCompanyResponse activatePharmaCompany(String id , Boolean activated)
	{
		PharmaCompanyResponse response = null;
		PharmaCompanyCollection pharmaCompanyCollection = null;
		try {
			if(id != null){
				pharmaCompanyCollection = pharmaCompanyRepository.findById(new ObjectId(id)).orElse(null);
			}
			
			
			if(pharmaCompanyCollection != null)
			{
				pharmaCompanyCollection.setIsActivated(activated);;
				pharmaCompanyCollection = pharmaCompanyRepository.save(pharmaCompanyCollection);
				Criteria criteria = new Criteria("userName").is(pharmaCompanyCollection.getEmailAddress());
				Query query = new Query();
				query.addCriteria(criteria);
				PCUserCollection userCollection = mongoTemplate.findById(query, PCUserCollection.class);
				userCollection.setIsActive(true);
				pcUserRepository.save(userCollection);
				response = new PharmaCompanyResponse();
				BeanUtil.map(pharmaCompanyCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	@Override
	@Transactional
	public PharmaCompanyResponse verifyPharmaCompany(String id , Boolean verified)
	{
		PharmaCompanyResponse response = null;
		PharmaCompanyCollection pharmaCompanyCollection = null;
		try {
			if(id != null){
				pharmaCompanyCollection = pharmaCompanyRepository.findById(new ObjectId(id)).orElse(null);
			}
			if(pharmaCompanyCollection != null)
			{
				pharmaCompanyCollection.setIsVerified(verified);
				pharmaCompanyCollection = pharmaCompanyRepository.save(pharmaCompanyCollection);
				response = new PharmaCompanyResponse();
				BeanUtil.map(pharmaCompanyCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public PharmaCompanyResponse editPharmaCompanyDetails(EditPharmaCompanyRequest request)
	{
		PharmaCompanyResponse response = null;
		PharmaCompanyCollection pharmaCompanyCollection = null;
		try {
			if(request.getId() != null){
				pharmaCompanyCollection = pharmaCompanyRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if(pharmaCompanyCollection != null)
			{
				BeanUtil.map(request, pharmaCompanyCollection);
				pharmaCompanyCollection = pharmaCompanyRepository.save(pharmaCompanyCollection);
				response = new PharmaCompanyResponse();
				BeanUtil.map(pharmaCompanyCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public PharmaCompanyResponse getCompanyDetails(String id )
	{
		PharmaCompanyResponse response = null;
		PharmaCompanyCollection pharmaCompanyCollection = null;
		try {
			if(id != null){
				pharmaCompanyCollection = pharmaCompanyRepository.findById(new ObjectId(id)).orElse(null);
			}
			if(pharmaCompanyCollection != null)
			{
				response = new PharmaCompanyResponse();
				BeanUtil.map(pharmaCompanyCollection, response);
				if(response.getLogo() != null)
				{
					if (response.getLogo().getImageUrl() != null) {
						response.getLogo().setImageUrl(imagePath + response.getLogo().getImageUrl());
						response.getLogo().setThumbnailUrl(imagePath + response.getLogo().getThumbnailUrl());
					}
				}
			}
			else
			{
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return response;
	}
	
//	changed Ritesh
	@Override
	@Transactional
	public List<DrugCompany> getDrugCompanyList(String searchTerm,int page, int size) {
		List<DrugCompany> response = null;

		try {
			
			Aggregation aggregation = null;
			
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}
			

		
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DrugCompany> aggregationResults = mongoTemplate.aggregate(aggregation,
					DrugCompanyCollection.class, DrugCompany.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			//logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
}
