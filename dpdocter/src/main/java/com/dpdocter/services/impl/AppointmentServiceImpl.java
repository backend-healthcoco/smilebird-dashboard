package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.AppointmentLookupResponse;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.AppointmentPaymentCollection;
import com.dpdocter.collections.AppointmentWorkFlowCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.QueueStatus;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.SMSContent;
import com.dpdocter.enums.SMSFormatType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.AppointmentPaymentRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.AppointmentWorkFlowRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.repository.ZoneRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.ConsultationSpeciality;
import com.dpdocter.response.PatientAppUsersResponse;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import com.dpdocter.beans.Zone;
import com.dpdocter.collections.ZoneCollection;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	private static Logger logger = LogManager.getLogger(AppointmentServiceImpl.class.getName());

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private LandmarkLocalityRepository landmarkLocalityRepository;

	@Autowired
	private ZoneRepository zoneRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SpecialityRepository specialityRepository;

//	@Autowired
//	private UserFavouriteService userFavouriteService;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private RegistrationService registrationService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${Appointment.timeSlotIsBooked}")
	private String timeSlotIsBooked;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private AppointmentBookedSlotRepository appointmentBookedSlotRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	// new
	@Autowired
	private PatientVisitService patientTrackService;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private SMSFormatRepository sMSFormatRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Value(value = "${mail.appointment.cancel.subject}")
	private String appointmentCancelMailSubject;

	@Value(value = "${mail.appointment.confirm.to.doctor.subject}")
	private String appointmentConfirmToDoctorMailSubject;

	@Value(value = "${mail.appointment.request.to.doctor.subject}")
	private String appointmentRequestToDoctorMailSubject;

	@Value(value = "${mail.appointment.confirm.to.patient.subject}")
	private String appointmentConfirmToPatientMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.doctor.subject}")
	private String appointmentRescheduleToDoctorMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.patient.subject}")
	private String appointmentRescheduleToPatientMailSubject;

	@Autowired
	UserRoleRepository UserRoleRepository;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	private AppointmentWorkFlowRepository appointmentWorkFlowRepository;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private AppointmentPaymentRepository appointmentPaymentRepository;

	@Value(value = "${Appointment.incorrectAppointmentId}")
	private String incorrectAppointmentId;

	@Override
	@Transactional
	public City addCity(City city) {
		try {
			CityCollection cityCollection = new CityCollection();
			city.setCreatedTime(new Date());
			city.setUpdatedTime(new Date());
			BeanUtil.map(city, cityCollection);
//			List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
//					city.getCity() + " " + ((cityCollection.getState() != null ? cityCollection.getState() : "") + " ")
//							+ (cityCollection.getCountry() != null ? cityCollection.getCountry() : ""));
//
//			if (geocodedLocations != null && !geocodedLocations.isEmpty())
//				BeanUtil.map(geocodedLocations.get(0), cityCollection);

			cityCollection = cityRepository.save(cityCollection);
			BeanUtil.map(cityCollection, city);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return city;
	}

	@Override
	@Transactional
	public Boolean activateDeactivateCity(String cityId, boolean activate) {
		try {
			CityCollection cityCollection = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (cityCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid city Id");
			}
			cityCollection.setIsActivated(activate);
			cityRepository.save(cityCollection);
		} catch (BusinessException be) {
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	@Transactional
	public Response<Object> getCities(int size, int page, String state, String searchTerm, Boolean isDiscarded) {
		List<City> responseList = new ArrayList<City>();
		Response<Object> response = new Response<Object>();

		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria.and("state").is(state);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("city").regex("^" + searchTerm, "i"),
						new Criteria("city").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//							Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))
					Aggregation.sort(Sort.Direction.DESC, "createdTime")), CityCollection.class, City.class)
					.getMappedResults().size());

			if (size > 0) {

				responseList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),
//								Aggregation.sort(new Sort(Sort.Direction.ASC, "city")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size)), CityCollection.class,
						City.class).getMappedResults();
			} else {

				responseList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//								Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))
						Aggregation.sort(Sort.Direction.DESC, "createdTime")), CityCollection.class, City.class)
						.getMappedResults();

			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public City getCity(String cityId) {
		City response = new City();
		try {
			CityCollection city = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (city != null) {
				BeanUtil.map(city, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public LandmarkLocality addLandmaklLocality(LandmarkLocality landmarkLocality) {
		CityCollection cityCollection = null;
		try {
			LandmarkLocalityCollection landmarkLocalityCollection = new LandmarkLocalityCollection();
			landmarkLocality.setCreatedTime(new Date());
			landmarkLocality.setUpdatedTime(new Date());
			BeanUtil.map(landmarkLocality, landmarkLocalityCollection);
			if (landmarkLocality.getCityId() != null) {
				cityCollection = cityRepository.findById(new ObjectId(landmarkLocality.getCityId())).orElse(null);
			}

//			List<GeocodedLocation> geocodedLocations = locationServices
//					.geocodeLocation((!DPDoctorUtils.anyStringEmpty(landmarkLocalityCollection.getLandmark())
//							? landmarkLocalityCollection.getLandmark() + ", "
//							: "")
//							+ (!DPDoctorUtils.anyStringEmpty(landmarkLocalityCollection.getLocality())
//									? landmarkLocalityCollection.getLocality() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getCity())
//									? cityCollection.getCity() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getState())
//									? cityCollection.getState() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getCountry())
//									? cityCollection.getCountry() + ", "
//									: ""));
//			
//
//			if (geocodedLocations != null && !geocodedLocations.isEmpty())
//				BeanUtil.map(geocodedLocations.get(0), landmarkLocalityCollection);

			landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
			BeanUtil.map(landmarkLocalityCollection, landmarkLocality);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return landmarkLocality;
	}

	@Override
	@Transactional
	public Zone addZone(Zone zone) {
		CityCollection cityCollection = null;
		try {
			ZoneCollection zoneCollection = new ZoneCollection();
			zone.setCreatedTime(new Date());
			zone.setUpdatedTime(new Date());
			BeanUtil.map(zone, zoneCollection);
			if (zone.getCityId() != null) {
				cityCollection = cityRepository.findById(new ObjectId(zone.getCityId())).orElse(null);
			}

			zoneCollection = zoneRepository.save(zoneCollection);
			BeanUtil.map(zoneCollection, zone);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return zone;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Clinic getClinic(String locationId) {
		Clinic response = new Clinic();
		LocationCollection localtionCollection = null;
		Location location = new Location();
		HospitalCollection hospitalCollection = null;
		Hospital hospital = new Hospital();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			localtionCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (localtionCollection == null) {
				return null;
			} else {
				BeanUtil.map(localtionCollection, location);
				response.setLocation(location);

				hospitalCollection = hospitalRepository.findById(localtionCollection.getHospitalId()).orElse(null);
				if (hospitalCollection != null) {
					BeanUtil.map(hospitalCollection, hospital);
					response.setHospital(hospital);
				}

				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationId(localtionCollection.getId());
				for (Iterator<DoctorClinicProfileCollection> iterator = doctorClinicProfileCollections
						.iterator(); iterator.hasNext();) {
					DoctorClinicProfileCollection doctorClinicProfileCollection = iterator.next();
					DoctorCollection doctorCollection = doctorRepository
							.findByUserId(doctorClinicProfileCollection.getDoctorId());
					UserCollection userCollection = userRepository.findById(doctorClinicProfileCollection.getDoctorId())
							.orElse(null);

					if (doctorCollection != null) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {
							BeanUtil.map(userCollection, doctor);
						}

						if (doctorClinicProfileCollection != null) {
							DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
							BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
							doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId().toString());
							doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

							if (doctorClinicProfile.getClinicOwnershipImageUrl() != null) {
								doctorClinicProfile.setClinicOwnershipImageUrl(
										getFinalImageURL(doctorClinicProfile.getClinicOwnershipImageUrl()));
							}

							doctor.setDoctorClinicProfile(doctorClinicProfile);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("superSpeciality"));
							doctor.setSpecialities(specialities);
						}
						doctors.add(doctor);
					}
				}
				response.setDoctors(doctors);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Lab getLab(String locationId) {
		Lab response = new Lab();
		LocationCollection localtionCollection = null;
		Location location = new Location();
		HospitalCollection hospitalCollection = null;
		Hospital hospital = new Hospital();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			localtionCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (localtionCollection == null) {
				return null;
			} else if (!localtionCollection.getIsLab()) {
				return null;
			} else {
				BeanUtil.map(localtionCollection, location);
				response.setLocation(location);

				hospitalCollection = hospitalRepository.findById(localtionCollection.getHospitalId()).orElse(null);
				if (hospitalCollection != null) {
					BeanUtil.map(hospitalCollection, hospital);
					response.setHospital(hospital);
				}

				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationId(localtionCollection.getId());
				for (Iterator<DoctorClinicProfileCollection> iterator = doctorClinicProfileCollections
						.iterator(); iterator.hasNext();) {
					DoctorClinicProfileCollection doctorClinicProfileCollection = iterator.next();
					DoctorCollection doctorCollection = doctorRepository
							.findByUserId(doctorClinicProfileCollection.getDoctorId());
					UserCollection userCollection = userRepository.findById(doctorClinicProfileCollection.getDoctorId())
							.orElse(null);

					if (doctorCollection != null) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {
							BeanUtil.map(userCollection, doctor);
						}

						if (doctorClinicProfileCollection != null) {
							DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
							BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
							doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId().toString());
							doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
							doctor.setDoctorClinicProfile(doctorClinicProfile);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("speciality"));
							doctor.setSpecialities(specialities);
						}
						doctors.add(doctor);
					}
				}
				response.setDoctors(doctors);

			
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<City> getCountries() {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("country").first("country").as("country"),
					Aggregation.project("country").andExclude("_id"), Aggregation.sort(Sort.Direction.ASC, "country"));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class,
					City.class);
			response = groupResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getCountriesCount() {
		Integer response = 0;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("country").first("country").as("country"),
					Aggregation.project("country").andExclude("_id"), Aggregation.sort(Sort.Direction.ASC, "country"));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class,
					City.class);
			response = groupResults.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Response<Object> getLandmarkLocality(int size, int page, String cityId, String searchTerm,
			Boolean isDiscarded) {
		List<LandmarkLocality> responseDataList = null;
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			Aggregation aggregation = null;
			if (!DPDoctorUtils.anyStringEmpty(cityId))
				criteria.and("cityId").is(new ObjectId(cityId));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("locality").regex("^" + searchTerm, "i"),
						new Criteria("locality").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//							Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))
					Aggregation.sort(Sort.Direction.DESC, "createdTime")), LandmarkLocalityCollection.class,
					LandmarkLocality.class).getMappedResults().size());

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Sort.Direction.ASC, "locality"))
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Sort.Direction.ASC, "locality"))
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			}
			AggregationResults<LandmarkLocality> groupResults = mongoTemplate.aggregate(aggregation,
					LandmarkLocalityCollection.class, LandmarkLocality.class);
			response.setDataList(groupResults.getMappedResults());

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<City> getStates(String country) {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("state").first("state").as("state").first("country").as("country"),
					Aggregation.project("state", "country").andExclude("_id"),
					Aggregation.sort(Sort.Direction.ASC, "state"));
			if (!DPDoctorUtils.anyStringEmpty(country))
				aggregation.match(Criteria.where("country").is(country));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class,
					City.class);
			response = groupResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getStatesCount(String country) {
		Integer response = 0;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("state").first("state").as("state").first("country").as("country"),
					Aggregation.project("state", "country").andExclude("_id"),
					Aggregation.sort(Sort.Direction.ASC, "state"));
			if (!DPDoctorUtils.anyStringEmpty(country))
				aggregation.match(Criteria.where("country").is(country));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class,
					City.class);
			response = groupResults.getMappedResults().size();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean cancelAppointment(String appointmentId) {
		Boolean response = false;
		try {
			AppointmentCollection appointment = appointmentRepository.findByAppointmentId(appointmentId);
			if (appointment != null) {
				appointment.setState(AppointmentState.CANCEL);
				appointmentRepository.save(appointment);
				response = true;
			} else {
				throw new BusinessException(ServiceError.Unknown, "Invalid apointmentId");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean verifyClinicDocument(String doctorId, Boolean isClinicOwnershipVerified, String locationId) {
		Boolean response = false;
		try {

			DoctorClinicProfileCollection doctorclinic = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));

			if (doctorclinic == null) {
				throw new BusinessException(ServiceError.Unknown, "Error while searching doctor");
			}

			if (doctorclinic.getClinicOwnershipImageUrl() != null) {
				doctorclinic.setIsClinicOwnershipVerified(isClinicOwnershipVerified);
				doctorClinicProfileRepository.save(doctorclinic);
				response = true;

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while verifying clinic documents");
			throw new BusinessException(ServiceError.Unknown, "Error while verifying clinic documents");
		}

		return response;

	}

	@Override
	public Boolean deleteCityById(String cityId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			CityCollection cityCollection = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (cityCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			cityCollection.setUpdatedTime(new Date());
			cityCollection.setIsDiscarded(isDiscarded);
			cityCollection = cityRepository.save(cityCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteLocalityById(String localityId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			LandmarkLocalityCollection landmarkLocalityCollection = landmarkLocalityRepository
					.findById(new ObjectId(localityId)).orElse(null);
			if (landmarkLocalityCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			landmarkLocalityCollection.setUpdatedTime(new Date());
			landmarkLocalityCollection.setIsDiscarded(isDiscarded);
			landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Appointment addAppointment(AppointmentRequest request, boolean isFormattedResponseRequired) {
		Appointment response = null;
		DoctorClinicProfileCollection clinicProfileCollection = null;
		try {

			ObjectId doctorId = new ObjectId(request.getDoctorId()), locationId = new ObjectId(request.getLocationId()),
					hospitalId = new ObjectId(request.getHospitalId()), patientId = null;

			patientId = registerPatientIfNotRegistered(request, doctorId, locationId, hospitalId);

			UserCollection userCollection = userRepository.findById(doctorId).orElse(null);
			LocationCollection locationCollection = locationRepository.findById(locationId).orElse(null);
			PatientCard patientCard = null;
			List<PatientCard> patientCards = null;

			if (patientId != null) {
				patientCards = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("userId").is(patientId).and("locationId").is(locationId)
										.and("hospitalId").is(hospitalId)),
								Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
						PatientCollection.class, PatientCard.class).getMappedResults();
				if (patientCards != null && !patientCards.isEmpty())
					patientCard = patientCards.get(0);
				request.setLocalPatientName(patientCard.getLocalPatientName());

				System.out.println("patientCards" + patientCards);
			}
			AppointmentCollection appointmentCollection = null;

			if (request.getCreatedBy().equals(AppointmentCreatedBy.PATIENT)) {
				System.out.println("created by patient");
				List<AppointmentCollection> appointmentCollections = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(
												Aggregation
														.match(new Criteria("locationId")
																.is(new ObjectId(request.getLocationId()))
																.andOperator(
																		new Criteria().orOperator(
																				new Criteria("doctorId")
																						.is(new ObjectId(
																								request.getDoctorId())),
																				new Criteria("doctorIds")
																						.is(new ObjectId(
																								request.getDoctorId()))
																						.and("isCalenderBlocked")
																						.is(true)),
																		new Criteria().orOperator(
																				new Criteria("time.fromTime")
																						.lte(request.getTime()
																								.getFromTime())
																						.and("time.toTime")
																						.gt(request.getTime()
																								.getToTime()),
																				new Criteria("time.fromTime")
																						.lt(request.getTime()
																								.getFromTime())
																						.and("time.toTime")
																						.gte(request.getTime()
																								.getToTime())))
																.and("fromDate").is(request.getFromDate()).and("toDate")
																.is(request.getToDate()).and("state")
																.ne(AppointmentState.CANCEL.getState()))),

								AppointmentCollection.class, AppointmentCollection.class)
						.getMappedResults();
				if (appointmentCollections != null && !appointmentCollections.isEmpty()) {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
			}

			if (userCollection != null && locationCollection != null) {

				clinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(doctorId,
						locationId);

				appointmentCollection = new AppointmentCollection();
				BeanUtil.map(request, appointmentCollection);
				appointmentCollection.setCreatedTime(new Date());

				appointmentCollection
						.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());

				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

				String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
						appointmentCollection.getTime().getFromTime() % 60);
				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				if (clinicProfileCollection != null) {
					sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				} else {
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				}

				Date _24HourDt = _24HourSDF.parse(_24HourTime);

				final String patientName = (patientCard != null && patientCard.getLocalPatientName() != null)
						? patientCard.getLocalPatientName().split(" ")[0]
						: (request.getLocalPatientName() != null ? request.getLocalPatientName().split(" ")[0] : "");
				final String appointmentId = appointmentCollection.getAppointmentId();
				final String dateTime = _12HourSDF.format(_24HourDt) + ", "
						+ sdf.format(appointmentCollection.getFromDate());
				final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();
				final String clinicName = locationCollection.getLocationName(),
						clinicContactNum = locationCollection.getClinicNumber() != null
								? locationCollection.getClinicNumber()
								: "";
				final String clinicGoogleMapShortUrl = locationCollection.getGoogleMapShortUrl() != null
						? locationCollection.getGoogleMapShortUrl()
						: "";
				final String branch = (!DPDoctorUtils.anyStringEmpty(request.getBranch())) ? request.getBranch() : "";

				if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {
					appointmentCollection.setState(AppointmentState.CONFIRM);
					appointmentCollection.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				} else {
					appointmentCollection.setIsCreatedByPatient(true);
					if (patientCard != null)
						appointmentCollection.setCreatedBy(patientCard.getLocalPatientName());
					else
						appointmentCollection.setCreatedBy(request.getLocalPatientName());

					if (clinicProfileCollection != null && clinicProfileCollection.getFacility() != null
							&& (clinicProfileCollection.getFacility().getType()
									.equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
						appointmentCollection.setState(AppointmentState.CONFIRM);
					} else {
						appointmentCollection.setState(AppointmentState.NEW);
					}

//					if (patientId != null)
//						userFavouriteService.addRemoveFavourites(request.getPatientId(), request.getDoctorId(),
//								Resource.DOCTOR.getType(), request.getLocationId(), false);
				}

				if (request.getType() != null) {
					appointmentCollection.setType(request.getType());
				} else {
					appointmentCollection.setType(appointmentCollection.getType());
				}

				if (request.getSpecialityId() != null)
					appointmentCollection.setSpecialityId(new ObjectId(request.getSpecialityId()));
				appointmentCollection.setIsAnonymousAppointment(request.getIsAnonymousAppointment());

				appointmentCollection = appointmentRepository.save(appointmentCollection);

				AppointmentPaymentCollection payment = null;
				if (request.getPaymentId() != null)
					payment = appointmentPaymentRepository.findById(new ObjectId(request.getPaymentId())).orElse(null);
				if (payment != null) {
					payment.setAppointmentId(appointmentCollection.getId());
					payment.setState(AppointmentState.CONFIRM);
					appointmentPaymentRepository.save(payment);

				}

				
				AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
				BeanUtil.map(appointmentCollection, bookedSlotCollection);

				bookedSlotCollection.setDoctorId(appointmentCollection.getDoctorId());
				bookedSlotCollection.setLocationId(appointmentCollection.getLocationId());
				bookedSlotCollection.setHospitalId(appointmentCollection.getHospitalId());
				bookedSlotCollection.setId(null);
				appointmentBookedSlotRepository.save(bookedSlotCollection);

				// sendSMS after appointment is saved

				final String id = appointmentCollection.getId().toString(),
						patientEmailAddress = patientCard != null ? patientCard.getUser().getEmailAddress() : null,
						patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
						doctorEmailAddress = userCollection.getEmailAddress(),
						doctorMobileNumber = userCollection.getMobileNumber();
				final DoctorFacility facility = (clinicProfileCollection != null)
						? clinicProfileCollection.getFacility()
						: null;

				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {

							sendAppointmentEmailSmsNotification(true, request, id, appointmentId, doctorName,
									patientName, dateTime, clinicName, clinicContactNum, patientEmailAddress,
									patientMobileNumber, doctorEmailAddress, doctorMobileNumber, facility, branch);
						} catch (MessagingException e) {
							e.printStackTrace();
						}
					}
				});

				if (appointmentCollection != null) {
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);


					if (isFormattedResponseRequired) {
						if (patientCard != null) {
							patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
							patientCard.getUser().setLocationId(patientCard.getLocationId());
							patientCard.getUser().setHospitalId(patientCard.getHospitalId());
							BeanUtil.map(patientCard.getUser(), patientCard);
							patientCard.setUserId(patientCard.getUserId());
							patientCard.setId(patientCard.getUserId());
							patientCard.setColorCode(patientCard.getUser().getColorCode());
							patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
							patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
						} else {
							patientCard = new PatientCard();
							patientCard.setLocalPatientName(request.getLocalPatientName());
						}
						response.setPatient(patientCard);
						if (userCollection != null)
							response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());
						if (locationCollection != null) {
							response.setLocationName(locationCollection.getLocationName());
							response.setClinicNumber(locationCollection.getClinicNumber());

							String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
									? locationCollection.getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
											? locationCollection.getLandmarkDetails() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
											? locationCollection.getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
											? locationCollection.getCity() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
											? locationCollection.getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
											? locationCollection.getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
											? locationCollection.getPostalCode()
											: "");

							if (address.charAt(address.length() - 2) == ',') {
								address = address.substring(0, address.length() - 2);
							}

							response.setClinicAddress(address);
							response.setLatitude(locationCollection.getLatitude());
							response.setLongitude(locationCollection.getLongitude());
						}
					}

					// for online consultation
					if (request.getType() != null && request.getType().equals(AppointmentType.ONLINE_CONSULTATION)) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollectionn = doctorClinicProfileRepository
								.findByDoctorId(doctorId);
//						
//						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollectionn)
//							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
//									"New Online appointment created.", ComponentType.ONLINE_CONSULTATION.getType(),
//									null, null);
					} else {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(locationId);
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									// "New appointment created.", ComponentType.APPOINTMENT_REFRESH.getType(),
									// null, null);
									"New appointment created.", ComponentType.APPOINTMENT_REFRESH.getType(), null,
									null);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private ObjectId registerPatientIfNotRegistered(AppointmentRequest request, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId) {
		ObjectId patientId = null;
		if (request.getPatientId() == null || request.getPatientId().isEmpty()) {

			if (DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				throw new BusinessException(ServiceError.InvalidInput,
						"Patient not selected exception frm admin add localPatientName in request");
			}
			PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
			patientRegistrationRequest.setFirstName(request.getLocalPatientName());
			patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
			patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
			patientRegistrationRequest.setDoctorId(request.getDoctorId());
			patientRegistrationRequest.setLocationId(request.getLocationId());
			patientRegistrationRequest.setHospitalId(request.getHospitalId());
			patientRegistrationRequest.setGender(request.getGender());
			patientRegistrationRequest.setDob(request.getDob());
			patientRegistrationRequest.setAge(request.getAge());
			patientRegistrationRequest.setPNUM(request.getPNUM());
			RegisteredPatientDetails patientDetails = null;
			patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
			if (patientDetails != null) {
				request.setPatientId(patientDetails.getUserId());
			}
			transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			patientId = new ObjectId(request.getPatientId());
		} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {

			patientId = new ObjectId(request.getPatientId());
			PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(patientId, locationId,
					hospitalId);
			if (patient == null) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
				patientRegistrationRequest.setFirstName(request.getLocalPatientName());
				patientRegistrationRequest.setUserId(request.getPatientId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				patientRegistrationRequest.setGender(request.getGender());
				patientRegistrationRequest.setDob(request.getDob());
				patientRegistrationRequest.setAge(request.getAge());
				patientRegistrationRequest.setPNUM(request.getPNUM());
				RegisteredPatientDetails patientDetails = registrationService
						.registerExistingPatient(patientRegistrationRequest);
				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			} else {
				List<ObjectId> consultantDoctorIds = patient.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(doctorId))
					consultantDoctorIds.add(doctorId);
				patient.setConsultantDoctorIds(consultantDoctorIds);
				patient.setUpdatedTime(new Date());
				patientRepository.save(patient);
			}
		}

		return patientId;
	}


	private void sendAppointmentEmailSmsNotification(Boolean isAddAppointment, AppointmentRequest request,
			String appointmentCollectionId, String appointmentId, String doctorName, String patientName,
			String dateTime, String clinicName, String clinicContactNum, String patientEmailAddress,
			String patientMobileNumber, String doctorEmailAddress, String doctorMobileNumber,
			DoctorFacility doctorFacility, String branch) throws MessagingException {

		/*
		 * sendAppointmentEmailSmsNotification(true, request,
		 * appointmentCollection.getId().toString(), appointmentId, doctorName,
		 * patientName, dateTime, clinicName, clinicContactNum,
		 * patientCard.getEmailAddress(), patientCard.getUser().getMobileNumber(),
		 * userCollection.getEmailAddress(), userCollection.getMobileNumber(),
		 * (clinicProfileCollection != null) ? clinicProfileCollection.getFacility() :
		 * null);
		 */

		if (isAddAppointment) {

			if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {

				// for online consultation
				if (request.getType().equals(AppointmentType.ONLINE_CONSULTATION)) {

					sendEmail(doctorName, patientName, dateTime, clinicName,
							"CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch,
							request.getConsultationType().getType());

					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT",
							patientEmailAddress, branch, request.getConsultationType().getType());

					sendPushNotification("CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
							doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch, request.getConsultationType().getType());

					sendPushNotification("CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
							patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch, request.getConsultationType().getType());

					sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT",
							request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
							request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch,
							AppointmentType.ONLINE_CONSULTATION.getType(), request.getConsultationType().getType());

					sendMsg(null, "CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum, branch,
							AppointmentType.ONLINE_CONSULTATION.getType(), request.getConsultationType().getType());

				} else {

					if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch, null);
					if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
						sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
								request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName,
								appointmentId, dateTime, doctorName, clinicName, clinicContactNum, branch, null, null);
					}
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentCollectionId, appointmentId, dateTime, doctorName, clinicName,
							clinicContactNum, branch, null);

					if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
							&& patientEmailAddress != null)
						sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
								patientEmailAddress, branch, null);
					if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()
							&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber)) {
						sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null, null);
					}
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
				}
			} else if (request.getCreatedBy().equals(AppointmentCreatedBy.PATIENT)
					&& request.getType().equals(AppointmentType.ONLINE_CONSULTATION)) {

				System.out.println("created by patient & online msg send ");

				sendEmail(doctorName, patientName, dateTime, clinicName,
						"CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch,
						request.getConsultationType().getType());

				System.out.println("patientEmailAddress" + patientEmailAddress);
				sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT",
						patientEmailAddress, branch, request.getConsultationType().getType());

				sendPushNotification("CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
						doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime, doctorName,
						clinicName, clinicContactNum, branch, request.getConsultationType().getType());

				sendPushNotification("CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
						patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime, doctorName,
						clinicName, clinicContactNum, branch, request.getConsultationType().getType());

				System.out.println("sms  CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT");
				System.out.println("patientMobileNumber" + patientMobileNumber);
				sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT",
						request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId(),
						patientMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
						clinicContactNum, branch, AppointmentType.ONLINE_CONSULTATION.getType(),
						request.getConsultationType().getType());

				System.out.println("sms   CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR");

				sendMsg(null, "CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
						request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName, appointmentId,
						dateTime, doctorName, clinicName, clinicContactNum, branch,
						AppointmentType.ONLINE_CONSULTATION.getType(), request.getConsultationType().getType());

			}

			else {
				if (doctorFacility != null
						&& (doctorFacility.getType().equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
					sendEmail(doctorName, patientName, dateTime, clinicName,
							"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch, null);
					sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName,
							appointmentId, dateTime, doctorName, clinicName, clinicContactNum, branch, null, null);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null, null);
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentCollectionId, appointmentId, dateTime, doctorName, clinicName,
							clinicContactNum, branch, null);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
				} else {
					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR",
							doctorEmailAddress, branch, null);
					sendMsg(null, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum, branch,
							null, null);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "TENTATIVE_APPOINTMENT_TO_PATIENT",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null, null);
					sendPushNotification("CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),
							doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch, null);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("TENTATIVE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
				}
			}
		} else {
			if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
				if (request.getCancelledBy().equals(AppointmentCreatedBy.DOCTOR.getType())) {
					if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", doctorEmailAddress, branch, null);

					if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
						sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
								request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
								clinicContactNum, branch, null, null);
					}

					sendPushNotification("CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
							doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch, null);

					if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
							&& patientEmailAddress != null)
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", patientEmailAddress, branch, null);

					if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()
							&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber)) {
						sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null, null);
					}

					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
				} else {
					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
								request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
								clinicContactNum, branch, null, null);
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(),
									"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getPatientId(),
									patientMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum, branch, null, null);

						sendPushNotification("CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendPushNotification("CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getPatientId(),
									patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum, branch, null);
						if (!DPDoctorUtils.anyStringEmpty(patientEmailAddress))
							sendEmail(doctorName, patientName, dateTime, clinicName,
									"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", patientEmailAddress, branch, null);
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch, null);
					}
				}
			} else {
				if (request.getCreatedBy().getType().equals(AppointmentCreatedBy.DOCTOR.getType())) {
					if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch, null);

					if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
						if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
							sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum, branch, null, null);
						else
							sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum, branch, null, null);
					}

					if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
					else
						sendPushNotification("RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
				}
				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
						&& !DPDoctorUtils.allStringsEmpty(patientEmailAddress)) {

					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
							patientEmailAddress, branch, null);
				}
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
					if (request.getState().getState().equals(AppointmentState.CONFIRM.getState())) {
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum, branch, null, null);
						else if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "RESCHEDULE_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum, branch, null, null);
					}

					if (request.getState().getState().equals(AppointmentState.CONFIRM.getState())
							&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
					else if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("RESCHEDULE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch, null);
				}
			}
		}
	}

	private void sendPushNotification(String type, String userId, String mobileNumber, String patientName,
			String appointmentCollectionId, String appointmentId, String dateTime, String doctorName, String clinicName,
			String clinicContactNum, String branch, String consultationType) {

		if (DPDoctorUtils.anyStringEmpty(patientName))
			patientName = "";
		if (DPDoctorUtils.anyStringEmpty(appointmentId))
			appointmentId = "";
		if (DPDoctorUtils.anyStringEmpty(dateTime))
			dateTime = "";
		if (DPDoctorUtils.anyStringEmpty(doctorName))
			doctorName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicName))
			clinicName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
			clinicContactNum = "";

		String text = "";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (branch != "" ? ", " + branch : "") + (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been confirmed @ " + dateTime + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your appointment with " + patientName + " has been scheduled @ " + dateTime
					+ (clinicName != "" ? " at " + clinicName : "") + (branch != "" ? ", " + branch : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			text = "Healthcoco! You have an appointment request from " + patientName + " for " + dateTime + " at "
					+ clinicName + (branch != "" ? ", " + branch : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "TENTATIVE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been sent for confirmation.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			text = "Your appointment" + " with " + patientName + " for " + dateTime + " at " + clinicName
					+ (branch != "" ? ", " + branch : "") + " has been cancelled as per your request.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			text = "Your appointment @ " + dateTime + " has been cancelled by " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Healthcoco! Your appointment" + " with " + patientName + " @ " + dateTime + " at " + clinicName
					+ (branch != "" ? ", " + branch : "") + ", has been cancelled by patient.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			text = "Your appointment for " + dateTime + " with " + doctorName
					+ " has been cancelled as per your request.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "APPOINTMENT_REMINDER_TO_PATIENT": {
			text = "You have an appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (branch != "" ? ", " + branch : "") + (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been rescheduled @ " + dateTime + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			text = "Your appointment with " + patientName + " has been rescheduled to " + dateTime + " at " + clinicName
					+ (branch != "" ? ", " + branch : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT": {
			text = "Your online " + consultationType + " consultation with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != null ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been confirmed @ " + dateTime
					+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.ONLINE_CONSULTATION.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your online " + consultationType + " consultation with " + patientName
					+ " has been scheduled @ " + dateTime + (clinicName != null ? " at " + clinicName : "")
					+ (branch != null ? ", " + branch : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.ONLINE_CONSULTATION.getType(),
					appointmentCollectionId, null);
		}
			break;

		default:
			break;
		}
	}

	private void sendEmail(String doctorName, String patientName, String dateTime, String clinicName, String type,
			String emailAddress, String branch, String consultationType) throws MessagingException {

		if (!DPDoctorUtils.anyStringEmpty(branch))
			branch = " " + branch + " ";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentConfirmToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToDoctorByPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentConfirmToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRequestToDoctorByPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentRequestToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByDoctorToDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByPatientToDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentRescheduleToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRescheduleByDoctorToDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentRescheduleToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateOnlineAppointmentEmailBody(doctorName, patientName, dateTime,
					clinicName, "confirmOnlineAppointmentToPatient.vm", branch, consultationType);
			mailService.sendEmail(emailAddress, appointmentConfirmToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateOnlineAppointmentEmailBody(doctorName, patientName, dateTime,
					clinicName, "confirmOnlineAppointmentToDoctorByPatient.vm", branch, consultationType);

			System.out.println("confirmOnlineAppointmentToDoctorByPatient");

			mailService.sendEmail(emailAddress, appointmentConfirmToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		default:
			break;
		}

	}

	private void sendMsg(String formatType, String type, String doctorId, String locationId, String hospitalId,
			String userId, String mobileNumber, String patientName, String appointmentId, String dateTime,
			String doctorName, String clinicName, String clinicContactNum, String branch, String appointmentType,
			String consultationType) {
		SMSFormatCollection smsFormatCollection = null;
		String time = "@ " + dateTime;
		if (formatType != null) {
			smsFormatCollection = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalIdAndType(
					new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId), formatType);
		}

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(new ObjectId(doctorId));
		smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
		smsTrackDetail.setLocationId(new ObjectId(locationId));
		smsTrackDetail.setType("APPOINTMENT");
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(new ObjectId(userId));
		SMS sms = new SMS();

		if (DPDoctorUtils.anyStringEmpty(patientName))
			patientName = "";
		if (DPDoctorUtils.anyStringEmpty(appointmentId))
			appointmentId = "";
		if (DPDoctorUtils.anyStringEmpty(dateTime))
			dateTime = "";
		if (DPDoctorUtils.anyStringEmpty(doctorName))
			doctorName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicName))
			clinicName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
			clinicContactNum = "";
		if (smsFormatCollection != null) {
			if (type.equalsIgnoreCase("CONFIRMED_APPOINTMENT_TO_PATIENT")
					|| type.equalsIgnoreCase("TENTATIVE_APPOINTMENT_TO_PATIENT")
					|| type.equalsIgnoreCase("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR")
					|| type.equalsIgnoreCase("APPOINTMENT_REMINDER_TO_PATIENT")
					|| type.equalsIgnoreCase("RESCHEDULE_APPOINTMENT_TO_PATIENT")) {
				if (!smsFormatCollection.getContent().contains(SMSContent.CLINIC_NAME.getContent())
						|| clinicName == null)
					clinicName = "";
				if (!smsFormatCollection.getContent().contains(SMSContent.CLINIC_CONTACT_NUMBER.getContent())
						|| clinicContactNum == null)
					clinicContactNum = "";
				if (!smsFormatCollection.getContent().contains(SMSContent.BRANCH.getContent()) || branch == null)
					branch = "";
			}
		}
		String text = "";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (branch != null ? ", " + branch : "") + (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been confirmed @ " + dateTime + ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307161191156377476");
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your appointment with " + patientName + " has been scheduled @ " + dateTime
					+ (clinicName != "" ? " at " + clinicName : "") + (branch != null ? ", " + branch : "") + ".";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307161191335595866");
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			text = "Healthcoco! You have an appointment request from " + patientName + " for " + dateTime + " at "
					+ clinicName + (branch != "" ? ", " + branch : "") + ".";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "TENTATIVE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != null ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been sent for confirmation. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			text = "Your appointment" + " with " + patientName + " for " + dateTime + " at " + clinicName
					+ (branch != "" ? ", " + branch : "") + " has been cancelled as per your request.";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307161191475616939");
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			text = "Your appointment @ " + dateTime + " has been cancelled by " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ ". Request you to book again. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307161522503253641");
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Healthcoco! Your appointment" + " with " + patientName + " @ " + dateTime + " at " + clinicName
					+ (branch != "" ? ", " + branch : "") + ", has been cancelled by patient.";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			text = "Your appointment " + time + " with " + doctorName
					+ " has been cancelled as per your request. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307161191475616939");
		}
			break;

		case "APPOINTMENT_REMINDER_TO_PATIENT": {
			text = "You have an appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ". Download Healthcoco App- "
					+ patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (branch != "" ? ", " + branch : "") + (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been rescheduled @ " + dateTime + ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307161191460900446");
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			text = "Your appointment with " + patientName + " has been rescheduled to " + dateTime + " at " + clinicName
					+ (branch != "" ? ", " + branch : "") + ".";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307161522509431392");
		}
			break;

		case "CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT": {

			System.out.println("enter in CONFIRMED_ONLINE_APPOINTMENT_TO_PATIENT");
			text = "Your online " + consultationType + " consultation with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "") + (branch != null ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been confirmed @ " + dateTime
					+ ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307161562814636124");
		}
			break;

		case "CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR": {
			System.out.println("enter in CONFIRMED_ONLINE_APPOINTMENT_TO_DOCTOR");

			text = "Healthcoco! Your online " + consultationType + " consultation with " + patientName
					+ " has been scheduled @ " + dateTime + (clinicName != "" ? " at " + clinicName : "")
					+ (branch != null ? ", " + branch : "") + ".";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307161649367351507");
		}
			break;

		default:
			break;
		}

		sms.setSmsText(text);

		SMSAddress smsAddress = new SMSAddress();
		System.out.println("mobileNumber" + mobileNumber);
		smsAddress.setRecipient(mobileNumber);
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		sMSServices.sendSMS(smsTrackDetail, true);

		System.out.println("SMS APPOINTment send from admin");
	}

	@Override
	public Integer getUsersCount(int size, int page, String searchTerm, String mobileNumber) {

		Integer response = null;
		try {
			Criteria criteria = new Criteria("signedUp").is(true);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (mobileNumber != null) {
				criteria.and("mobileNumber").is(mobileNumber);
			}
			response = (int) mongoTemplate.count(new Query(criteria), UserCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while patients " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<PatientAppUsersResponse> getUsers(int size, int page, String searchTerm, String mobileNumber) {
		List<PatientAppUsersResponse> response = null;
		try {
//			Criteria criteria = new Criteria("signedUp").is(true);
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (mobileNumber != null) {
				criteria.and("mobileNumber").is(mobileNumber);
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
			System.out.println("Aggregation:" + aggregation);
			response = mongoTemplate.aggregate(aggregation, UserCollection.class, PatientAppUsersResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patients " + e.getMessage());

		}
		return response;
	}

	@Override
	@Transactional
	public Response<Object> getUsersNew(int size, int page, String searchTerm, String mobileNumber,
			Boolean isDentalChainPatient) {

		Response<Object> response = new Response<Object>();
		List<PatientAppUsersResponse> patientResponse = null;
		try {
//			Criteria criteria = new Criteria("signedUp").is(true);
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (mobileNumber != null) {
				criteria.and("mobileNumber").is(mobileNumber);
			}

			if (isDentalChainPatient)
				criteria.and("isDentalChainPatient").is(isDentalChainPatient);

//			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
//					new BasicDBObject("_id", "$_id")
//					.append("title", "$title")		
//					.append("firstName", "$patient.firstName")		
//					.append("lastName", "$lastName")
//					.append("middleName", "$middleName")
//					.append("emailAddress", "$emailAddress")					
//					.append("countryCode", "$countryCode")
//					.append("mobileNumber", "$mobileNumber")
//					.append("colorCode", "$colorCode")	
//					.append("createdTime", "$createdTime")	
//					.append("updatedTime", "$updatedTime")	
//					));
//
//			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//					new BasicDBObject("_id", "$_id")
//					.append("title", new BasicDBObject("$first", "$title"))
//					.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
//					.append("lastName", new BasicDBObject("$first", "$lastName"))
//					.append("middleName", new BasicDBObject("$first", "$middleName"))
//					.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//					.append("countryCode", new BasicDBObject("$first", "$countryCode"))
//					.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
//					.append("colorCode", new BasicDBObject("$first", "$colorCode"))
//					.append("createdTime",  new BasicDBObject("$first", "$createdTime"))
//					.append("updatedTime",  new BasicDBObject("$first", "$updatedTime"))
//					));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size), Aggregation.lookup("user_cl", "userId", "_id", "patient"),
						Aggregation.unwind("patient"),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
										.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
										.append("lastName", new BasicDBObject("$first", "$lastName"))
										.append("middleName", new BasicDBObject("$first", "$middleName"))
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("countryCode", new BasicDBObject("$first", "$countryCode"))
										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
										.append("colorCode", new BasicDBObject("$first", "$colorCode"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$updatedTime")))),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "patient"), Aggregation.unwind("patient"),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
										.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
										.append("lastName", new BasicDBObject("$first", "$lastName"))
										.append("middleName", new BasicDBObject("$first", "$middleName"))
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("countryCode", new BasicDBObject("$first", "$countryCode"))

										.append("mobileNumber", new BasicDBObject("$first", "$patient.mobileNumber"))

										.append("colorCode", new BasicDBObject("$first", "$colorCode"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$updatedTime")))),

//						new CustomAggregationOperation(new Document("$project",
//								new BasicDBObject("_id", "$_id")
//								.append("title", "$title")		
//								.append("firstName", "$firstName")		
//								.append("lastName", "$lastName")
//								.append("middleName", "$middleName")
//								.append("emailAddress", "$emailAddress")					
//								.append("countryCode", "$countryCode")
//								.append("mobileNumber", "$mobileNumber")
//								.append("colorCode", "$colorCode")	
//								.append("createdTime", "$createdTime")	
//								.append("updatedTime", "$updatedTime")	
//								)),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			}
			patientResponse = mongoTemplate
					.aggregate(aggregation, PatientCollection.class, PatientAppUsersResponse.class)

					.getMappedResults();
			response.setDataList(patientResponse);
			response.setCount(patientResponse.size());
			System.out.println("aggregation" + aggregation);
		} catch (BusinessException e) {
			logger.error("Error while getting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patients " + e.getMessage());

		}
		return response;
	}


	@Transactional
	public Boolean checkToday(int dayOfDate, int yearOfDate, String timeZone) {
		Boolean status = false;
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		if (yearOfDate == localCalendar.get(Calendar.YEAR) && dayOfDate == localCalendar.get(Calendar.DAY_OF_YEAR)) {
			status = true;
		}
		return status;
	}

	@Transactional
	public Integer getMinutesOfDay(Date date) {
		DateTime dateTime = new DateTime(new Date(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
		Integer currentMinute = dateTime.getMinuteOfDay();
		return currentMinute;
	}

	@Override
	@Transactional
	public Response<Appointment> getAppointments(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String status, String sortBy, String fromTime,
			String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb, Boolean discarded, String branch,
			Boolean isAnonymousAppointment, String type) {
		Response<Appointment> response = new Response<Appointment>();
		List<Appointment> appointments = null;
		// PatientTreatmentResponse patientTreatmentResponse=null;

		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);
//online consultation
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			} else {
				criteria.and("type").is(AppointmentType.APPOINTMENT.getType());
			}

			criteria.and("updatedTime").gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").is(false);

			// Criteria criteria = new Criteria("updatedTime")
			// .gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").ne(true);

			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (isAnonymousAppointment != null)
				criteria.and("isAnonymousAppointment").is(isAnonymousAppointment);

			if (!DPDoctorUtils.anyStringEmpty(branch))
				criteria.and("branch").is(branch);
//			if (doctorId != null && !doctorId.isEmpty()) {
//				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
//				for (String id : doctorId)
//					doctorObjectIds.add(new ObjectId(id));
//				criteria.and("doctorId").in(doctorObjectIds);
//			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("status").is(status.toUpperCase()).and("state").ne(AppointmentState.CANCEL.getState());

			if (!discarded)
				criteria.and("discarded").is(discarded);

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime = null, toDateTime = null;
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				// DateTime
				fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
				System.out.println(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				// DateTime
				toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				System.out.println(toDateTime);

				criteria.and("toDate").lte(toDateTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(fromTime))
				criteria.and("time.fromTime").is(Integer.parseInt(fromTime));

			if (!DPDoctorUtils.anyStringEmpty(toTime))
				criteria.and("time.toTime").is(Integer.parseInt(toTime));
			List<AppointmentLookupResponse> appointmentLookupResponses = null;

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime"));

			if (!DPDoctorUtils.anyStringEmpty(status)) {
				if (status.equalsIgnoreCase(QueueStatus.SCHEDULED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "time.fromTime"));
				} else if (status.equalsIgnoreCase(QueueStatus.WAITING.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedInAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.ENGAGED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "engagedAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.CHECKED_OUT.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedOutAt"));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			}

			Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if (count != null && count > 0) {
				response.setCount(count);

				if (isWeb)
					appointments = getAppointmentsForWeb(criteria, sortOperation, page, size, appointments,
							appointmentLookupResponses);
				else {
					if (size > 0) {
						Aggregation aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
										Aggregation.unwind("doctor"),
										Aggregation.lookup("location_cl", "locationId", "_id", "location"),
										Aggregation.unwind("location"),
										// new
										// Aggregation.lookup("patient_treatment_cl", "doctorId","treatmentServiceId" ,
										// "treatmentService"),
										// Aggregation.unwind("treatmentService"),
										Aggregation.lookup("patient_treatment_cl", "doctorId", "_id", "treatments"),
										Aggregation.unwind("treatments", true),

										Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
										new CustomAggregationOperation(new Document("$unwind",
												new BasicDBObject("path", "$patientCard")
														.append("preserveNullAndEmptyArrays", true))),
										new CustomAggregationOperation(new Document("$redact", new BasicDBObject(
												"$cond",
												new BasicDBObject("if",
														new BasicDBObject("$eq",
																Arrays.asList("$patientCard.locationId",
																		"$locationId"))).append("then", "$$KEEP")
																				.append("else", "$$PRUNE")))),

										Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
										Aggregation.unwind("patientCard.user"), sortOperation,
										Aggregation.skip((page) * size), Aggregation.limit(size))
								.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

						System.out.println("Aggregation" + aggregation);

						appointmentLookupResponses = mongoTemplate
								.aggregate(aggregation, AppointmentCollection.class, AppointmentLookupResponse.class)
								.getMappedResults();
					} else {
						Aggregation aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
										Aggregation.unwind("doctor"),

										// new
										// Aggregation.lookup("patient_treatment_cl", "doctorId","treatmentServiceId" ,
										// "treatmentService"),
										// Aggregation.unwind("treatmentService"),
										Aggregation.lookup("patient_treatment_cl", "doctorId", "_id", "treatments"),
										Aggregation.unwind("treatments", true),

										Aggregation.lookup("location_cl", "locationId", "_id", "location"),
										Aggregation.unwind("location"),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
										new CustomAggregationOperation(new Document("$unwind",
												new BasicDBObject("path", "$patientCard")
														.append("preserveNullAndEmptyArrays", true))),
										new CustomAggregationOperation(new Document("$redact", new BasicDBObject(
												"$cond",
												new BasicDBObject("if",
														new BasicDBObject("$eq",
																Arrays.asList("$patientCard.locationId",
																		"$locationId"))).append("then", "$$KEEP")
																				.append("else", "$$PRUNE")))),

										Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
										Aggregation.unwind("patientCard.user"), sortOperation)
								.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

						System.out.println("Aggregation" + aggregation);
						appointmentLookupResponses = mongoTemplate
								.aggregate(aggregation, AppointmentCollection.class, AppointmentLookupResponse.class)
								.getMappedResults();

					}

					if (appointmentLookupResponses != null && !appointmentLookupResponses.isEmpty()) {
						appointments = new ArrayList<Appointment>();

						for (AppointmentLookupResponse collection : appointmentLookupResponses) {
							Appointment appointment = new Appointment();
							// new
							// ObjectId appointmentId = new ObjectId(collection.getAppointmentId());

							PatientCard patientCard = null;
							// if
							// (collection.getType().getType().equals(AppointmentType.APPOINTMENT.getType()))
							// {
							patientCard = collection.getPatientCard();
							if (patientCard != null) {
								patientCard.setId(patientCard.getUserId());

								if (patientCard.getUser() != null) {
									patientCard.setColorCode(patientCard.getUser().getColorCode());
									patientCard.setMobileNumber(patientCard.getUser().getMobileNumber());
								}
								// patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
								patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));

							}
							// }
							BeanUtil.map(collection, appointment);

							// patientTreatmentResponse =
							// getPatientTreatmentAppointmentById(appointmentId.toString());

							appointment.setPatient(patientCard);
							appointment.setLocalPatientName(patientCard.getLocalPatientName());
							if (collection.getDoctor() != null) {
								appointment.setDoctorName(collection.getDoctor().getTitle() + " "
										+ collection.getDoctor().getFirstName());
							}
							// new
//						if(collection.getPatientTreatmentResponse()!=null) {
//							
//						}
							// appointment.setPatientTreatmentResponse(patientTreatmentResponse);

							if (collection.getLocation() != null) {
								appointment.setLocationName(collection.getLocation().getLocationName());
								appointment.setClinicNumber(collection.getLocation().getClinicNumber());

								String address = (!DPDoctorUtils
										.anyStringEmpty(collection.getLocation().getStreetAddress())
												? collection.getLocation().getStreetAddress() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
												? collection.getLocation().getLandmarkDetails() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
												? collection.getLocation().getLocality() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
												? collection.getLocation().getCity() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
												? collection.getLocation().getState() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
												? collection.getLocation().getCountry() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
												? collection.getLocation().getPostalCode()
												: "");

								if (address.charAt(address.length() - 2) == ',') {
									address = address.substring(0, address.length() - 2);
								}

								appointment.setClinicAddress(address);
								appointment.setLatitude(collection.getLocation().getLatitude());
								appointment.setLongitude(collection.getLocation().getLongitude());
							}

							// response.setData(appointment);
							appointments.add(appointment);

						}
					}
				}

				response.setDataList(appointments);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
	
	
	private List<Appointment> getAppointmentsForWeb(Criteria criteria, SortOperation sortOperation, int page, int size,
			List<Appointment> response, List<AppointmentLookupResponse> appointmentLookupResponses) {

		CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
				new BasicDBObject("_id", "$_id").append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("patientId", "$patientId").append("time", "$time")
						.append("state", "$state").append("isRescheduled", "$isRescheduled")
						.append("fromDate", "$fromDate").append("toDate", "$toDate")
						.append("appointmentId", "$appointmentId").append("subject", "$subject")
						.append("explanation", "$explanation").append("type", "$type")
						.append("isCalenderBlocked", "$isCalenderBlocked")
						.append("isFeedbackAvailable", "$isFeedbackAvailable").append("isAllDayEvent", "$isAllDayEvent")
						.append("doctorName",
								new BasicDBObject("$concat", Arrays.asList("$doctor.title", " ", "$doctor.firstName")))
						.append("cancelledBy", "$cancelledBy").append("notifyPatientBySms", "$notifyPatientBySms")
						.append("notifyPatientByEmail", "$notifyPatientByEmail")
						.append("notifyDoctorBySms", "$notifyDoctorBySms")
						.append("notifyDoctorByEmail", "$notifyDoctorByEmail").append("visitId", "$visitId")
						.append("status", "$status").append("waitedFor", "$waitedFor")
						.append("engagedFor", "$engagedFor").append("engagedAt", "$engagedAt")
						.append("checkedInAt", "$checkedInAt").append("checkedOutAt", "$checkedOutAt")
						.append("count", "$count").append("category", "$category").append("branch", "$branch")
						.append("treatmentFields", "$treatmentFields")
//						.append("patientTreatmentResponse","$patientTreatmentResponse")
						.append("cancelledByProfile", "$cancelledByProfile")
						.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("isCreatedByPatient", "$isCreatedByPatient")
						.append("patient._id", "$patientCard.userId").append("patient.userId", "$patientCard.userId")
						.append("patient.localPatientName", "$patientCard.localPatientName")
						.append("patient.PID", "$patientCard.PID").append("patient.PNUM", "$patientCard.PNUM")
						.append("patient.imageUrl", new BasicDBObject("$cond",
								new BasicDBObject("if",
										new BasicDBObject("eq", Arrays.asList("$patientCard.imageUrl", null)))
												.append("then",
														new BasicDBObject("$concat",
																Arrays.asList(imagePath, "$patientCard.imageUrl")))
												.append("else", null)))
						.append("patient.thumbnailUrl", new BasicDBObject("$cond",
								new BasicDBObject("if",
										new BasicDBObject("eq", Arrays.asList("$patientCard.thumbnailUrl", null)))
												.append("then",
														new BasicDBObject("$concat",
																Arrays.asList(imagePath, "$patientCard.thumbnailUrl")))
												.append("else", null)))
						.append("patient.mobileNumber", "$patientUser.mobileNumber")
						.append("patient.colorCode", "$patientUser.colorCode")));

		CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("time", new BasicDBObject("$first", "$time"))
						.append("state", new BasicDBObject("$first", "$state"))
						.append("isRescheduled", new BasicDBObject("$first", "$isRescheduled"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("toDate", new BasicDBObject("$first", "$toDate"))
						.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
						.append("subject", new BasicDBObject("$first", "$subject"))
						.append("explanation", new BasicDBObject("$first", "$explanation"))
						.append("type", new BasicDBObject("$first", "$type"))
						.append("isCalenderBlocked", new BasicDBObject("$first", "$isCalenderBlocked"))
						.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
						.append("isAllDayEvent", new BasicDBObject("$first", "$isAllDayEvent"))
						.append("doctorName", new BasicDBObject("$first", "$doctorName"))
						.append("cancelledBy", new BasicDBObject("$first", "$cancelledBy"))
						.append("notifyPatientBySms", new BasicDBObject("$first", "$notifyPatientBySms"))
						.append("notifyPatientByEmail", new BasicDBObject("$first", "$notifyPatientByEmail"))
						.append("notifyDoctorBySms", new BasicDBObject("$first", "$notifyDoctorBySms"))
						.append("notifyDoctorByEmail", new BasicDBObject("$first", "$notifyDoctorByEmail"))
						.append("visitId", new BasicDBObject("$first", "$visitId"))
						.append("status", new BasicDBObject("$first", "$status"))
						.append("waitedFor", new BasicDBObject("$first", "$waitedFor"))
						.append("engagedFor", new BasicDBObject("$first", "$engagedFor"))
						.append("engagedAt", new BasicDBObject("$first", "$engagedAt"))
						.append("checkedInAt", new BasicDBObject("$first", "$checkedInAt"))
						.append("checkedOutAt", new BasicDBObject("$first", "$checkedOutAt"))
						.append("count", new BasicDBObject("$first", "$count"))
						.append("category", new BasicDBObject("$first", "$category"))
						.append("branch", new BasicDBObject("$first", "$branch"))
						.append("treatmentFields", new BasicDBObject("$first", "$treatmentFields"))
//						.append("patientTreatmentResponse", new BasicDBObject("$first","$patientTreatmentResponse"))
						.append("cancelledByProfile", new BasicDBObject("$first", "$cancelledByProfile"))
						.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("isCreatedByPatient", new BasicDBObject("$first", "$isCreatedByPatient"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))
						.append("patient", new BasicDBObject("$first", "$patient"))));

		if (size > 0) {
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),

					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),

//											Aggregation.lookup("patient_treatment_cl", "appointmentId","appointmentId", "patientTreatmentResponse"),
//											Aggregation.unwind("patientTreatmentResponse",true),
//					//new											
//											Aggregation.lookup("patient_treatment_cl", "doctorId","_id", "treatments"),
//											Aggregation.unwind("treatments",true),
//											
					Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$redact", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq", Arrays.asList("$patientCard.locationId", "$locationId")))
											.append("then", "$$KEEP").append("else", "$$PRUNE")))),

					Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"), Aggregation.unwind("patientUser"),
					projectOperation, groupOperation, sortOperation, Aggregation.skip((page) * size),
					Aggregation.limit(size))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
					AppointmentCollection.class, Appointment.class).getMappedResults();
		} else {
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),

//											Aggregation.lookup("patient_treatment_cl", "appointmentId","appointmentId", "patientTreatmentResponse"),
//											Aggregation.unwind("patientTreatmentResponse",true),
//					//new						
//											Aggregation.lookup("patient_treatment_cl", "doctorId","doctorId", "treatments"),
//											Aggregation.unwind("treatments",true),
//					//new					
//											
					Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$redact", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq", Arrays.asList("$patientCard.locationId", "$locationId")))
											.append("then", "$$KEEP").append("else", "$$PRUNE")))),

					Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"), Aggregation.unwind("patientUser"),
					projectOperation, groupOperation, sortOperation)
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
					AppointmentCollection.class, Appointment.class).getMappedResults();
		}
		return response;
	}

	public PatientTreatmentResponse getPatientTreatmentAppointmentById(String appointmentId) {
		PatientTreatmentResponse response = null;
		try {
			CustomAggregationOperation projectList = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("patientId", "$patientId").append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId").append("doctorId", "$doctorId")
							.append("visitId", "$patientVisit._id").append("uniqueEmrId", "$uniqueEmrId")
							.append("totalCost", "$totalCost").append("totalDiscount", "$totalDiscount")
							.append("grandTotal", "$grandTotal").append("discarded", "$discarded")
							.append("inHistory", "$inHistory").append("appointmentId", "$appointmentId")
							.append("time", "$time").append("fromDate", "$fromDate")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
							.append("createdBy", "$createdBy")
							.append("treatments.treatmentService", "$treatmentService")
							.append("treatments.treatmentServiceId", "$treatments.treatmentServiceId")
							.append("treatments.doctorId", "$treatments.doctorId")
							.append("treatments.doctorName",
									new BasicDBObject("$concat",
											Arrays.asList("$treatmentDoctor.title", " ", "$treatmentDoctor.firstName")))
							.append("treatments.status", "$treatments.status")
							.append("treatments.cost", "$treatments.cost").append("treatments.note", "$treatments.note")
							.append("treatments.discount", "$treatments.discount")
							.append("treatments.finalCost", "$treatments.finalCost")
							.append("treatments.quantity", "$treatments.quantity")
							.append("treatments.treatmentFields", "$treatments.treatmentFields")
							.append("appointmentRequest", "$appointmentRequest")));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation
							.match(new Criteria("appointmentId").is(appointmentId).and("isPatientDiscarded").ne(true)),
					Aggregation.unwind("treatments", "arrayIndex"),

					Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
							"treatmentService"),
					Aggregation.unwind("treatmentService"),
					Aggregation.lookup("patient_visit_cl", "_id", "treatmentId", "patientVisit"),
					Aggregation.unwind("patientVisit"),

					Aggregation.lookup("user_cl", "treatments.doctorId", "_id", "treatmentDoctor"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$treatmentDoctor").append("preserveNullAndEmptyArrays", true))),
					projectList,

					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$_id")
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("totalCost", new BasicDBObject("$first", "$totalCost"))
									.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
									.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))
									.append("treatments", new BasicDBObject("$push", "$treatments")))));

			AggregationResults<PatientTreatmentResponse> groupResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, PatientTreatmentResponse.class);
			List<PatientTreatmentResponse> patientDetailsresponse = groupResults.getMappedResults();
			response = (patientDetailsresponse != null && !patientDetailsresponse.isEmpty())
					? patientDetailsresponse.get(0)
					: null;
		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public Appointment updateAppointment(AppointmentRequest request, Boolean isStatusChange) {
		Appointment response = null;

		try {
//			if (DPDoctorUtils.anyStringEmpty(request.getType())) {
//				
//				request.setType(AppointmentType.APPOINTMENT);
//			}

			if (request.getCreatedBy() != null) {

				request.setCreatedBy(AppointmentCreatedBy.PATIENT);
			}
			ObjectId doctorId = new ObjectId(request.getDoctorId()), locationId = new ObjectId(request.getLocationId()),
					hospitalId = new ObjectId(request.getHospitalId()), patientId = null;

			DoctorCollection userCollection = doctorRepository.findByUserId(doctorId);
			LocationCollection locationCollection = locationRepository.findById(locationId).orElse(null);

			AppointmentLookupResponse appointmentLookupResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("appointmentId").is(request.getAppointmentId())),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location")),
					AppointmentCollection.class, AppointmentLookupResponse.class).getUniqueMappedResult();
			if (appointmentLookupResponse != null) {
				AppointmentCollection appointmentCollection = new AppointmentCollection();
				BeanUtil.map(appointmentLookupResponse, appointmentCollection);
				PatientCard patientCard = null;
				List<PatientCard> patientCards = null;

				patientId = registerPatientIfNotRegistered(request, doctorId, locationId, hospitalId);

//				if (!DPDoctorUtils.allStringsEmpty(request.getPatientId())) {
//					patientCards = mongoTemplate.aggregate(
//							Aggregation.newAggregation(
//									Aggregation.match(new Criteria("userId").is(new ObjectId(request.getPatientId()))
//											.and("locationId").is(new ObjectId(request.getLocationId()))
//											.and("hospitalId").is(new ObjectId(request.getHospitalId()))),
//									Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
//							PatientCollection.class, PatientCard.class).getMappedResults();
//					if (patientCards != null && !patientCards.isEmpty())
//						patientCard = patientCards.get(0);
//					appointmentCollection.setLocalPatientName(patientCard.getLocalPatientName());
//				} else {
//					appointmentCollection.setLocalPatientName(request.getLocalPatientName());
//				}

				final String doctorName = appointmentLookupResponse.getDoctor().getTitle() + " "
						+ appointmentLookupResponse.getDoctor().getFirstName();

				if (!isStatusChange) {
					AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
					BeanUtil.map(appointmentLookupResponse, appointmentWorkFlowCollection);
					appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);
					System.out.println("workflow data :" + appointmentWorkFlowCollection);
					appointmentCollection.setState(request.getState());

					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						if (request.getCancelledBy() != null) {
							if (request.getCancelledBy().equalsIgnoreCase(AppointmentCreatedBy.DOCTOR.getType())) {
								appointmentCollection.setCancelledBy(appointmentLookupResponse.getDoctor().getTitle()
										+ " " + appointmentLookupResponse.getDoctor().getFirstName());
								appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.DOCTOR.getType());
							} else {
								appointmentCollection.setCancelledBy(patientCard.getLocalPatientName());
								appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.PATIENT.getType());
							}
						}
						AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
								.findByAppointmentId(request.getAppointmentId());
						if (bookedSlotCollection != null)
							appointmentBookedSlotRepository.delete(bookedSlotCollection);
					} else {
						if (request.getState().getState().equals(AppointmentState.RESCHEDULE.getState())) {
							appointmentCollection.setFromDate(request.getFromDate());
							appointmentCollection.setToDate(request.getToDate());
							appointmentCollection.setTime(request.getTime());
							appointmentCollection.setIsRescheduled(true);
							appointmentCollection.setState(AppointmentState.CONFIRM);
							appointmentCollection.setDoctorId(userCollection.getUserId());
							appointmentCollection.setHospitalId(hospitalId);
							appointmentCollection.setLocationId(locationCollection.getId());
							AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
									.findByAppointmentId(request.getAppointmentId());
							if (bookedSlotCollection != null) {
								bookedSlotCollection.setDoctorId(new ObjectId(request.getDoctorId()));
								bookedSlotCollection.setLocationId(new ObjectId(request.getLocationId()));
								bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
								bookedSlotCollection.setToDate(appointmentCollection.getToDate());
								bookedSlotCollection.setTime(request.getTime());
								bookedSlotCollection.setUpdatedTime(new Date());
								appointmentBookedSlotRepository.save(bookedSlotCollection);
							}
						}

						if (!request.getDoctorId().equalsIgnoreCase(appointmentLookupResponse.getDoctorId())) {
							appointmentCollection.setDoctorId(new ObjectId(request.getDoctorId()));
							User drCollection = mongoTemplate.aggregate(
									Aggregation.newAggregation(Aggregation
											.match(new Criteria("id").is(appointmentCollection.getDoctorId()))),
									UserCollection.class, User.class).getUniqueMappedResult();
							appointmentLookupResponse.setDoctor(drCollection);
						}
					}

					DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdAndLocationId(appointmentCollection.getDoctorId(),
									appointmentCollection.getLocationId());

					appointmentCollection.setNotifyDoctorByEmail(true);
					appointmentCollection.setNotifyDoctorBySms(true);
					appointmentCollection.setNotifyPatientByEmail(true);
					appointmentCollection.setNotifyPatientBySms(true);
					appointmentCollection.setUpdatedTime(new Date());
					// appointmentCollection.setTreatmentFields(request.getTreatmentFields());

					appointmentCollection.setSpecialityId(new ObjectId(request.getSpecialityId()));
					appointmentCollection.setIsAnonymousAppointment(request.getIsAnonymousAppointment());

					if (request.getType() != null) {
						appointmentCollection.setType(request.getType());
					} else {
						appointmentCollection.setType(appointmentCollection.getType());
					}
					appointmentCollection = appointmentRepository.save(appointmentCollection);

//					AppointmentPaymentCollection payment=appointmentPaymentRepository.findById(new ObjectId(request.getPaymentId())).orElse(null);
//					if(payment !=null)
//					{
//						payment.setAppointmentId(appointmentCollection.getId());
//						payment.setState(AppointmentState.CONFIRM);
//						appointmentPaymentRepository.save(payment);
//						
//					}
					PatientVisitCollection patientVisitCollection = patientVisitRepository
							.findByAppointmentId(request.getAppointmentId());

					if (patientVisitCollection != null) {
						patientVisitCollection.setDoctorId(doctorId);
						patientVisitCollection.setLocationId(locationId);
						patientVisitCollection.setHospitalId(hospitalId);
						patientVisitCollection.setUpdatedTime(new Date());
						patientVisitRepository.save(patientVisitCollection);

					}

//							patientVisitService.updateAppointmentTime(appointmentCollection.getVisitId(), null, null,
//									null);
//						else
//							patientVisitService.updateAppointmentTime(appointmentCollection.getVisitId(),
//									appointmentCollection.getAppointmentId(), appointmentCollection.getTime(),
//									appointmentCollection.getFromDate());

					SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
					String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
							appointmentCollection.getTime().getFromTime() % 60);
					String _24HourToTime = String.format("%02d:%02d", appointmentCollection.getTime().getToTime() / 60,
							appointmentCollection.getTime().getToTime() % 60);
					;
					SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
					SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
					if (clinicProfileCollection != null) {
						sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
						_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
						_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					} else {
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
						_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					}

					Date _24HourDt = _24HourSDF.parse(_24HourTime);
					Date _24HourToDt = _24HourSDF.parse(_24HourToTime);

					final String patientName = (patientCard != null && patientCard.getLocalPatientName() != null)
							? patientCard.getLocalPatientName().split(" ")[0]
							: (request.getLocalPatientName() != null ? request.getLocalPatientName().split(" ")[0]
									: "");
					final String appointmentId = appointmentCollection.getAppointmentId();
					final String dateTime = _12HourSDF.format(_24HourDt) + ", "
							+ sdf.format(appointmentCollection.getFromDate());
					final String branch = appointmentLookupResponse.getBranch();
					final String clinicName = appointmentLookupResponse.getLocation().getLocationName();
					final String clinicContactNum = appointmentLookupResponse.getLocation().getClinicNumber() != null
							? appointmentLookupResponse.getLocation().getClinicNumber()
							: "";

					// sendSMS after appointment is saved
					final String id = appointmentCollection.getId().toString(),
							patientEmailAddress = patientCard != null ? patientCard.getEmailAddress() : null,
							patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
							doctorEmailAddress = appointmentLookupResponse.getDoctor().getEmailAddress(),
							doctorMobileNumber = appointmentLookupResponse.getDoctor().getMobileNumber();
					final DoctorFacility facility = (clinicProfileCollection != null)
							? clinicProfileCollection.getFacility()
							: null;

					Executors.newSingleThreadExecutor().execute(new Runnable() {
						@Override
						public void run() {
							try {
								sendAppointmentEmailSmsNotification(false, request, id, appointmentId, doctorName,
										patientName, dateTime, clinicName, clinicContactNum, patientEmailAddress,
										patientMobileNumber, doctorEmailAddress, doctorMobileNumber, facility, branch);
							} catch (MessagingException e) {
								e.printStackTrace();
							}
						}
					});
				} else if (request.getStatus() != null) {

					appointmentCollection.setCheckedInAt(request.getCheckedInAt());
					appointmentCollection.setEngagedAt(request.getEngagedAt());
					appointmentCollection.setCheckedOutAt(request.getCheckedOutAt());

					if (request.getStatus().name().equalsIgnoreCase(QueueStatus.SCHEDULED.name())) {
						appointmentCollection.setCheckedInAt(0);
						appointmentCollection.setEngagedAt(0);
						appointmentCollection.setWaitedFor(0);
						appointmentCollection.setCheckedOutAt(0);
						appointmentCollection.setEngagedFor(0);
					} else if (request.getStatus().name().equalsIgnoreCase(QueueStatus.ENGAGED.name())) {
						appointmentCollection.setWaitedFor(
								appointmentCollection.getEngagedAt() - appointmentCollection.getCheckedInAt());
					} else if (request.getStatus().name().equalsIgnoreCase(QueueStatus.CHECKED_OUT.name())) {
						appointmentCollection.setEngagedFor(
								appointmentCollection.getCheckedOutAt() - appointmentCollection.getEngagedAt());
					}
					if (request.getType() != null) {
						appointmentCollection.setType(request.getType());
					} else {
						appointmentCollection.setType(appointmentCollection.getType());
					}
					appointmentCollection = appointmentRepository.save(appointmentCollection);

					appointmentCollection.setStatus(request.getStatus());
					appointmentCollection.setUpdatedTime(new Date());
					appointmentRepository.save(appointmentCollection);
				}

				response = new Appointment();
				BeanUtil.map(appointmentCollection, response);
				if (patientCard != null) {
					patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
					patientCard.getUser().setLocationId(patientCard.getLocationId());
					patientCard.getUser().setHospitalId(patientCard.getHospitalId());
					BeanUtil.map(patientCard.getUser(), patientCard);
					patientCard.setUserId(patientCard.getUserId());
					patientCard.setId(patientCard.getUserId());
					patientCard.setColorCode(patientCard.getUser().getColorCode());
					patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
					patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
				} else {
					patientCard = new PatientCard();
					patientCard.setLocalPatientName(request.getLocalPatientName());
					patientCard.setId(request.getPatientId());
				}
				response.setPatient(patientCard);

				response.setDoctorName(doctorName);
				if (appointmentLookupResponse.getLocation() != null) {
					response.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
					response.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

					String address = (!DPDoctorUtils
							.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
									? appointmentLookupResponse.getLocation().getStreetAddress() + ", "
									: "")
							+ (!DPDoctorUtils
									.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
											? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
											: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
									? appointmentLookupResponse.getLocation().getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
									? appointmentLookupResponse.getLocation().getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
									? appointmentLookupResponse.getLocation().getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
									? appointmentLookupResponse.getLocation().getCountry() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
									? appointmentLookupResponse.getLocation().getPostalCode()
									: "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					response.setClinicAddress(address);
					response.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
					response.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
				}

				// for online consultation
				if (request.getType() != null && request.getType().equals(AppointmentType.ONLINE_CONSULTATION)) {
					List<DoctorClinicProfileCollection> doctorClinicProfileCollectionn = doctorClinicProfileRepository
							.findByDoctorId(new ObjectId(request.getDoctorId()));
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollectionn)
						pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
								"New Online appointment created.", ComponentType.ONLINE_CONSULTATION.getType(), null,
								null);
				}

				else {
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByLocationId(new ObjectId(request.getLocationId()));
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
								"Appointment updated.", ComponentType.APPOINTMENT_REFRESH.getType(), null, null);
					}

				}

			} else {
				logger.error("incorrectAppointmentId");
				throw new BusinessException(ServiceError.InvalidInput, "incorrectAppointmentId");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Appointment getAppointmentById(ObjectId appointmentId) {
		Appointment appointment = null;
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("_id").is(appointmentId)),
				Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
				Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
				Aggregation.lookup("user_cl", "patientId", "_id", "patient"), Aggregation.unwind("patient")
		// ,Aggregation.lookup("appointment_payment_transfer_cl", "appointmentId",
		// "appointmentId", "payment"),
		// Aggregation.unwind("payment")
		);

		System.out.println("Aggregation" + aggregation);
		AppointmentLookupResponse appointmentLookupResponse = mongoTemplate
				.aggregate(aggregation, AppointmentCollection.class, AppointmentLookupResponse.class)
				.getUniqueMappedResult();

		if (appointmentLookupResponse != null) {
			appointment = new Appointment();
			BeanUtil.map(appointmentLookupResponse, appointment);
			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
					new ObjectId(appointmentLookupResponse.getPatientId()),
					new ObjectId(appointmentLookupResponse.getLocationId()),
					new ObjectId(appointmentLookupResponse.getHospitalId()));

			PatientCard patient = new PatientCard();
			BeanUtil.map(patientCollection, patient);
			patient.setUserId(patient.getUserId());
			patient.setId(patient.getUserId());
			if (patient.getUser() != null)
				patient.setColorCode(patient.getUser().getColorCode());
			patient.setImageUrl(getFinalImageURL(patient.getImageUrl()));
			patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl()));
			appointment.setPatient(patient);
			if (appointmentLookupResponse.getDoctor() != null) {
				appointment.setDoctorName(appointmentLookupResponse.getDoctor().getTitle() + " "
						+ appointmentLookupResponse.getDoctor().getFirstName());
			}
			if (appointmentLookupResponse.getLocation() != null) {
				appointment.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
				appointment.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

		

				String address = (!DPDoctorUtils
						.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
								? appointmentLookupResponse.getLocation().getStreetAddress() + ", "
								: "")
						+ (!DPDoctorUtils
								.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
										? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
										: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
								? appointmentLookupResponse.getLocation().getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
								? appointmentLookupResponse.getLocation().getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
								? appointmentLookupResponse.getLocation().getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
								? appointmentLookupResponse.getLocation().getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
								? appointmentLookupResponse.getLocation().getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}

				appointment.setClinicAddress(address);
				appointment.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
				appointment.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
			}
		}
		return appointment;
	}

	@Override
	@Transactional
	public Response<ConsultationSpeciality> getConsultationWithSpeciality(String locationId, String doctorId,
			String patientId, String from, String to, int page, int size, String updatedTime, String status,
			String sortBy, String fromTime, String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb,
			Boolean discarded, String branch, Boolean isAnonymousAppointment, String type) {
		Response<ConsultationSpeciality> response = new Response<ConsultationSpeciality>();
		List<ConsultationSpeciality> appointments = null;
		// PatientTreatmentResponse patientTreatmentResponse=null;

		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);
//online consultation
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			} else {
				criteria.and("type").is(AppointmentType.APPOINTMENT.getType());
			}

			criteria.and("updatedTime").gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").is(false);

			// Criteria criteria = new Criteria("updatedTime")
			// .gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").ne(true);

			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (isAnonymousAppointment != null)
				criteria.and("isAnonymousAppointment").is(isAnonymousAppointment);

			if (!DPDoctorUtils.anyStringEmpty(branch))
				criteria.and("branch").is(branch);
//			if (doctorId != null && !doctorId.isEmpty()) {
//				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
//				for (String id : doctorId)
//					doctorObjectIds.add(new ObjectId(id));
//				criteria.and("doctorId").in(doctorObjectIds);
//			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("status").is(status.toUpperCase()).and("state").ne(AppointmentState.CANCEL.getState());

			if (!discarded)
				criteria.and("discarded").is(discarded);

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime = null, toDateTime = null;
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				// DateTime
				fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
				System.out.println(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				// DateTime
				toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				System.out.println(toDateTime);

				criteria.and("toDate").lte(toDateTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(fromTime))
				criteria.and("time.fromTime").is(Integer.parseInt(fromTime));

			if (!DPDoctorUtils.anyStringEmpty(toTime))
				criteria.and("time.toTime").is(Integer.parseInt(toTime));
			List<AppointmentLookupResponse> appointmentLookupResponses = null;

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime"));

			if (!DPDoctorUtils.anyStringEmpty(status)) {
				if (status.equalsIgnoreCase(QueueStatus.SCHEDULED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "time.fromTime"));
				} else if (status.equalsIgnoreCase(QueueStatus.WAITING.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedInAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.ENGAGED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "engagedAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.CHECKED_OUT.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedOutAt"));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			}

			Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if (count != null && count > 0) {
				response.setCount(count);

//				if (isWeb)
//					appointments = getAppointmentsForWeb(criteria, sortOperation, page, size, appointments,
//							appointmentLookupResponses);

				if (size > 0) {
					Aggregation aggregation = Aggregation
							.newAggregation(Aggregation.match(criteria),
									Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
									Aggregation.unwind("doctor"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									// Aggregation.lookup("appointment_payment_transfer_cl", "appointmentId", "_id",
									// "payment"),
									// Aggregation.unwind("payment"),
									// new
									// Aggregation.lookup("patient_treatment_cl", "doctorId","treatmentServiceId" ,
									// "treatmentService"),
									// Aggregation.unwind("treatmentService"),
									Aggregation.lookup("patient_treatment_cl", "doctorId", "_id", "treatments"),
									Aggregation.unwind("treatments", true),

									Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
									new CustomAggregationOperation(new Document("$unwind",
											new BasicDBObject("path", "$patientCard")
													.append("preserveNullAndEmptyArrays", true))),
									new CustomAggregationOperation(new Document("$redact",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$patientCard.locationId", "$locationId")))
																	.append("then", "$$KEEP")
																	.append("else", "$$PRUNE")))),

									Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
									Aggregation.unwind("patientCard.user"), sortOperation,
									Aggregation.skip((page) * size), Aggregation.limit(size))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

					System.out.println("Aggregation" + aggregation);

					appointmentLookupResponses = mongoTemplate
							.aggregate(aggregation, AppointmentCollection.class, AppointmentLookupResponse.class)
							.getMappedResults();
				} else {
					Aggregation aggregation = Aggregation
							.newAggregation(Aggregation.match(criteria),
									Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
									Aggregation.unwind("doctor"),
									// Aggregation.lookup("appointment_payment_transfer_cl", "appointmentId", "_id",
									// "payment"),
									// Aggregation.unwind("payment"),

									// new
									// Aggregation.lookup("patient_treatment_cl", "doctorId","treatmentServiceId" ,
									// "treatmentService"),
									// Aggregation.unwind("treatmentService"),
									Aggregation.lookup("patient_treatment_cl", "doctorId", "_id", "treatments"),
									Aggregation.unwind("treatments", true),

									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
									new CustomAggregationOperation(new Document("$unwind",
											new BasicDBObject("path", "$patientCard")
													.append("preserveNullAndEmptyArrays", true))),
									new CustomAggregationOperation(new Document("$redact",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$patientCard.locationId", "$locationId")))
																	.append("then", "$$KEEP")
																	.append("else", "$$PRUNE")))),

									Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
									Aggregation.unwind("patientCard.user"), sortOperation)
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

					System.out.println("Aggregation" + aggregation);
					appointmentLookupResponses = mongoTemplate
							.aggregate(aggregation, AppointmentCollection.class, AppointmentLookupResponse.class)
							.getMappedResults();

				}

				if (appointmentLookupResponses != null && !appointmentLookupResponses.isEmpty()) {
					appointments = new ArrayList<ConsultationSpeciality>();

					for (AppointmentLookupResponse collection : appointmentLookupResponses) {
						ConsultationSpeciality appointment = new ConsultationSpeciality();
						// new
						// ObjectId appointmentId = new ObjectId(collection.getAppointmentId());

						PatientCard patientCard = null;
						// if
						// (collection.getType().getType().equals(AppointmentType.APPOINTMENT.getType()))
						// {
						patientCard = collection.getPatientCard();
						if (patientCard != null) {
							patientCard.setId(patientCard.getUserId());

							if (patientCard.getUser() != null) {
								patientCard.setColorCode(patientCard.getUser().getColorCode());
								patientCard.setMobileNumber(patientCard.getUser().getMobileNumber());
							}
							// patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
							patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));

						}
						// }
						BeanUtil.map(collection, appointment);

						// patientTreatmentResponse =
						// getPatientTreatmentAppointmentById(appointmentId.toString());

						appointment.setPatient(patientCard);
						appointment.setLocalPatientName(patientCard.getLocalPatientName());
						if (collection.getDoctor() != null) {
							appointment.setDoctorName(
									collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
						}

						if (collection.getPayment() != null) {
							appointment.setIsPaymentTransfer(collection.getPayment().getIsPaymentTransfer());
						}
						// new
//						if(collection.getPatientTreatmentResponse()!=null) {
//							
//						}
						// appointment.setPatientTreatmentResponse(patientTreatmentResponse);

						if (collection.getLocation() != null) {
							appointment.setLocationName(collection.getLocation().getLocationName());
							appointment.setClinicNumber(collection.getLocation().getClinicNumber());

							String address = (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getStreetAddress())
									? collection.getLocation().getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
											? collection.getLocation().getLandmarkDetails() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
											? collection.getLocation().getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
											? collection.getLocation().getCity() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
											? collection.getLocation().getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
											? collection.getLocation().getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
											? collection.getLocation().getPostalCode()
											: "");

							if (address.charAt(address.length() - 2) == ',') {
								address = address.substring(0, address.length() - 2);
							}

							appointment.setClinicAddress(address);
							appointment.setLatitude(collection.getLocation().getLatitude());
							appointment.setLongitude(collection.getLocation().getLongitude());
						}

						// response.setData(appointment);
						appointments.add(appointment);

					}
				}
			}

			response.setDataList(appointments);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getUsersFromUserCl(int size, int page, String searchTerm, String mobileNumber,
			Boolean isDentalChainPatient) {

		Response<Object> response = new Response<Object>();
		List<PatientAppUsersResponse> patientResponse = null;
		try {
//			Criteria criteria = new Criteria("signedUp").is(true);
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (mobileNumber != null) {
				criteria.and("mobileNumber").is(mobileNumber);
			}

			if (isDentalChainPatient)
				criteria.and("isDentalChainPatient").is(isDentalChainPatient);

//			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
//					new BasicDBObject("_id", "$_id")
//					.append("title", "$title")		
//					.append("firstName", "$patient.firstName")		
//					.append("lastName", "$lastName")
//					.append("middleName", "$middleName")
//					.append("emailAddress", "$emailAddress")					
//					.append("countryCode", "$countryCode")
//					.append("mobileNumber", "$mobileNumber")
//					.append("colorCode", "$colorCode")	
//					.append("createdTime", "$createdTime")	
//					.append("updatedTime", "$updatedTime")	
//					));
//
//			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//					new BasicDBObject("_id", "$_id")
//					.append("title", new BasicDBObject("$first", "$title"))
//					.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
//					.append("lastName", new BasicDBObject("$first", "$lastName"))
//					.append("middleName", new BasicDBObject("$first", "$middleName"))
//					.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//					.append("countryCode", new BasicDBObject("$first", "$countryCode"))
//					.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
//					.append("colorCode", new BasicDBObject("$first", "$colorCode"))
//					.append("createdTime",  new BasicDBObject("$first", "$createdTime"))
//					.append("updatedTime",  new BasicDBObject("$first", "$updatedTime"))
//					));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.lookup("user_cl", "userId", "_id", "patient"),
//						Aggregation.unwind("patient"),
//						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
//								.append("title", new BasicDBObject("$first", "$title"))
//								.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
//								.append("lastName", new BasicDBObject("$first", "$lastName"))
//								.append("middleName", new BasicDBObject("$first", "$middleName"))
//								.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//								.append("countryCode", new BasicDBObject("$first", "$countryCode"))
//								.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
//								.append("colorCode", new BasicDBObject("$first", "$colorCode"))
//								.append("isDentalChainVerified", new BasicDBObject("$first", "$isDentalChainVerified"))
//								.append("isDentalChainPatient", new BasicDBObject("$first", "$isDentalChainPatient"))
//
//								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
//								.append("updatedTime", new BasicDBObject("$first", "$updatedTime")))),

						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
//						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.lookup("user_cl", "userId", "_id", "patient"), Aggregation.unwind("patient"),
//						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
//								.append("title", new BasicDBObject("$first", "$title"))
//								.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
//
//								.append("lastName", new BasicDBObject("$first", "$lastName"))
//								.append("middleName", new BasicDBObject("$first", "$middleName"))
//								.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//								.append("countryCode", new BasicDBObject("$first", "$countryCode"))
//
//								.append("mobileNumber", new BasicDBObject("$first", "$patient.mobileNumber"))
//
//								.append("colorCode", new BasicDBObject("$first", "$colorCode"))
//								.append("isDentalChainVerified", new BasicDBObject("$first", "$isDentalChainVerified"))
//								.append("isDentalChainPatient", new BasicDBObject("$first", "$isDentalChainPatient"))
//								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
//								.append("updatedTime", new BasicDBObject("$first", "$updatedTime")))),

//						new CustomAggregationOperation(new Document("$project",
//								new BasicDBObject("_id", "$_id")
//								.append("title", "$title")		
//								.append("firstName", "$firstName")		
//								.append("lastName", "$lastName")
//								.append("middleName", "$middleName")
//								.append("emailAddress", "$emailAddress")					
//								.append("countryCode", "$countryCode")
//								.append("mobileNumber", "$mobileNumber")
//								.append("colorCode", "$colorCode")	
//								.append("createdTime", "$createdTime")	
//								.append("updatedTime", "$updatedTime")	
//								)),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
//						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			}
			patientResponse = mongoTemplate.aggregate(aggregation, UserCollection.class, PatientAppUsersResponse.class)

					.getMappedResults();
			response.setDataList(patientResponse);
			response.setCount(patientResponse.size());
			System.out.println("aggregation" + aggregation);
			
			return response;
		} catch (BusinessException e) {
			logger.error("Error while getting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patients " + e.getMessage());
		}
	}

	@Override
	public Response<Object> getZone(int size, int page, String cityId, String searchTerm, Boolean isDiscarded) {
		List<Zone> responseDataList = null;
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			Aggregation aggregation = null;
			if (!DPDoctorUtils.anyStringEmpty(cityId))
				criteria.and("cityId").is(new ObjectId(cityId));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("zone").regex("^" + searchTerm, "i"),
						new Criteria("zone").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime")), ZoneCollection.class,
					Zone.class).getMappedResults().size());

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			}
			AggregationResults<Zone> groupResults = mongoTemplate.aggregate(aggregation,
					ZoneCollection.class, Zone.class);
			response.setDataList(groupResults.getMappedResults());

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteZoneById(String zoneId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			ZoneCollection zoneCollection = zoneRepository
					.findById(new ObjectId(zoneId)).orElse(null);
			if (zoneCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			zoneCollection.setUpdatedTime(new Date());
			zoneCollection.setIsDiscarded(isDiscarded);
			zoneCollection = zoneRepository.save(zoneCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}
