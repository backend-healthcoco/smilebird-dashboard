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

import com.dpdocter.beans.CompanyDetail;
import com.dpdocter.beans.PackageDetail;
import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.collections.PackageDetailCollection;
import com.dpdocter.collections.PackageDetailObjectCollection;
import com.dpdocter.enums.PackageType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PackageDetailObjectRepository;
import com.dpdocter.services.PackageDetailObjectService;

import common.util.web.DPDoctorUtils;

@Service
public class PackageDetailObjectServiceImpl implements PackageDetailObjectService  {

	private static Logger logger = LogManager.getLogger(PackageDetailObjectServiceImpl.class.getName());

	@Autowired
	PackageDetailObjectRepository packageDetailObjectRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public PackageDetailObject addEditPackageDetailObject(PackageDetailObject request) {
		PackageDetailObject response = null;
		try {
			PackageDetailObjectCollection  packageDetailObjectCollection = null;
			
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				packageDetailObjectCollection = packageDetailObjectRepository.findById(new ObjectId(request.getId())).orElse(null);;
				if (packageDetailObjectCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Package Not found with Id");
				}
				packageDetailObjectCollection.setPackageAmount(null);
				request.setUpdatedTime(new Date());
				request.setCreatedBy(packageDetailObjectCollection.getCreatedBy());
				BeanUtil.map(request, packageDetailObjectCollection);

			}else {
				PackageDetailObjectCollection packageDetailObjectCollectionName = packageDetailObjectRepository.findByPackageName(request.getPackageName());
				if (packageDetailObjectCollectionName != null) {
					throw new BusinessException(ServiceError.NotFound, "Packege Already Present");
				}
				packageDetailObjectCollection = new PackageDetailObjectCollection();
				BeanUtil.map(request, packageDetailObjectCollection);
				packageDetailObjectCollection.setCreatedBy("ADMIN");
				packageDetailObjectCollection.setUpdatedTime(new Date());
				packageDetailObjectCollection.setCreatedTime(new Date());
				
			}
			packageDetailObjectCollection = packageDetailObjectRepository.save(packageDetailObjectCollection);
			response = new PackageDetailObject();
			BeanUtil.map(packageDetailObjectCollection, response);
			
		}catch (BusinessException e) {
			logger.error("Error while add/edit Package detail  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Package detail " + e.getMessage());
		}
		
		return response;
	}

	@Override
	public List<PackageDetailObject> getPackages(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<PackageDetailObject> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
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
			response = mongoTemplate.aggregate(aggregation, PackageDetailObjectCollection.class, PackageDetailObject.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Package detail " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Package detail " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countPackages(Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
					new Criteria("packageName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), PackageDetailObjectCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Package detail " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while Package detail " + e.getMessage());

		}
		return response;

	}

	@Override
	public PackageDetailObject getPackageDetailByPackageName(PackageType packageName) {
		PackageDetailObject response = null;
		try {
			PackageDetailObjectCollection packageDetailObjectCollection = packageDetailObjectRepository.findByPackageName(packageName);
			if (packageDetailObjectCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such name");
			}
			response = new PackageDetailObject();

			BeanUtil.map(packageDetailObjectCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;

	}
	
	@Override
	public Boolean deletePackageDetail(String id, Boolean isDiscarded) {
		Boolean response=null;
		try {
			PackageDetailObjectCollection packageDetailObjectCollection = packageDetailObjectRepository.findById(new ObjectId(id)).orElse(null);
		if(packageDetailObjectCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		packageDetailObjectCollection.setIsDiscarded(isDiscarded);
		packageDetailObjectCollection.setUpdatedTime(new Date());
		packageDetailObjectCollection = packageDetailObjectRepository.save(packageDetailObjectCollection);
		response= true;
//		BeanUtil.map(packageDetailObjectCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the package  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the package");
		}
		
		return response;
	}


}
