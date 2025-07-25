package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SubscriptionDetailCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.services.SubscriptionDetailServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class SubscriptionDetailServicesImpl implements SubscriptionDetailServices {
	private static Logger logger = LogManager.getLogger(SubscriptionDetailServicesImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SubscriptionDetailRepository subscriptionDetailRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private TransactionalManagementService transnationalService;

	public SubscriptionDetail activate(SubscriptionDetail detail) {
		SubscriptionDetail response = null;
		try {
			SubscriptionDetailCollection detailCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(detail.getId())) {
				detailCollection = subscriptionDetailRepository.findById(new ObjectId(detail.getId())).orElse(null);
			}
			if (detailCollection != null) {
				if (detail.getMonthsforSuscrption() > 0 && !detailCollection.getIsExpired()) {
					detail.setToDate(
							DPDoctorUtils.addmonth(detailCollection.getToDate(), detail.getMonthsforSuscrption()));
				}
				if (detail.getMonthsforSuscrption() > 0 && detailCollection.getIsExpired()) {
					detail.setToDate(DPDoctorUtils.addmonth(new Date(), detail.getMonthsforSuscrption()));
				}
				if (detail.getMonthsforSms() > 0) {
					detail.setSmsToDate(DPDoctorUtils.addmonth(new Date(), detail.getMonthsforSms()));

				}
				if (detail.getIsDemo().equals(detailCollection.getIsDemo())) {
					detail.setNoOfsms(detailCollection.getNoOfsms() + detail.getNoOfsms());
				}
				BeanUtil.map(detail, detailCollection);
				detailCollection.setIsExpired(false);
			} else {
				if (detail.getMonthsforSuscrption() > 0) {
					detail.setFromDate(new Date());
					detail.setToDate(DPDoctorUtils.addmonth(detail.getFromDate(), detail.getMonthsforSuscrption()));
				}
				if (detail.getMonthsforSms() > 0) {
					detail.setSmsFromDate(new Date());
					detail.setSmsToDate(DPDoctorUtils.addmonth(detail.getSmsFromDate(), detail.getMonthsforSms()));
				}
				detailCollection = new SubscriptionDetailCollection();
				BeanUtil.map(detail, detailCollection);
				detailCollection.setCreatedTime(new Date());
				detailCollection.setIsExpired(false);
			}
			detailCollection = subscriptionDetailRepository.save(detailCollection);
			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByLocationIdIn(detailCollection.getLocationIds());
			List<LocationCollection> locationCollections = locationRepository
					.findByIdIn(detailCollection.getLocationIds());
			for (LocationCollection locationCollection : locationCollections) {
				locationCollection.setIsActivate(true);
			}
			locationRepository.saveAll(locationCollections);
			for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {

				doctorClinicProfileCollection.setIsActivate(true);
				doctorClinicProfileCollection = doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				transnationalService.checkDoctor(doctorClinicProfileCollection.getDoctorId(),
						doctorClinicProfileCollection.getLocationId());
			}
			response = new SubscriptionDetail();
			BeanUtil.map(detailCollection, response);
		} catch (Exception e) {
			logger.error("Error while activating subscription " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while activating  subscription " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deactivate(String subscriptionDetailId) {
		Boolean response = false;
		try {
			SubscriptionDetailCollection subscriptionDetailCollection = subscriptionDetailRepository
					.findById(new ObjectId(subscriptionDetailId)).orElse(null);
			if (subscriptionDetailCollection.getId() != null) {
				subscriptionDetailCollection.setIsExpired(true);
				subscriptionDetailCollection = subscriptionDetailRepository.save(subscriptionDetailCollection);
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationIdIn(subscriptionDetailCollection.getLocationIds());
				List<LocationCollection> locationCollections = locationRepository
						.findByIdIn(subscriptionDetailCollection.getLocationIds());
				for (LocationCollection locationCollection : locationCollections) {
					locationCollection.setIsActivate(false);
				}
				locationRepository.saveAll(locationCollections);
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					doctorClinicProfileCollection.setIsActivate(false);
					doctorClinicProfileCollection = doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					transnationalService.checkDoctor(doctorClinicProfileCollection.getDoctorId(),
							doctorClinicProfileCollection.getLocationId());
				}
				response = true;
			} else
				throw new BusinessException(ServiceError.Unknown, "suscription not present");
		} catch (Exception e) {
			logger.error("Error while deactivating Suscription " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while deactivating Suscription " + e.getMessage());
		}
		return response;

	}

	@Override
	public List<SubscriptionDetail> getSubscriptionDetails(int size, int page, String doctorId, boolean isDemo,
			boolean isExpired) {
		List<SubscriptionDetail> response = null;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria();
			if (isDemo)
				criteria = new Criteria("isDemo").is(isDemo);
			if (isExpired)
				criteria = new Criteria("isExpired").is(isExpired);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<SubscriptionDetail> groupResults = mongoTemplate.aggregate(aggregation,
					SubscriptionDetailCollection.class, SubscriptionDetail.class);
			response = groupResults.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting Suscription Detail " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Suscription  Detail " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<SubscriptionDetail> addsubscriptionData() {
		List<SubscriptionDetail> response = null;
		try {
			RoleCollection superAdminRole = null;
			RoleCollection roleCollection = null;
			UserRoleCollection superAdminRoleCollection = null;
			SubscriptionDetailCollection subscriptionDetailCollection = null;
			Set<ObjectId> locationIdSet = null;
			SubscriptionDetail subscriptionDetail = null;
			Criteria criteria = new Criteria("roles.role").is(RoleEnum.SUPER_ADMIN.toString());

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("role_cl", "roleId", "_id", "roles"), Aggregation.match(criteria));
			AggregationResults<UserRoleCollection> groupResults = mongoTemplate.aggregate(aggregation,
					UserRoleCollection.class, UserRoleCollection.class);
			List<UserRoleCollection> userRoleList = groupResults.getMappedResults();
			response = new ArrayList<SubscriptionDetail>();
			roleCollection = roleRepository.findByRole(RoleEnum.SUPER_ADMIN.toString());
			if (roleCollection == null) {
				roleCollection = new RoleCollection();
				roleCollection.setCreatedTime(new Date());
				roleCollection.setRole(RoleEnum.SUPER_ADMIN.toString());
				roleCollection = roleRepository.save(roleCollection);
			}
			for (UserRoleCollection userRoleCollection : userRoleList) {
				// create SuperAdmin role
				roleCollection = roleRepository.findById(userRoleCollection.getRoleId()).orElse(null);
				superAdminRole = new RoleCollection();
				superAdminRole.setCreatedTime(new Date());
				superAdminRole.setRole(RoleEnum.SUPER_ADMIN.toString());
				superAdminRole.setHospitalId(roleCollection.getHospitalId());
				superAdminRole.setLocationId(roleCollection.getLocationId());
				superAdminRole = roleRepository.save(superAdminRole);
				// create SuperAdmin userRole
				superAdminRoleCollection = new UserRoleCollection();
				superAdminRoleCollection.setRoleId(superAdminRole.getId());
				superAdminRoleCollection.setUserId(userRoleCollection.getUserId());
				superAdminRoleCollection.setCreatedTime(new Date());
				superAdminRoleCollection = userRoleRepository.save(superAdminRoleCollection);
				// create subscription detail
				subscriptionDetailCollection = new SubscriptionDetailCollection();
				subscriptionDetailCollection.setCreatedTime(new Date());
				subscriptionDetailCollection.setDoctorId(superAdminRoleCollection.getUserId());
				subscriptionDetailCollection.setFromDate(new Date());
				subscriptionDetailCollection.setSmsFromDate(new Date());
				subscriptionDetailCollection.setIsExpired(false);
				locationIdSet = new HashSet<ObjectId>();
				locationIdSet.add(superAdminRole.getLocationId());
				subscriptionDetailCollection.setLocationIds(locationIdSet);
				subscriptionDetailCollection.setNoOfsms(500);
				subscriptionDetailCollection.setToDate(DPDoctorUtils.addmonth(new Date(), 12));
				subscriptionDetailCollection.setSmsToDate(DPDoctorUtils.addmonth(new Date(), 12));
				subscriptionDetailCollection.setIsDemo(true);
				subscriptionDetailCollection = subscriptionDetailRepository.save(subscriptionDetailCollection);
				subscriptionDetail = new SubscriptionDetail();
				BeanUtil.map(subscriptionDetailCollection, subscriptionDetail);
				response.add(subscriptionDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

}
