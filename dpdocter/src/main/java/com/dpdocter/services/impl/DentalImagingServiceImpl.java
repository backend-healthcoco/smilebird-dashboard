package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.dpdocter.beans.CBDTArch;
import com.dpdocter.beans.CBDTQuadrant;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.FOV;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.collections.CBDTArchCollection;
import com.dpdocter.collections.CBDTQuadrantCollection;
import com.dpdocter.collections.DentalDiagnosticServiceCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorHospitalDentalImagingAssociationCollection;
import com.dpdocter.collections.FOVCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CBDTArchRepository;
import com.dpdocter.repository.CBDTQuadrantRepository;
import com.dpdocter.repository.DentalDiagnosticServiceRepository;
import com.dpdocter.repository.DoctorHospitalDentalImagingAssociationRepository;
import com.dpdocter.repository.FOVRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.DoctorHospitalDentalImagingAssociationLookupResponse;
import com.dpdocter.services.DentalImagingService;
import com.dpdocter.webservices.DentalImagingAPI;

import common.util.web.DPDoctorUtils;

@Service
public class DentalImagingServiceImpl implements DentalImagingService {

	private static Logger logger = LogManager.getLogger(DentalImagingAPI.class.getName());

	@Autowired
	private DentalDiagnosticServiceRepository dentalDiagnosticServiceRepository;

	@Autowired
	private DoctorHospitalDentalImagingAssociationRepository doctorHospitalDentalImagingAssociationRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private CBDTQuadrantRepository cbdtQuadrantRepository;

	@Autowired
	private CBDTArchRepository cbdtArchRepository;

	@Autowired
	private FOVRepository fovRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	@Transactional
	public DentalDiagnosticService addEditService(DentalDiagnosticService service) {
		DentalDiagnosticServiceCollection dentalDiagnosticServiceCollection = null;
		DentalDiagnosticService response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(service.getId())) {
				dentalDiagnosticServiceCollection = dentalDiagnosticServiceRepository
						.findById(new ObjectId(service.getId())).orElse(null);
			} else {
				dentalDiagnosticServiceCollection = new DentalDiagnosticServiceCollection();
			}
			BeanUtil.map(service, dentalDiagnosticServiceCollection);
			dentalDiagnosticServiceCollection = dentalDiagnosticServiceRepository
					.save(dentalDiagnosticServiceCollection);
			if (dentalDiagnosticServiceCollection != null) {
				response = new DentalDiagnosticService();
				BeanUtil.map(dentalDiagnosticServiceCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalDiagnosticService> getServices(String searchTerm, String type, int page, int size) {
		List<DentalDiagnosticService> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm), new Criteria("name").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalDiagnosticService> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalDiagnosticServiceCollection.class, DentalDiagnosticService.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalDiagnosticService getServiceById(String serviceId) {
		DentalDiagnosticServiceCollection dentalDiagnosticServiceCollection = null;
		DentalDiagnosticService response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(serviceId)) {
				dentalDiagnosticServiceCollection = dentalDiagnosticServiceRepository.findById(new ObjectId(serviceId)).orElse(null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (dentalDiagnosticServiceCollection != null) {
				response = new DentalDiagnosticService();
				BeanUtil.map(dentalDiagnosticServiceCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalDiagnosticService discardService(String serviceId, Boolean discarded) {
		DentalDiagnosticServiceCollection dentalDiagnosticServiceCollection = null;
		DentalDiagnosticService response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(serviceId)) {
				dentalDiagnosticServiceCollection = dentalDiagnosticServiceRepository.findById(new ObjectId(serviceId)).orElse(null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			
			dentalDiagnosticServiceCollection.setDiscarded(discarded);
			dentalDiagnosticServiceCollection = dentalDiagnosticServiceRepository
					.save(dentalDiagnosticServiceCollection);
			if (dentalDiagnosticServiceCollection != null) {
				response = new DentalDiagnosticService();
				BeanUtil.map(dentalDiagnosticServiceCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDoctorHospitalDentalImagingAssociation(List<DoctorHospitalDentalImagingAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		DoctorHospitalDentalImagingAssociationCollection doctorHospitalDentalImagingAssociationCollection = null;
		try {
			for (DoctorHospitalDentalImagingAssociation doctorHospitalDentalImagingAssociation : request) {
				doctorHospitalDentalImagingAssociationCollection = doctorHospitalDentalImagingAssociationRepository
						.findByDoctorIdAndDoctorLocationIdAndHospitalId(new ObjectId(doctorHospitalDentalImagingAssociation.getDoctorId()),
								new ObjectId(doctorHospitalDentalImagingAssociation.getDoctorLocationId()),
								new ObjectId(doctorHospitalDentalImagingAssociation.getHospitalId()));

				if (doctorHospitalDentalImagingAssociationCollection == null) {
					doctorHospitalDentalImagingAssociationCollection = new DoctorHospitalDentalImagingAssociationCollection();
				} else {
					oldId = doctorHospitalDentalImagingAssociationCollection.getId();
				}
				BeanUtil.map(doctorHospitalDentalImagingAssociation, doctorHospitalDentalImagingAssociationCollection);
				doctorHospitalDentalImagingAssociationCollection.setId(oldId);
				doctorHospitalDentalImagingAssociationCollection = doctorHospitalDentalImagingAssociationRepository
						.save(doctorHospitalDentalImagingAssociationCollection);
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
	public List<DoctorHospitalDentalImagingAssociationLookupResponse> getDoctorHospitalAssociation(String doctorId,
			String doctorLocationId, String hospitalId, String searchTerm, int page, int size, Boolean discarded) {
		List<DoctorHospitalDentalImagingAssociationLookupResponse> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorLocationId)) {
				criteria.and("doctorLocationId").is(new ObjectId(doctorLocationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("location.locationName").regex("^" + searchTerm, "i"),
						new Criteria("location.locationName").regex("^" + searchTerm),
						new Criteria("location.locationName").regex(searchTerm + ".*"),
						new Criteria("hospital.hospitalName").regex("^" + searchTerm, "i"),
						new Criteria("hospital.hospitalName").regex("^" + searchTerm),
						new Criteria("hospital.hospitalName").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"),
						Aggregation.lookup("location_cl", "doctorLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"),
						Aggregation.lookup("location_cl", "doctorLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DoctorHospitalDentalImagingAssociationLookupResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, DoctorHospitalDentalImagingAssociationCollection.class,
							DoctorHospitalDentalImagingAssociationLookupResponse.class);

			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<Hospital> getHospitalList(String doctorId, String hospitalId) {
		List<Hospital> hospitals = null;

		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
			Criteria criteria = new Criteria("doctorId").is(userCollection.getId());
			criteria.and("location.hospitalId").is(new ObjectId(hospitalId));
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("hospital_cl", "$location.hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital"), Aggregation.match(criteria)),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if (doctorClinicProfileLookupResponses == null || doctorClinicProfileLookupResponses.isEmpty()) {
				logger.warn("None of your clinic is active");
				// user.setUserState(UserState.NOTACTIVATED);
				throw new BusinessException(ServiceError.NotAuthorized, "None of your clinic is active");
			}
			if (doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				hospitals = new ArrayList<Hospital>();
				Map<String, Hospital> checkHospitalId = new HashMap<String, Hospital>();
				for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					LocationCollection locationCollection = doctorClinicProfileLookupResponse.getLocation();
					HospitalCollection hospitalCollection = doctorClinicProfileLookupResponse.getHospital();
					LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
					BeanUtil.map(locationCollection, locationAndAccessControl);
					locationAndAccessControl.setIsActivate(doctorClinicProfileLookupResponse.getIsActivate());
					locationAndAccessControl.setIsVerified(doctorClinicProfileLookupResponse.getIsVerified());
					locationAndAccessControl.setLogoUrl(getFinalImageURL(locationAndAccessControl.getLogoUrl()));
					locationAndAccessControl
							.setLogoThumbnailUrl(getFinalImageURL(locationAndAccessControl.getLogoThumbnailUrl()));
					locationAndAccessControl.setImages(getFinalClinicImages(locationAndAccessControl.getImages()));
					Hospital hospital = new Hospital();
					BeanUtil.map(hospitalCollection, hospital);

					hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
					hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
					checkHospitalId.put(locationCollection.getHospitalId().toString(), hospital);
					hospitals.add(hospital);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occured :: " + e);
		}
		return hospitals;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	private List<ClinicImage> getFinalClinicImages(List<ClinicImage> clinicImages) {
		if (clinicImages != null && !clinicImages.isEmpty())
			for (ClinicImage clinicImage : clinicImages) {
				if (clinicImage.getImageUrl() != null) {
					clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
				}
				if (clinicImage.getThumbnailUrl() != null) {
					clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
				}
			}
		return clinicImages;
	}

	@Override
	@Transactional
	public CBDTQuadrant addeditCBDTQuadrant(CBDTQuadrant cbdtQuadrant) {
		CBDTQuadrantCollection quadrantCollection = null;
		CBDTQuadrant response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(cbdtQuadrant.getId())) {
				quadrantCollection = cbdtQuadrantRepository.findById(new ObjectId(cbdtQuadrant.getId())).orElse(null);
				cbdtQuadrant.setCreatedTime(quadrantCollection.getCreatedTime());
			} else {
				quadrantCollection = new CBDTQuadrantCollection();
				cbdtQuadrant.setCreatedTime(new Date());
			}
			BeanUtil.map(cbdtQuadrant, quadrantCollection);
			quadrantCollection = cbdtQuadrantRepository.save(quadrantCollection);
			if (quadrantCollection != null) {
				response = new CBDTQuadrant();
				BeanUtil.map(quadrantCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public CBDTArch addeditCBDTArch(CBDTArch cbdtArch) {
		CBDTArchCollection cbdtArchCollection = null;
		CBDTArch response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(cbdtArch.getId())) {
				cbdtArchCollection = cbdtArchRepository.findById(new ObjectId(cbdtArch.getId())).orElse(null);
				cbdtArch.setCreatedTime(cbdtArchCollection.getCreatedTime());
			} else {
				cbdtArchCollection = new CBDTArchCollection();
				cbdtArch.setCreatedTime(new Date());
			}
			BeanUtil.map(cbdtArch, cbdtArchCollection);
			cbdtArchCollection = cbdtArchRepository.save(cbdtArchCollection);
			if (cbdtArchCollection != null) {
				response = new CBDTArch();
				BeanUtil.map(cbdtArchCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}


	@Override
	@Transactional
	public FOV addeditFOV(FOV fov) {
		FOVCollection fovCollection = null;
		FOV response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(fov.getId())) {
				fovCollection = fovRepository.findById(new ObjectId(fov.getId())).orElse(null);
				fov.setCreatedTime(fovCollection.getCreatedTime());
			} else {
				fovCollection = new FOVCollection();
				fov.setCreatedTime(new Date());
			}
			BeanUtil.map(fov, fovCollection);
			fovCollection = fovRepository.save(fovCollection);
			if (fovCollection != null) {
				response = new FOV();
				BeanUtil.map(fovCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	
	@Override
	@Transactional
	public List<CBDTQuadrant> getCBDTQuadrants(String searchTerm, int page, int size) {
		List<CBDTQuadrant> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm), new Criteria("name").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<CBDTQuadrant> aggregationResults = mongoTemplate.aggregate(aggregation,
					CBDTQuadrantCollection.class, CBDTQuadrant.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	
	@Override
	@Transactional
	public List<CBDTArch> getCBDTArchs(String searchTerm, int page, int size) {
		List<CBDTArch> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm), new Criteria("name").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<CBDTArch> aggregationResults = mongoTemplate.aggregate(aggregation,
					CBDTArchCollection.class, CBDTArch.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<FOV> getFOVs(String searchTerm, int page, int size) {
		List<FOV> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm), new Criteria("name").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<FOV> aggregationResults = mongoTemplate.aggregate(aggregation,
					FOVCollection.class, FOV.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public CBDTQuadrant getCBDTQuadrantById(String id) {
		CBDTQuadrantCollection cbdtQuadrantCollection = null;
		CBDTQuadrant response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				cbdtQuadrantCollection = cbdtQuadrantRepository.findById(new ObjectId(id)).orElse(null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (cbdtQuadrantCollection != null) {
				response = new CBDTQuadrant();
				BeanUtil.map(cbdtQuadrantCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public CBDTArch getCBDTArchById(String id) {
		CBDTArchCollection cbdtArchCollection = null;
		CBDTArch response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				cbdtArchCollection = cbdtArchRepository.findById(new ObjectId(id)).orElse(null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (cbdtArchCollection != null) {
				response = new CBDTArch();
				BeanUtil.map(cbdtArchCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public FOV getFOVById(String id) {
		FOVCollection fovCollection = null;
		FOV response = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				fovCollection = fovRepository.findById(new ObjectId(id)).orElse(null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (fovCollection != null) {
				response = new FOV();
				BeanUtil.map(fovCollection, response);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
}
