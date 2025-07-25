package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabAssociation;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.Specimen;
import com.dpdocter.collections.LabAssociationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RateCardCollection;
import com.dpdocter.collections.SpecimenCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RateCardRepository;
import com.dpdocter.repository.SpecimenRepository;
import com.dpdocter.response.LabAssociationLookupResponse;
import com.dpdocter.services.LocationServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import common.util.web.DPDoctorUtils;

@Service
public class LocationServiceImpl implements LocationServices {
	@Value("${geocoding.services.api.key}")
	private String GEOCODING_SERVICES_API_KEY;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private SpecimenRepository specimenRepository;

	@Autowired
	private RateCardRepository rateCardRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	private static Logger logger = LogManager.getLogger(LocationServiceImpl.class);

	@Override
	public List<GeocodedLocation> geocodeLocation(String address) {
		List<GeocodedLocation> response = null;
		GeoApiContext context = new GeoApiContext().setApiKey(GEOCODING_SERVICES_API_KEY);
		GeocodingResult[] results = null;
		try {
			results = GeocodingApi.geocode(context, address).await();
			if (results != null && results.length != 0) {
				response = new ArrayList<GeocodedLocation>();
				for (GeocodingResult result : results) {
					GeocodedLocation geocodedLocation = new GeocodedLocation();
					geocodedLocation.setFormattedAddress(result.formattedAddress);
					geocodedLocation.setLatitude(result.geometry.location.lat);
					geocodedLocation.setLongitude(result.geometry.location.lng);
					response.add(geocodedLocation);
				}
			}
		} catch (Exception e) {
			throw new BusinessException(ServiceError.Unknown, "Couldn't Geocode the location "+e.getMessage());
		}
		return response;
	}

	

	@Override
	public List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude) {
		List<GeocodedLocation> response = null;
		GeoApiContext context = new GeoApiContext().setApiKey(GEOCODING_SERVICES_API_KEY);
		GeocodingResult[] results = null;
		try {
			results = GeocodingApi.newRequest(context).latlng(new LatLng(latitude, longitude)).await();
			if (results != null && results.length != 0) {
				response = new ArrayList<GeocodedLocation>();
				for (GeocodingResult result : results) {
					GeocodedLocation geocodedLocation = new GeocodedLocation();
					geocodedLocation.setFormattedAddress(result.formattedAddress);
					geocodedLocation.setLatitude(result.geometry.location.lat);
					geocodedLocation.setLongitude(result.geometry.location.lng);
					response.add(geocodedLocation);
				}
			}
		} catch (Exception e) {
			throw new BusinessException(ServiceError.Unknown, "Couldn't Geocode the location");
		}
		return response;
	}

	@Override
	@Transactional
	public List<Location> getAssociatedLabs(String locationId, Boolean isParent) {

		List<LabAssociationLookupResponse> lookupResponses = null;
		List<Location> locations = null;
		ObjectId locationObjectId = new ObjectId(locationId);
		try {
			LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "location not found");
			}
			Criteria criteria = new Criteria();
			criteria.and("isActive").is(Boolean.TRUE);
			Aggregation aggregation = null;
			if (isParent) {
				criteria.and("parentLabId").is(locationObjectId);
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			} else {
				criteria.and("daughterLabId").is(locationObjectId);
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "parentLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			}
			AggregationResults<LabAssociationLookupResponse> results = mongoTemplate.aggregate(aggregation,
					LabAssociationCollection.class, LabAssociationLookupResponse.class);
			lookupResponses = new ArrayList<LabAssociationLookupResponse>();
			lookupResponses = results.getMappedResults();
			if (lookupResponses != null) {
				locations = new ArrayList<Location>();
				for (LabAssociationLookupResponse response : lookupResponses) {
					locations.add(response.getLocation());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return locations;

	}

	@Override
	@Transactional
	public Boolean addAssociatedLabs(List<LabAssociation> labAssociations) {
		Boolean response = false;
	
		return response;
	}

	@Override
	@Transactional
	public Location addEditParentLab(String locationId, Boolean isParent) {
		Location response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "location not found");
			}
			locationCollection.setIsParent(isParent);
			locationCollection = locationRepository.save(locationCollection);
			response = new Location();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<Specimen> getSpecimenList(int page, int size, String searchTerm) {
		List<Specimen> specimens = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("specimen").regex("^" + searchTerm, "i"),
						new Criteria("specimen").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Specimen> aggregationResults = mongoTemplate.aggregate(aggregation,
					SpecimenCollection.class, Specimen.class);
			specimens = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Specimens");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Specimens");
		}
		return specimens;
	}

	@Override
	@Transactional
	public Specimen addEditSpecimen(Specimen request) {
		Specimen specimen = null;
		SpecimenCollection specimenCollection = null;
		try {
			if (request.getId() != null) {
				specimenCollection = specimenRepository.findById(new ObjectId(request.getId())).orElse(null);
			} else {
				specimenCollection = new SpecimenCollection();
			}

			BeanUtil.map(request, specimenCollection);
			specimenCollection = specimenRepository.save(specimenCollection);
			specimen = new Specimen();
			BeanUtil.map(specimenCollection, specimen);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Specimens");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Specimens");
		}
		return specimen;
	}

	public RateCard addEditRateCard(RateCard request) {
		RateCard response = null;
		RateCardCollection rateCardCollection = null;
		try {
			if (request.getId() != null) {
				rateCardCollection = rateCardRepository.findById(new ObjectId(request.getId())).orElse(null);
			} else {
				rateCardCollection = new RateCardCollection();
			}
			BeanUtil.map(request, rateCardCollection);
			rateCardCollection = rateCardRepository.save(rateCardCollection);
			response = new RateCard();
			BeanUtil.map(rateCardCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error adding / editing ratecard");
			throw new BusinessException(ServiceError.Unknown, "Error adding / editing ratecard");
		}
		return response;
	}
	
	
	

	/*
	 * @Override
	 * 
	 * @Transactional public List<RateCard> getRateCard(int page, int size,
	 * String locationId, String category, String searchTerm , String labId) {
	 * List<RateCard> rateCards = null; try { Aggregation aggregation = null;
	 * 
	 * Criteria criteria = new Criteria();
	 * 
	 * if (!DPDoctorUtils.anyStringEmpty(locationId)) {
	 * 
	 * criteria = criteria.andOperator(new
	 * Criteria("locationId").is(locationId)); } if
	 * (!DPDoctorUtils.anyStringEmpty(searchTerm)) { criteria =
	 * criteria.orOperator(new Criteria("diagnosticTest.testName").regex("^" +
	 * searchTerm, "i"), new Criteria("diagnosticTest.testName").regex("^" +
	 * searchTerm)); }
	 * 
	 * if (size > 0) { aggregation = Aggregation.newAggregation(
	 * Aggregation.lookup("doctor_clinic_profile_cl", "_id", "doctorId",
	 * "doctorProfile"),
	 * projectList.and("doctorProfile").size().as("doctorProfile"),
	 * Aggregation.match(criteria), projectList, Aggregation.skip((long)(page) *
	 * size), Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC,
	 * "createdTime")); } else { aggregation = Aggregation.newAggregation(
	 * Aggregation.lookup("doctor_clinic_profile_cl", "_id", "doctorId",
	 * "doctorProfile"),
	 * projectList.and("doctorProfile").size().as("doctorProfile"),
	 * Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC,
	 * "createdTime")); }
	 * 
	 * AggregationResults<UserCollection> results =
	 * mongoTemplate.aggregate(aggregation, UserCollection.class,
	 * UserCollection.class); List<UserCollection> userCollections =
	 * results.getMappedResults(); if (userCollections != null &&
	 * !userCollections.isEmpty()) { response = new ArrayList<DoctorResponse>();
	 * for (UserCollection userCollection : userCollections) { DoctorResponse
	 * doctorResponse = new DoctorResponse(); BeanUtil.map(userCollection,
	 * doctorResponse); List<UserRoleCollection> userRoleCollection =
	 * userRoleRepository .findByUserId(userCollection.getId());
	 * 
	 * @SuppressWarnings("unchecked") Collection<ObjectId> roleIds =
	 * CollectionUtils.collect(userRoleCollection, new
	 * BeanToPropertyValueTransformer("roleId")); if (roleIds != null &&
	 * !roleIds.isEmpty()) { Integer count =
	 * roleRepository.findCountByIdAndRole(roleIds,
	 * RoleEnum.LOCATION_ADMIN.getRole()); if (count != null && count > 0)
	 * doctorResponse.setRole(RoleEnum.LOCATION_ADMIN.getRole()); }
	 * response.add(doctorResponse); } } } catch (Exception e) { logger.error(
	 * "Error while getting doctors " + e.getMessage()); e.printStackTrace();
	 * throw new BusinessException(ServiceError.Unknown,
	 * "Error while getting doctors " + e.getMessage()); } return rateCards; }
	 */

}
