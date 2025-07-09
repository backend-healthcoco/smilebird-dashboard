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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AdminUIPermission;
import com.dpdocter.collections.AdminUIPermissionCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AdminUIPermissionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.AdminUIPermissionServices;

@Service
public class AdminUIPermissionServicesImpl implements AdminUIPermissionServices {

	private static Logger logger = LogManager.getLogger(AdminUIPermissionServicesImpl.class.getName());

	@Autowired
	private AdminUIPermissionRepository adminUIPermissionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	public AdminUIPermission addPermission(AdminUIPermission adminUIPermission) {
		AdminUIPermission response = null;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(adminUIPermission.getAdminId())).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Admin not found for adminId");
			}
			AdminUIPermissionCollection adminUIPermissionCollection = null;
			adminUIPermissionCollection = adminUIPermissionRepository.findByAdminId(new ObjectId(adminUIPermission.getAdminId()));
			if (adminUIPermissionCollection == null) {
				adminUIPermissionCollection = new AdminUIPermissionCollection();
				adminUIPermissionCollection.setCreatedTime(new Date());
			}
			adminUIPermissionCollection.setCreatedBy("ADMIN");
			adminUIPermissionCollection.setUpdatedTime(new Date());
			adminUIPermissionCollection.setAdminId(new ObjectId(adminUIPermission.getAdminId()));
			adminUIPermissionCollection.setUiPermissions(adminUIPermission.getUiPermissions());
			adminUIPermissionCollection = adminUIPermissionRepository.save(adminUIPermissionCollection);
			response = new AdminUIPermission();
			BeanUtil.map(adminUIPermissionCollection, response);

		} catch (Exception e) {
			logger.error("Error while adding admin ui permission " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding admin ui permission " + e.getMessage());
		}
		return response;

	}

	public List<AdminUIPermission> getUIPermission(int page, int size) {
		List<AdminUIPermission> response = null;
		try {
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.sort(Sort.Direction.DESC, "updatedTime"),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.sort(Sort.Direction.DESC, "updatedTime"));
			response = mongoTemplate.aggregate(aggregation, AdminUIPermissionCollection.class, AdminUIPermission.class)
					.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting admin ui permission " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting admin ui permission " + e.getMessage());
		}
		return response;

	}

	public AdminUIPermission getUIPermissionByAdminId(String adminId) {
		AdminUIPermission response = null;
		try {
			AdminUIPermissionCollection adminUIPermissionCollection = adminUIPermissionRepository
					.findByAdminId(new ObjectId(adminId));
			if (adminUIPermissionCollection != null) {
				response = new AdminUIPermission();
				BeanUtil.map(adminUIPermissionCollection, response);
			}
		} catch (Exception e) {
			logger.error("Error while getting admin ui permission  by adminId" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting admin ui permission by adminId" + e.getMessage());
		}
		return response;

	}

	public AdminUIPermission getUIPermissionById(String id) {
		AdminUIPermission response = null;
		try {
			AdminUIPermissionCollection adminUIPermissionCollection = adminUIPermissionRepository
					.findById(new ObjectId(id)).orElse(null);
			if (adminUIPermissionCollection != null) {
				response = new AdminUIPermission();
				BeanUtil.map(adminUIPermissionCollection, response);
			}

		} catch (Exception e) {
			logger.error("Error while getting admin ui permission  by id" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting admin ui permission by id" + e.getMessage());
		}
		return response;

	}

}
