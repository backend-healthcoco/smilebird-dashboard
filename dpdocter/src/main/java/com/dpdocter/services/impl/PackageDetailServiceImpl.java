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

import com.dpdocter.beans.PackageDetail;
import com.dpdocter.collections.PackageDetailCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PackageDetailRepository;
import com.dpdocter.services.PackageDetailServices;

import common.util.web.DPDoctorUtils;

@Service
public class PackageDetailServiceImpl implements PackageDetailServices{

	private static Logger logger = LogManager.getLogger(PackageDetailServiceImpl.class.getName());

	@Autowired
	PackageDetailRepository packageDetailRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PackageDetail addEditPackageDetail(PackageDetail request) {
		PackageDetail response = null;
		try {
			PackageDetailCollection packageDetailCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				packageDetailCollection = packageDetailRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (packageDetailCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Package Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(packageDetailCollection.getCreatedBy());			

				BeanUtil.map(request, packageDetailCollection);

			} else {
				packageDetailCollection = new PackageDetailCollection();
				BeanUtil.map(request, packageDetailCollection);
				packageDetailCollection.setCreatedBy("ADMIN");
				packageDetailCollection.setUpdatedTime(new Date());
				packageDetailCollection.setCreatedTime(new Date());
			}
			packageDetailCollection = packageDetailRepository.save(packageDetailCollection);
			response = new PackageDetail();

			BeanUtil.map(packageDetailCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Package  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Package " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<PackageDetail> getPackageDetails(int size, int page, Boolean discarded, String searchTerm) {
		List<PackageDetail> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, PackageDetailCollection.class, PackageDetail.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting package name " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting package name " + e.getMessage());

		}
		return response;

	}
	
	@Override
	public Integer countPackageDetails(Boolean discarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
					new Criteria("packageName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), PackageDetailCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting package name " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while package name " + e.getMessage());

		}
		return response;
	}
	
	
	@Override
	public PackageDetail deletePackageDetail(String id, Boolean discarded) {
		PackageDetail response=null;
		try {
			PackageDetailCollection packageDetailCollection = packageDetailRepository.findById(new ObjectId(id)).orElse(null);
		if(packageDetailCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		packageDetailCollection.setDiscarded(discarded);
		packageDetailCollection.setUpdatedTime(new Date());
		packageDetailCollection = packageDetailRepository.save(packageDetailCollection);
		response=new PackageDetail();
		BeanUtil.map(packageDetailCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the package  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the package");
		}
		
		return response;
	}
}
