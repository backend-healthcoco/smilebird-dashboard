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
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.ProductAndService;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.ProductsAndServicesCollection;
import com.dpdocter.collections.ProductsAndServicesCostCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.PatientTreatmentService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.ProductsAndServicesCostRepository;
import com.dpdocter.repository.ProductsAndServicesRepository;
import com.dpdocter.repository.TreatmentRatelistRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.PatientTreatmentAddEditRequest;
import com.dpdocter.request.TreatmentRatelistRequest;
import com.dpdocter.request.TreatmentRequest;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.response.TreatmentRatelistResponse;
import com.dpdocter.response.TreatmentResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.PatientTreatmentServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class PatientTreatmentServicesImpl implements PatientTreatmentServices {
	private static Logger logger = LogManager.getLogger(PatientTreatmentServicesImpl.class);

	@Autowired
	private ProductsAndServicesRepository productsAndServicesRepository;

	@Autowired
	private ProductsAndServicesCostRepository productsAndServicesCostRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TreatmentServicesRepository treatmentServicesRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientTreamentRepository patientTreamentRepository;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private TreatmentRatelistRepository treatmentRatelistRepository;

	@Override
	@Transactional
	public boolean addEditProductService(ProductAndService productAndService) {
		boolean response = false;
		ProductsAndServicesCollection productsAndServicesCollection;
		ProductsAndServicesCostCollection productsAndServicesCostCollection;
		try {
			if (DPDoctorUtils.anyStringEmpty(productAndService.getId())) {
				productsAndServicesCollection = new ProductsAndServicesCollection();
				BeanUtil.map(productAndService, productsAndServicesCollection);
				productsAndServicesCollection.setCreatedTime(new Date());
				productsAndServicesCollection.setUpdatedTime(new Date());
				productsAndServicesCollection = productsAndServicesRepository.save(productsAndServicesCollection);

				if (productAndService.getCost() != 0.0) {
					productsAndServicesCostCollection = new ProductsAndServicesCostCollection();
					BeanUtil.map(productAndService, productsAndServicesCostCollection);
					productsAndServicesCostCollection.setProductAndServiceId(productsAndServicesCollection.getId());
					productsAndServicesCostCollection.setCreatedTime(new Date());
					productsAndServicesCostCollection.setUpdatedTime(new Date());
					productsAndServicesCostCollection = productsAndServicesCostRepository
							.save(productsAndServicesCostCollection);
				}
			} else {
				productsAndServicesCollection = productsAndServicesRepository
						.findById(new ObjectId(productAndService.getId())).orElse(null);
				if (productsAndServicesCollection != null) {
					List<String> specialityIds = productAndService.getSpecialityIds();
					if (productAndService.getSpecialityIds() != null
							&& !productAndService.getSpecialityIds().isEmpty()) {
						specialityIds.addAll(productAndService.getSpecialityIds());
						Set<String> tempSpecialityIds = new HashSet<String>(specialityIds);
						specialityIds.clear();
						specialityIds.addAll(tempSpecialityIds);
					}
					if (!DPDoctorUtils.anyStringEmpty(productAndService.getName())) {
						productsAndServicesCollection.setName(productAndService.getName());
					}
					if (!DPDoctorUtils.anyStringEmpty(productAndService.getLocationId())) {
						productsAndServicesCollection.setLocationId(new ObjectId(productAndService.getLocationId()));
					}
					if (!DPDoctorUtils.anyStringEmpty(productAndService.getHospitalId())) {
						productsAndServicesCollection.setHospitalId(new ObjectId(productAndService.getHospitalId()));
					}
					if (!DPDoctorUtils.anyStringEmpty(productAndService.getDoctorId())) {
						productsAndServicesCollection.setDoctorId(new ObjectId(productAndService.getDoctorId()));
					}

					productsAndServicesCostCollection = productsAndServicesCostRepository
							.findById(new ObjectId(productAndService.getId())).orElse(null);
					if (productsAndServicesCostCollection != null) {
						ObjectId productsAndServicesCostCollectionId = productsAndServicesCostCollection.getId();
						if (productAndService.getCost() != 0.0) {
							productsAndServicesCostCollection.setCost(productAndService.getCost());
						}
						BeanUtil.map(productsAndServicesCollection, productsAndServicesCostCollection);
						productsAndServicesCostCollection.setId(productsAndServicesCostCollectionId);
					}

					productsAndServicesCollection.setUpdatedTime(new Date());
					productsAndServicesCollection = productsAndServicesRepository.save(productsAndServicesCollection);

					productsAndServicesCostCollection.setUpdatedTime(new Date());
					productsAndServicesCostCollection = productsAndServicesCostRepository
							.save(productsAndServicesCostCollection);
				} else {
					throw new BusinessException(ServiceError.NotFound, "No product or service found for the given Id");
				}
			}
			response = true;
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing products and services", e);
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing products and services");
		}
		return response;
	}

	@Override
	@Transactional
	public List<?> getServices(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm,
			String category, String disease, String speciality, String ratelistId) {

		List<?> response = new ArrayList<Object>();

		switch (PatientTreatmentService.valueOf(type.toUpperCase())) {

		case SERVICE: {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				searchTerm = searchTerm.toUpperCase();
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalTreatmentServiceForAdmin(page, size, updatedTime, discarded, searchTerm,
							category, ratelistId);
				else
					response = getGlobalTreatmentService(page, size, updatedTime, discarded, ratelistId);
				break;

			case CUSTOM:
				if (isAdmin)
					response = getCustomTreatmentServiceForAdmin(page, size, updatedTime, discarded, searchTerm,
							category, ratelistId);
				else
					response = getCustomTreatmentService(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, ratelistId);
				break;

			case BOTH:
				if (isAdmin)
					response = getCustomGlobalTreatmentServiceForAdmin(page, size, updatedTime, discarded, searchTerm,
							category, ratelistId);
				else
					response = getCustomGlobalTreatmentService(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, ratelistId);
				break;
			}
			break;
		}
		default:
			break;
		}
		return response;

	}

	private List<TreatmentService> getGlobalTreatmentServiceForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category, String ratelistId) {
		List<TreatmentService> response = null;
		try {
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"name", null, category, null, ratelistId),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

	private List<TreatmentService> getCustomTreatmentServiceForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category, String ratelistId) {
		List<TreatmentService> response = null;
		try {
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"name", null, category, null, ratelistId),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

	private List<TreatmentService> getCustomGlobalTreatmentServiceForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category, String ratelistId) {
		List<TreatmentService> response = null;
		try {
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"name", null, category, null, ratelistId),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

	@Override
	@Transactional
	public TreatmentService addEditService(TreatmentService treatmentService) {
		TreatmentService response = null;
		TreatmentServicesCollection treatmentServicesCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(treatmentService.getId())) {
				treatmentServicesCollection = new TreatmentServicesCollection();
				BeanUtil.map(treatmentService, treatmentServicesCollection);
				treatmentServicesCollection.setCreatedTime(new Date());
				treatmentServicesCollection.setUpdatedTime(new Date());
				treatmentServicesCollection.setTreatmentCode("TR" + DPDoctorUtils.generateRandomId());

				if (!DPDoctorUtils.anyStringEmpty(treatmentServicesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(treatmentServicesCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						treatmentServicesCollection.setRankingCount(1);

						treatmentServicesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					treatmentServicesCollection.setCreatedBy("ADMIN");
				}
				treatmentServicesCollection = treatmentServicesRepository.save(treatmentServicesCollection);
			} else {
				treatmentServicesCollection = treatmentServicesRepository
						.findById(new ObjectId(treatmentService.getId())).orElse(null);
				if (treatmentServicesCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(treatmentService.getName())) {
						treatmentServicesCollection.setName(treatmentService.getName());
					}
					treatmentServicesCollection.setRatelistId(new ObjectId(treatmentService.getRatelistId()));
					treatmentServicesCollection.setFieldsRequired(treatmentService.getFieldsRequired());
					treatmentServicesCollection.setSpeciality(treatmentService.getSpeciality());
					treatmentServicesCollection.setCategory(treatmentService.getCategory());
					treatmentServicesCollection.setUpdatedTime(new Date());
					treatmentServicesCollection = treatmentServicesRepository.save(treatmentServicesCollection);
				} else {
					logger.error("No service found for the given Id");
					throw new BusinessException(ServiceError.NotFound, "No service found for the given Id");
				}
			}
			if (treatmentServicesCollection != null) {
				response = new TreatmentService();
				BeanUtil.map(treatmentServicesCollection, response);
			}
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing services", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while adding or editing services");
		}
		return response;
	}

	private List<TreatmentService> getGlobalTreatmentService(int page, int size, String updatedTime, boolean discarded,
			String ratelistId) {
		List<TreatmentService> response = null;
		try {
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, ratelistId, null, null, null),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

	private List<TreatmentService> getCustomTreatmentService(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String ratelistId) {
		List<TreatmentService> response = null;
		try {
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, ratelistId, null, null),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

	private List<TreatmentService> getCustomGlobalTreatmentService(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, boolean discarded, String ratelistId) {
		List<TreatmentService> response = null;
		try {
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, ratelistId, null, null, null),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

	@Override
	public TreatmentService deleteService(String treatmentServiceId, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		TreatmentService response = null;
		try {
			TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
					.findById(new ObjectId(treatmentServiceId)).orElse(null);
			if (treatmentServicesCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(treatmentServicesCollection.getDoctorId(),
						treatmentServicesCollection.getHospitalId(), treatmentServicesCollection.getLocationId())) {
					if (treatmentServicesCollection.getDoctorId().toString().equals(doctorId)
							&& treatmentServicesCollection.getHospitalId().toString().equals(hospitalId)
							&& treatmentServicesCollection.getLocationId().toString().equals(locationId)) {
						treatmentServicesCollection.setDiscarded(discarded);
						treatmentServicesCollection.setUpdatedTime(new Date());
						treatmentServicesRepository.save(treatmentServicesCollection);
						response = new TreatmentService();
						BeanUtil.map(treatmentServicesCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Treatment Service not found!");
				throw new BusinessException(ServiceError.NoRecord, "Treatment Service not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public boolean addEditProductServiceCost(ProductAndService productAndService) {
		boolean response = false;
		ProductsAndServicesCostCollection productAndServiceCostCollection = null;
		try {
			List<ProductsAndServicesCostCollection> productsAndServicesCostCollections = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(
									new Criteria("productAndServiceId").is(new ObjectId(productAndService.getId()))
											.and("locationId").is(new ObjectId(productAndService.getLocationId()))
											.and("hospitalId").is(new ObjectId(productAndService.getDoctorId()))
											.and("doctorId").is(new ObjectId(productAndService.getHospitalId())))),
							ProductsAndServicesCostCollection.class, ProductsAndServicesCostCollection.class)
					.getMappedResults();
			if (productsAndServicesCostCollections != null && !productsAndServicesCostCollections.isEmpty())
				productAndServiceCostCollection = productsAndServicesCostCollections.get(0);

			if (productAndServiceCostCollection != null) {
				productAndServiceCostCollection.setCost(productAndService.getCost());
			} else {
				productAndServiceCostCollection = new ProductsAndServicesCostCollection();
				BeanUtil.map(productAndService, productAndServiceCostCollection);
				productAndServiceCostCollection.setId(null);
				if (!DPDoctorUtils.anyStringEmpty(productAndService.getId()))
					productAndServiceCostCollection.setProductAndServiceId(new ObjectId(productAndService.getId()));
			}
			productAndServiceCostCollection.setUpdatedTime(new Date());
			productAndServiceCostCollection = productsAndServicesCostRepository.save(productAndServiceCostCollection);
			response = true;
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing cost for products and services", e);
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing cost for products and services");
		}
		return response;
	}

	@Override
	@Transactional
	public List<ProductAndService> getProductsAndServices(String locationId, String hospitalId, String doctorId) {
		List<ProductAndService> response = null;
		try {
			DoctorCollection doctor = doctorRepository.findByUserId(new ObjectId(doctorId));
			List<ObjectId> specialityIds = doctor.getSpecialities();
			List<ProductsAndServicesCollection> productsAndServicesCollections = productsAndServicesRepository
					.findBySpecialityIdsIn(specialityIds);
			if (productsAndServicesCollections != null && !productsAndServicesCollections.isEmpty()) {
				response = new ArrayList<ProductAndService>();
				for (ProductsAndServicesCollection productAndServiceCollection : productsAndServicesCollections) {
					ProductAndService productAndService = new ProductAndService();
					BeanUtil.map(productAndServiceCollection, productAndService);

					ProductsAndServicesCostCollection productAndServiceCostCollection = null;
					List<ProductsAndServicesCostCollection> productsAndServicesCostCollections = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(new Criteria("productAndServiceId")
											.is(new ObjectId(productAndService.getId())).and("locationId")
											.is(new ObjectId(productAndService.getLocationId())).and("hospitalId")
											.is(new ObjectId(productAndService.getDoctorId())).and("doctorId")
											.is(new ObjectId(productAndService.getHospitalId())))),
									ProductsAndServicesCostCollection.class, ProductsAndServicesCostCollection.class)
							.getMappedResults();
					if (productsAndServicesCostCollections != null && !productsAndServicesCostCollections.isEmpty())
						productAndServiceCostCollection = productsAndServicesCostCollections.get(0);

					if (productAndServiceCostCollection != null) {
						productAndService.setCost(productAndServiceCostCollection.getCost());
					}
					response.add(productAndService);
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "No products and services found");
			}
		} catch (Exception e) {
			logger.error("Error occurred getting products and services", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred getting products and services");
		}
		return response;
	}

	@Override
	@Transactional
	public List<TreatmentService> getTreatmentServices(List<ObjectId> idList) {
		List<TreatmentService> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(idList).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(aggregation,

					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

	@Override
	@Transactional
	public PatientTreatmentResponse addEditPatientTreatment(PatientTreatmentAddEditRequest request,
			Boolean isAppointmentAdd, String createdBy, Appointment appointment) {
		PatientTreatmentResponse response;
		PatientTreatmentCollection patientTreatmentCollection = new PatientTreatmentCollection();
		try {
			if (request.getAppointmentRequest() != null && isAppointmentAdd) {
				appointment = addTreatmentAppointment(request.getAppointmentRequest());
				if (appointment != null) {
					request.setAppointmentId(appointment.getAppointmentId());
					request.setTime(appointment.getTime());
					request.setFromDate(appointment.getFromDate());
				}
			}

			patientTreatmentCollection = new PatientTreatmentCollection();
			BeanUtil.map(request, patientTreatmentCollection);

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {

				if (request.getCreatedTime() != null) {
					patientTreatmentCollection.setCreatedTime(request.getCreatedTime());
				} else {
					patientTreatmentCollection.setCreatedTime(new Date());
				}
				patientTreatmentCollection.setAdminCreatedTime(new Date());
				patientTreatmentCollection
						.setUniqueEmrId(UniqueIdInitial.TREATMENT.getInitial() + DPDoctorUtils.generateRandomId());

				if (DPDoctorUtils.anyStringEmpty(createdBy)) {
					UserCollection userCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
						userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
					}
					if (userCollection != null)
						createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName();
					else {
						throw new BusinessException(ServiceError.NotFound, "No Doctor Found");
					}
				}
				patientTreatmentCollection.setCreatedBy(createdBy);
			} else {
				PatientTreatmentCollection oldPatientTreatmentCollection = patientTreamentRepository
						.findByIdAndDoctorIdAndLocationIdAndHospitalId(new ObjectId(request.getId()),
								new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
								new ObjectId(request.getHospitalId()));

				if (oldPatientTreatmentCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "No treatment found for the given ids");
				} else {

					createdBy = oldPatientTreatmentCollection.getCreatedBy();

					BeanUtil.map(request, patientTreatmentCollection);
					if (request.getCreatedTime() != null) {
						patientTreatmentCollection.setCreatedTime(new Date());
					} else {
						patientTreatmentCollection.setCreatedTime(oldPatientTreatmentCollection.getCreatedTime());
					}
					patientTreatmentCollection.setAdminCreatedTime(oldPatientTreatmentCollection.getAdminCreatedTime());
					patientTreatmentCollection.setUpdatedTime(new Date());
					patientTreatmentCollection.setCreatedBy(createdBy);
					patientTreatmentCollection.setUniqueEmrId(oldPatientTreatmentCollection.getUniqueEmrId());
					patientTreatmentCollection.setDiscarded(oldPatientTreatmentCollection.getDiscarded());
					patientTreatmentCollection.setInHistory(oldPatientTreatmentCollection.getInHistory());
					patientTreatmentCollection
							.setIsPatientDiscarded(oldPatientTreatmentCollection.getIsPatientDiscarded());
				}
			}
			List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
			List<Treatment> treatments = new ArrayList<Treatment>();
			if (request.getTreatments() != null && !request.getTreatments().isEmpty())
				for (TreatmentRequest treatmentRequest : request.getTreatments()) {

					if (treatmentRequest.getStatus() == null) {
						treatmentRequest.setStatus("NOT_STARTED");
					}
					Treatment treatment = new Treatment();
					TreatmentResponse treatmentResponse = new TreatmentResponse();
					BeanUtil.map(treatmentRequest, treatment);
					BeanUtil.map(treatmentRequest, treatmentResponse);
					TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
							.findById(new ObjectId(treatmentRequest.getTreatmentServiceId())).orElse(null);
					if (treatmentServicesCollection != null) {
						TreatmentService treatmentService = new TreatmentService();
						BeanUtil.map(treatmentServicesCollection, treatmentService);

						treatmentResponse.setTreatmentService(treatmentService);
						treatmentService.setDoctorId(patientTreatmentCollection.getDoctorId().toString());
						treatmentService.setLocationId(patientTreatmentCollection.getLocationId().toString());
						treatmentService.setHospitalId(patientTreatmentCollection.getHospitalId().toString());
						treatmentService.setCost(treatmentRequest.getCost());
//						addFavouritesToService(treatmentService, createdBy);
					}
					treatments.add(treatment);
					treatmentResponses.add(treatmentResponse);
				}
			patientTreatmentCollection.setTreatments(treatments);

			patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);

			response = new PatientTreatmentResponse();
			BeanUtil.map(patientTreatmentCollection, response);
			response.setTreatments(treatmentResponses);

//			pushNotificationServices.notifyUser(patientTreatmentCollection.getDoctorId().toString(),
//					"Treament Added",
//					ComponentType.TREATMENTS_REFRESH.getType(), patientTreatmentCollection.getPatientId().toString(), null);

		} catch (Exception e) {
			logger.error("Error occurred while adding or editing treatment for patients", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing treatment for patients");
		}
		return response;
	}

	private Appointment addTreatmentAppointment(AppointmentRequest appointment) {
		Appointment response = null;
		if (appointment.getAppointmentId() == null) {
			response = appointmentService.addAppointment(appointment, false);
		}
		return response;
	}

	@Override
	public TreatmentRatelistResponse addEditTreatmentRatelist(TreatmentRatelistRequest request) {
		TreatmentRatelistResponse response = null;
		TreatmentRatelistCollection reasonsCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				reasonsCollection = treatmentRatelistRepository.findById(new ObjectId(request.getId())).orElse(null);
				request.setCreatedTime(reasonsCollection.getCreatedTime());
				BeanUtil.map(request, reasonsCollection);
				reasonsCollection.setUpdatedTime(new Date());
			} else {
				reasonsCollection = new TreatmentRatelistCollection();
				BeanUtil.map(request, reasonsCollection);
				reasonsCollection.setUpdatedTime(new Date());
				reasonsCollection.setCreatedTime(new Date());
			}
			reasonsCollection = treatmentRatelistRepository.save(reasonsCollection);
			response = new TreatmentRatelistResponse();
			BeanUtil.map(reasonsCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while add edit Reasons");
			throw new BusinessException(ServiceError.Unknown, "Error while add edit Reasons");
		}
		return response;
	}

	@Override
	public Boolean deleteTreatmentRatelistById(String rateListId, Boolean isDiscarded) {
		Boolean response = false;
		TreatmentRatelistCollection dentalReasonsCollection = null;
		try {
			dentalReasonsCollection = treatmentRatelistRepository.findById(new ObjectId(rateListId)).orElse(null);
			if (dentalReasonsCollection != null) {
				dentalReasonsCollection.setDiscarded(isDiscarded);
				dentalReasonsCollection.setUpdatedTime(new Date());
				treatmentRatelistRepository.save(dentalReasonsCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Response<Object> getTreatmentRatelist(int page, int size, String searchTerm) {
		List<TreatmentRatelistResponse> responseList = new ArrayList<TreatmentRatelistResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									TreatmentRatelistCollection.class, TreatmentRatelistResponse.class)
							.getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						TreatmentRatelistCollection.class, TreatmentRatelistResponse.class).getMappedResults();
			} else {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						TreatmentRatelistCollection.class, TreatmentRatelistResponse.class).getMappedResults();
			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}
