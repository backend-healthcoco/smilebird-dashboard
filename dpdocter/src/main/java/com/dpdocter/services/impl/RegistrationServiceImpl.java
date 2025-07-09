
package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.Address;
import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.ClinicTreatmentRatelist;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.BirthAchievementCollection;
import com.dpdocter.collections.ClinicContactUsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorLabReportCollection;
import com.dpdocter.collections.FeedbackCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MasterBabyAchievementCollection;
import com.dpdocter.collections.MasterBabyImmunizationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ProfessionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SubscriptionCollection;
import com.dpdocter.collections.UserAddressCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.collections.VaccineCollection;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.ContactState;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.LocationState;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.Type;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.BirthAchievementRepository;
import com.dpdocter.repository.ClinicContactUsRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorLabReportRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.FeedbackRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MasterBabyAchievementRepository;
import com.dpdocter.repository.MasterBabyImmunizationRepository;
import com.dpdocter.repository.PatientGroupRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientV3Repository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ProfessionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRepositoryForAppointment;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.repository.VaccineRepository;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.ClinicProfileHandheld;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.CheckPatientSignUpResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.PatientStatusResponse;
import com.dpdocter.response.RegisterDoctorResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	private static Logger logger = LogManager.getLogger(RegistrationServiceImpl.class.getName());

	@Autowired
	private ClinicContactUsRepository clinicContactUsRepository;

	@Autowired
	private SubscriptionDetailRepository subscriptionDetailRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRepositoryForAppointment userRepositoryForAppoint;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientV3Repository patientv3Repository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private PatientGroupRepository patientGroupRepository;

	@Autowired
	private ReferenceRepository referrenceRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private ProfessionRepository professionRepository;

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private RecordsRepository recordsRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private OTPService otpService;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private VaccineRepository vaccineRepository;

	@Autowired
	private BirthAchievementRepository birthAchievementRepository;

	@Autowired
	private ESRegistrationService esRegRistrationService;

	@Autowired
	private MasterBabyAchievementRepository masterBabyAchievementRepository;

	@Autowired
	private MasterBabyImmunizationRepository masterBabyImmunizationRepository;

	@Autowired
	private GenerateUniqueUserNameService generateUniqueUserNameService;

	@Autowired
	private DoctorLabReportRepository doctorLabReportRepository;

	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Value(value = "${mail.forgotPassword.subject}")
	private String forgotUsernamePasswordSub;

	@Value(value = "${mail.staffmember.account.verify.subject}")
	private String staffmemberAccountVerifySub;

	@Value(value = "${mail.add.existing.doctor.to.clinic.subject}")
	private String addExistingDoctorToClinicSub;

	@Value(value = "${mail.add.doctor.to.clinic..verify.subject}")
	private String addDoctorToClinicVerifySub;

	@Value(value = "${mail.add.feedback.subject}")
	private String addFeedbackSubject;

	@Value(value = "${mail.add.feedback.for.doctor.subject}")
	private String addFeedbackForDoctorSubject;

	@Value(value = "${patient.count}")
	private String patientCount;
//	@Value(value = "${patient.welcome.message}")
//	private String patientWelcomeMessage;

	@Value(value = "${Register.checkPatientCount}")
	private String checkPatientCount;

	@Value(value = "${Signup.role}")
	private String role;

	@Value(value = "${Signup.DOB}")
	private String DOB;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	@Transactional
	public List<RegisteredPatientDetails> getUsersByPhoneNumber(String phoneNumber, String doctorId, String locationId,
			String hospitalId) {
		List<RegisteredPatientDetails> users = null;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumber(phoneNumber);
			if (userCollections != null) {
				users = new ArrayList<RegisteredPatientDetails>();
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						RegisteredPatientDetails user = new RegisteredPatientDetails();

						// if (locationId != null && hospitalId != null) {
						List<PatientCollection> patientCollections = patientRepository
								.findByUserId(userCollection.getId());
						boolean isPartOfClinic = false;
						if (patientCollections != null) {
							for (PatientCollection patientCollection : patientCollections) {
								if (patientCollection.getDoctorId() != null && patientCollection.getLocationId() != null
										&& patientCollection.getHospitalId() != null) {
									if (patientCollection.getDoctorId().equals(doctorId)
											&& patientCollection.getLocationId().equals(locationId)
											&& patientCollection.getHospitalId().equals(hospitalId)) {
										isPartOfClinic = true;
										break;
									}
								} else {
									Patient patient = new Patient();
									BeanUtil.map(patientCollection, patient);
									BeanUtil.map(patientCollection, user);
									BeanUtil.map(userCollection, user);
									patient.setPatientId(patientCollection.getUserId().toString());
									user.setPatient(patient);
								}
							}
						}
						if (!isPartOfClinic) {
							user.setUserId(userCollection.getId().toString());
							user.setFirstName(userCollection.getFirstName());
							user.setMobileNumber(userCollection.getMobileNumber());
						} else {
							BeanUtil.map(userCollection, user);
							user.setUserId(userCollection.getId().toString());
						}
						user.setIsPartOfClinic(isPartOfClinic);
						// }
						users.add(user);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return users;
	}

	@Override
	@Transactional
	public RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId,
			String hospitalId) {
		RegisteredPatientDetails registeredPatientDetails = null;
		List<Group> groups = null;
		try {
			ObjectId userObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(userId))
				userObjectId = new ObjectId(userId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection userCollection = userRepository.findById(userObjectId).orElse(null);
			if (userCollection != null) {
				PatientCollection patientCollection = patientRepository
						.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userObjectId, doctorObjectId,
								locationObjectId, hospitalObjectId);
				if (patientCollection != null) {

					Reference reference = null;
					if (patientCollection.getReferredBy() != null) {
						ReferencesCollection referencesCollection = referrenceRepository
								.findById(patientCollection.getReferredBy()).orElse(null);
						if (referencesCollection != null) {
							reference = new Reference();
							BeanUtil.map(referencesCollection, reference);
						}
					}
					patientCollection.setReferredBy(null);
					List<PatientGroupCollection> patientGroupCollections = patientGroupRepository
							.findByPatientIdAndDiscardedIsFalse(patientCollection.getUserId());

					registeredPatientDetails = new RegisteredPatientDetails();
					BeanUtil.map(patientCollection, registeredPatientDetails);
					BeanUtil.map(userCollection, registeredPatientDetails);
					registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
					registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());

					registeredPatientDetails.setUserId(userCollection.getId().toString());
					registeredPatientDetails.setReferredBy(reference);
					Patient patient = new Patient();
					BeanUtil.map(patientCollection, patient);
					patient.setPatientId(patientCollection.getId().toString());

					Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(),
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());

					if ((prescriptionCount != null && prescriptionCount > 0)
							|| (clinicalNotesCount != null && clinicalNotesCount > 0)
							|| (recordsCount != null && recordsCount > 0))
						patient.setIsDataAvailableWithOtherDoctor(true);

					patient.setIsPatientOTPVerified(otpService.checkOTPVerified(
							patientCollection.getDoctorId().toString(), patientCollection.getLocationId().toString(),
							patientCollection.getHospitalId().toString(), userCollection.getId().toString()));
					registeredPatientDetails.setPatient(patient);
					registeredPatientDetails.setAddress(patientCollection.getAddress());
					@SuppressWarnings("unchecked")
					Collection<ObjectId> groupIds = CollectionUtils.collect(patientGroupCollections,
							new BeanToPropertyValueTransformer("groupId"));
					if (groupIds != null && !groupIds.isEmpty()) {
						groups = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupIds))),
								GroupCollection.class, Group.class).getMappedResults();
						registeredPatientDetails.setGroups(groups);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	@Override
	@Transactional
	public Reference addEditReference(Reference reference) {
		try {
			ReferencesCollection referrencesCollection = new ReferencesCollection();
			BeanUtil.map(reference, referrencesCollection);
			if (referrencesCollection.getId() == null) {
				referrencesCollection.setCreatedTime(new Date());
				if (reference.getDoctorId() != null) {
					UserCollection userCollection = userRepository.findById(new ObjectId(reference.getDoctorId()))
							.orElse(null);
					if (userCollection != null) {
						referrencesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else
					referrencesCollection.setCreatedBy("ADMIN");
			}
			referrencesCollection = referrenceRepository.save(referrencesCollection);
			BeanUtil.map(referrencesCollection, reference);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return reference;
	}

	@Override
	@Transactional
	public Reference deleteReferrence(String referenceId, Boolean discarded) {
		Reference response = null;
		try {
			ReferencesCollection referrencesCollection = referrenceRepository.findById(new ObjectId(referenceId))
					.orElse(null);
			if (referrencesCollection != null) {
				referrencesCollection.setDiscarded(discarded);
				referrencesCollection.setUpdatedTime(new Date());
				referrenceRepository.save(referrencesCollection);
				response = new Reference();
				BeanUtil.map(referrencesCollection, response);
			} else {
				logger.warn("Invalid Referrence Id!");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Referrence Id!");
			}
		} catch (BusinessException be) {
			be.printStackTrace();
			logger.error(be);
			throw new BusinessException(ServiceError.Unknown, be.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<ReferenceDetail> getReferences(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ReferenceDetail> response = null;

		try {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalReferences(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomReferences(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalReferences(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<ReferenceDetail> getGlobalReferences(int page, int size, String updatedTime, boolean discarded) {
		List<ReferenceDetail> response = null;
		try {
			AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, "reference", null, null),
					ReferencesCollection.class, ReferenceDetail.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<ReferenceDetail> getCustomReferences(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<ReferenceDetail> response = null;
		try {
			AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, "reference", null, null, null),
					ReferencesCollection.class, ReferenceDetail.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<ReferenceDetail> getCustomGlobalReferences(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<ReferenceDetail> response = null;
		try {
			AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, "reference", null, null),
					ReferencesCollection.class, ReferenceDetail.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Location getClinicDetails(String clinicId) {
		Location location = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(clinicId)).orElse(null);
			if (locationCollection != null) {
				location = new Location();
				BeanUtil.map(locationCollection, location);

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
				location.setClinicAddress(address);
			} else {
				logger.warn("No Location Found For The Location Id");
				throw new BusinessException(ServiceError.NotFound, "No Location Found For The Location Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Retrieving Location Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Retrieving Location Details");
		}
		return location;
	}

	@Override
	@Transactional
	public ClinicProfile updateClinicProfile(ClinicProfile request) {
		ClinicProfile response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(request.getId())).orElse(null);
			locationCollection.setLocationEmailAddress(request.getLocationEmailAddress());
			locationCollection.setTagLine(request.getTagLine());
			locationCollection.setWebsiteUrl(request.getWebsiteUrl());
			locationCollection.setLocationName(request.getLocationName());
			locationCollection.setSpecialization(request.getSpecialization());
			locationCollection.setGoogleMapShortUrl(request.getGoogleMapShortUrl());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicProfile();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicAddress updateClinicAddress(ClinicAddress request) {
		ClinicAddress response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(request.getId())).orElse(null);
			String locationName = "";
			if (locationCollection != null) {
				locationName = locationCollection.getLocationName();
				BeanUtil.map(request, locationCollection);
			}

//			List<GeocodedLocation> geocodedLocations = locationServices
//					.geocodeLocation((!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
//							? locationCollection.getStreetAddress() + ", "
//							: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
//									? locationCollection.getLandmarkDetails() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
//									? locationCollection.getLocality() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
//									? locationCollection.getCity() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
//									? locationCollection.getState() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
//									? locationCollection.getCountry() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
//									? locationCollection.getPostalCode()
//									: ""));
//
//			if (request.getLatitude() == null || request.getLongitude() == null) {
//				if (geocodedLocations != null && !geocodedLocations.isEmpty()) {
//					BeanUtil.map(geocodedLocations.get(0), locationCollection);
//				}
//			}

			if (DPDoctorUtils.anyStringEmpty(request.getLocationName()))
				locationCollection.setLocationName(locationName);
			locationCollection.setAlternateClinicNumbers(request.getAlternateClinicNumbers());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicAddress();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicTiming updateClinicTiming(ClinicTiming request) {
		ClinicTiming response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (locationCollection != null)
				BeanUtil.map(request, locationCollection);
			locationCollection.setClinicWorkingSchedules(request.getClinicWorkingSchedules());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicTiming();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicSpecialization updateClinicSpecialization(ClinicSpecialization request) {
		ClinicSpecialization response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (locationCollection != null)
				BeanUtil.map(request, locationCollection);
			locationCollection.setSpecialization(request.getSpecialization());
			locationCollection.setId(new ObjectId(request.getId()));
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicSpecialization();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public List<BloodGroup> getBloodGroup() {

		List<BloodGroup> bloodGroups = new ArrayList<BloodGroup>();
		try {
			for (com.dpdocter.enums.BloodGroup group : com.dpdocter.enums.BloodGroup.values()) {
				BloodGroup bloodGroup = new BloodGroup();
				bloodGroup.setBloodGroup(group.getGroup());
				bloodGroups.add(bloodGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return bloodGroups;
	}

	@Override
	@Transactional
	public Profession addProfession(Profession request) {
		Profession profession = new Profession();
		try {
			ProfessionCollection professionCollection = new ProfessionCollection();
			BeanUtil.map(request, professionCollection);
			professionCollection = professionRepository.save(professionCollection);
			BeanUtil.map(professionCollection, profession);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return profession;
	}

	@Override
	@Transactional
	public List<Profession> getProfession(int page, int size, String updatedTime) {
		List<Profession> professions = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "profession")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "profession")));
			AggregationResults<Profession> aggregationResults = mongoTemplate.aggregate(aggregation,
					ProfessionCollection.class, Profession.class);
			professions = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return professions;
	}

	@Override
	@Transactional
	public ClinicLogo changeClinicLogo(ClinicLogoAddRequest request) {
		ClinicLogo response = null;
		try {
			LocationCollection locationCollection = locationRepository.findById(new ObjectId(request.getId()))
					.orElse(null);
			if (locationCollection == null) {
				logger.warn("Clinic not found");
				throw new BusinessException(ServiceError.NotFound, "Clinic not found");
			} else {
				if (request.getImage() != null) {
					String path = "clinic" + File.separator + "logos";
					// save image
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					locationCollection.setLogoUrl(imageURLResponse.getImageUrl());
					locationCollection.setLogoThumbnailUrl(imageURLResponse.getThumbnailUrl());
					locationCollection = locationRepository.save(locationCollection);

					List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
							.findByLocationId(new ObjectId(request.getId()));
					if (printSettingsCollections != null) {
						for (PrintSettingsCollection printSettingsCollection : printSettingsCollections) {
							printSettingsCollection.setClinicLogoUrl(imageURLResponse.getImageUrl());
							printSettingsRepository.save(printSettingsCollection);
						}
					}
					response = new ClinicLogo();
					BeanUtil.map(locationCollection, response);
				}
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
	public ImageURLResponse changePharmaLogo(FileDetails request) {
		ImageURLResponse response = null;
		try {

			if (request != null) {
				String path = "pharma" + File.separator + "logos";
				// save image
				request.setFileName(request.getFileName() + new Date().getTime());
				response = fileManager.saveImageAndReturnImageUrl(request, path, true);

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
	public List<ClinicImage> addClinicImage(ClinicImageAddRequest request) {
		List<ClinicImage> response = null;

		try {
			LocationCollection locationCollection = locationRepository.findById(new ObjectId(request.getId()))
					.orElse(null);
			if (locationCollection == null) {
				logger.warn("Clinic not found");
				throw new BusinessException(ServiceError.NotFound, "Clinic not found");
			} else {
				int counter = locationCollection.getImages() != null ? locationCollection.getImages().size() : 0;
				response = new ArrayList<ClinicImage>();
				if (locationCollection.getImages() != null)
					response.addAll(locationCollection.getImages());
				if (request.getImages() != null) {
					for (FileDetails image : request.getImages()) {
						counter++;
						String path = "clinic" + File.separator + "images";
						image.setFileName(image.getFileName() + new Date().getTime());
						ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(image, path, true);
						ClinicImage clinicImage = new ClinicImage();
						clinicImage.setImageUrl(imageURLResponse.getImageUrl());
						clinicImage.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
						clinicImage.setCounter(counter);
						response.add(clinicImage);
					}
					locationCollection.setImages(response);
					locationCollection.setUpdatedTime(new Date());
					locationCollection = locationRepository.save(locationCollection);
					BeanUtil.map(locationCollection, response);
				}
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
	public Boolean deleteClinicImage(String locationId, int counter) {

		try {
			LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (locationCollection == null) {
				logger.warn("User not found");
				throw new BusinessException(ServiceError.NotFound, "Clinic not found");
			} else {
				boolean foundImage = false;
				int imgCounter = 0;
				List<ClinicImage> images = locationCollection.getImages();
				List<ClinicImage> copyimages = new ArrayList<ClinicImage>(images);
				for (Iterator<ClinicImage> iterator = copyimages.iterator(); iterator.hasNext();) {
					ClinicImage image = iterator.next();
					if (foundImage) {
						image.setCounter(imgCounter);
						imgCounter++;
					} else if (image.getCounter() == counter) {
						foundImage = true;
						imgCounter = counter;
						images.remove(image);
					}
				}
				locationCollection.setImages(images);
				locationCollection.setUpdatedTime(new Date());
				locationCollection = locationRepository.save(locationCollection);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	@Transactional
	public Boolean checktDoctorExistByEmailAddress(String emailAddress) {
		try {
			UserCollection userCollections = userRepository.findByUserNameAndEmailAddress(emailAddress, emailAddress);
			if (userCollections == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Role addRole(Role request) {
		Role role = new Role();
		try {
			RoleCollection roleCollection = new RoleCollection();
			BeanUtil.map(request, roleCollection);
			roleCollection.setCreatedTime(new Date());
			roleCollection = roleRepository.save(roleCollection);

			if (request.getAccessModules() != null && !request.getAccessModules().isEmpty()) {
				AccessControl accessControl = new AccessControl();
				BeanUtil.map(request, accessControl);
				accessControl.setType(Type.ROLE);
				accessControl.setRoleOrUserId(roleCollection.getId().toString());
				accessControl = accessControlServices.setAccessControls(accessControl);
				if (accessControl != null)
					role.setAccessModules(accessControl.getAccessModules());
			}
			BeanUtil.map(roleCollection, role);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return role;
	}

	@Override
	@Transactional
	public List<Role> getRole(String range, int page, int size, String locationId, String hospitalId,
			String updatedTime) {
		List<Role> response = null;

		try {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalRole(page, size, updatedTime);
				break;
			case CUSTOM:
				response = getCustomRole(page, size, locationId, hospitalId, updatedTime);
				break;
			case BOTH:
				response = getCustomGlobalRole(page, size, locationId, hospitalId, updatedTime);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	private List<Role> getCustomGlobalRole(int page, int size, String locationId, String hospitalId,
			String updatedTime) {
		List<Role> response = null;
		List<RoleCollection> roleCollections = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				if (size > 0)
					roleCollections = roleRepository.findByUpdatedTimeGreaterThan(new Date(createdTimeStamp),
							PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					roleCollections = roleRepository.findByUpdatedTimeGreaterThan(new Date(createdTimeStamp),
							new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
				if (size > 0)
					roleCollections = roleRepository.findCustomGlobal(new ObjectId(locationId),
							new ObjectId(hospitalId), new Date(createdTimeStamp),
							PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					roleCollections = roleRepository.findCustomGlobal(new ObjectId(locationId),
							new ObjectId(hospitalId), new Date(createdTimeStamp),
							new Sort(Sort.Direction.DESC, "createdTime"));
			}
			if (roleCollections != null) {
				response = new ArrayList<Role>();
				for (RoleCollection roleCollection : roleCollections) {
					Role role = new Role();
					AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(),
							roleCollection.getLocationId(), roleCollection.getHospitalId());
					BeanUtil.map(roleCollection, role);
					role.setAccessModules(accessControl.getAccessModules());
					response.add(role);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<Role> getCustomRole(int page, int size, String locationId, String hospitalId, String updatedTime) {
		List<Role> response = null;
		List<RoleCollection> roleCollections = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			if (size > 0)
				roleCollections = roleRepository.findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThan(
						new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp),
						PageRequest.of(page, size, Direction.DESC, "createdTime"));
			else
				roleCollections = roleRepository.findByLocationIdIsNullAndHospitalIdIsNullAndUpdatedTimeGreaterThan(
						new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp),
						new Sort(Sort.Direction.DESC, "createdTime"));

			if (roleCollections != null) {
				response = new ArrayList<Role>();
				for (RoleCollection roleCollection : roleCollections) {
					Role role = new Role();
					AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(),
							roleCollection.getLocationId(), roleCollection.getHospitalId());
					BeanUtil.map(roleCollection, role);
					role.setAccessModules(accessControl.getAccessModules());
					response.add(role);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<Role> getGlobalRole(int page, int size, String updatedTime) {
		List<Role> response = null;
		List<RoleCollection> roleCollections = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			if (size > 0)
				roleCollections = roleRepository.findByLocationIdIsNullAndHospitalIdIsNullAndUpdatedTimeGreaterThan(
						new Date(createdTimeStamp), PageRequest.of(page, size, Direction.DESC, "createdTime"));
			else
				roleCollections = roleRepository.findByLocationIdIsNullAndHospitalIdIsNullAndUpdatedTimeGreaterThan(
						new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

			if (roleCollections != null) {
				response = new ArrayList<Role>();
				for (RoleCollection roleCollection : roleCollections) {
					Role role = new Role();
					AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(),
							roleCollection.getLocationId(), roleCollection.getHospitalId());
					BeanUtil.map(roleCollection, role);
					role.setAccessModules(accessControl.getAccessModules());
					response.add(role);
				}
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
	public ESDoctorDocument getESDoctorDocument(RegisterDoctorResponse doctorResponse) {
		ESDoctorDocument esDoctorDocument = null;
		try {
			esDoctorDocument = new ESDoctorDocument();
			BeanUtil.map(doctorResponse, esDoctorDocument);
			LocationCollection locationCollection = locationRepository
					.findById(new ObjectId(doctorResponse.getLocationId())).orElse(null);
			if (locationCollection != null) {
				BeanUtil.map(locationCollection, esDoctorDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return esDoctorDocument;
	}

	@Override
	@Transactional
	public Role deleteRole(String roleId, Boolean discarded) {
		Role response = null;
		try {
			RoleCollection roleCollection = roleRepository.findById(new ObjectId(roleId)).orElse(null);
			if (roleCollection != null) {
				roleCollection.setDiscarded(discarded);
				roleCollection = roleRepository.save(roleCollection);
				response = new Role();
				BeanUtil.map(roleCollection, response);
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
	public ClinicLabProperties updateLabProperties(ClinicLabProperties request) {
		ClinicLabProperties response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (locationCollection != null) {
				if (request.getIsClinic().equals(false) && request.getIsLab().equals(false)
						&& request.getIsDentalImagingLab().equals(false)
						&& request.getIsDentalWorksLab().equals(false)) {
					logger.error("Location has to be either Clinic or Lab or Both");
					throw new BusinessException(ServiceError.Unknown,
							"Location has to be either Clinic or Lab or Both");
				}
				locationCollection.setIsLab(request.getIsLab());
				locationCollection.setIsClinic(request.getIsClinic());
				locationCollection.setIsDentalImagingLab(request.getIsDentalImagingLab());
				locationCollection.setIsDentalWorksLab(request.getIsDentalWorksLab());
				locationCollection.setIsMobileNumberOptional(request.getIsMobileNumberOptional());
				if (request.getIsLab()) {
					locationCollection.setIsHomeServiceAvailable(request.getIsHomeServiceAvailable());
					locationCollection.setIsNABLAccredited(request.getIsNABLAccredited());
					locationCollection.setIsOnlineReportsAvailable(request.getIsOnlineReportsAvailable());
					locationCollection.setIsParent(request.getIsParent());
				} else {
					locationCollection.setIsHomeServiceAvailable(false);
					locationCollection.setIsNABLAccredited(false);
					locationCollection.setIsOnlineReportsAvailable(false);
					locationCollection.setIsParent(false);
				}
				System.out.println(locationCollection);
				locationCollection = locationRepository.save(locationCollection);
				response = new ClinicLabProperties();
				BeanUtil.map(locationCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic IsLab");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic IsLab");
		}
		return response;
	}

	@Override
	@Transactional
	public Feedback addFeedback(Feedback request) {
		Feedback response = new Feedback();
		try {
			FeedbackCollection feedbackCollection = new FeedbackCollection();
			BeanUtil.map(request, feedbackCollection);
			feedbackCollection
					.setUniqueFeedbackId(UniqueIdInitial.FEEDBACK.getInitial() + DPDoctorUtils.generateRandomId());
			feedbackCollection.setCreatedTime(new Date());
			feedbackCollection = feedbackRepository.save(feedbackCollection);
			if (feedbackCollection != null
					&& (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType())
							|| feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType())
							|| feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType()))) {
				if (feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType())
						&& request.getResourceId() != null) {
					PrescriptionCollection prescriptionCollection = prescriptionRepository
							.findById(new ObjectId(request.getResourceId())).orElse(null);
					if (prescriptionCollection != null) {
						prescriptionCollection.setIsFeedbackAvailable(true);
						prescriptionCollection.setUpdatedTime(new Date());
						prescriptionRepository.save(prescriptionCollection);
					}
				}
				if (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType())
						&& request.getResourceId() != null) {
					AppointmentCollection appointmentCollection = appointmentRepository
							.findById(new ObjectId(request.getResourceId())).orElse(null);
					if (appointmentCollection != null) {
						appointmentCollection.setIsFeedbackAvailable(true);
						appointmentCollection.setUpdatedTime(new Date());
						appointmentRepository.save(appointmentCollection);
					}
				}
				if (feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType())
						&& request.getResourceId() != null) {
					RecordsCollection recordsCollection = recordsRepository
							.findById(new ObjectId(request.getResourceId())).orElse(null);
					if (recordsCollection != null) {
						recordsCollection.setIsFeedbackAvailable(true);
						recordsCollection.setUpdatedTime(new Date());
						recordsRepository.save(recordsCollection);
					}
				} else {
					feedbackCollection.setIsVisible(true);
				}
				DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdAndLocationId(feedbackCollection.getDoctorId(),
								feedbackCollection.getLocationId());
				if (doctorClinicProfileCollection != null) {
					doctorClinicProfileCollection = new DoctorClinicProfileCollection();
					doctorClinicProfileCollection.setDoctorId(feedbackCollection.getDoctorId());
					doctorClinicProfileCollection.setLocationId(feedbackCollection.getLocationId());
					doctorClinicProfileCollection.setCreatedTime(new Date());
				}
				if (feedbackCollection.getIsRecommended())
					doctorClinicProfileCollection
							.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() + 1);
				else
					doctorClinicProfileCollection
							.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() - 1);
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);

				feedbackCollection = feedbackRepository.findById(feedbackCollection.getId()).orElse(null);
				UserCollection patient = userRepository.findById(feedbackCollection.getUserId()).orElse(null);
				UserCollection doctor = userRepository.findById(feedbackCollection.getDoctorId()).orElse(null);
				LocationCollection locationCollection = locationRepository.findById(feedbackCollection.getLocationId())
						.orElse(null);
				if (patient.getEmailAddress() != null) {
					String body = mailBodyGenerator.generateFeedbackEmailBody(patient.getFirstName(),
							doctor.getTitle() + " " + doctor.getFirstName(), locationCollection.getLocationName(),
							feedbackCollection.getUniqueFeedbackId(), "feedbackUserToDoctorTemplate.vm");
					mailService.sendEmail(patient.getEmailAddress(), addFeedbackForDoctorSubject, body, null);
				}
			} else {
				if (feedbackCollection.getEmailAddress() != null) {
					String body = mailBodyGenerator.generateFeedbackEmailBody(feedbackCollection.getCreatedBy(), null,
							null, null, "feedbackTemplate.vm");
					mailService.sendEmail(feedbackCollection.getEmailAddress(), addFeedbackSubject, body, null);
				}
			}
			BeanUtil.map(feedbackCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding feedback");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicProfile updateClinicProfileHandheld(ClinicProfileHandheld request) {
		ClinicProfile response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (locationCollection != null)
				BeanUtil.map(request, locationCollection);
			else {
				logger.error("No Clinic Found");
				throw new BusinessException(ServiceError.NoRecord, "No Clinic Found");
			}
			locationCollection.setAlternateClinicNumbers(request.getAlternateClinicNumbers());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicProfile();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public PatientStatusResponse getPatientStatus(String patientId, String doctorId, String locationId,
			String hospitalId) {
		PatientStatusResponse response = new PatientStatusResponse();
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorObjectId,
					patientObjectId, hospitalObjectId, locationObjectId);
			Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorObjectId,
					patientObjectId, hospitalObjectId, locationObjectId);
			Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorObjectId, patientObjectId,
					hospitalObjectId, locationObjectId);

			if ((prescriptionCount != null && prescriptionCount > 0)
					|| (clinicalNotesCount != null && clinicalNotesCount > 0)
					|| (recordsCount != null && recordsCount > 0))
				response.setIsDataAvailableWithOtherDoctor(true);

			response.setIsPatientOTPVerified(otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While getting status");
			throw new BusinessException(ServiceError.Unknown, "Error While getting status");
		}
		return response;
	}

	@Override
	@Transactional
	public Feedback visibleFeedback(String feedbackId, Boolean isVisible) {
		Feedback response = null;
		try {
			FeedbackCollection feedbackCollection = feedbackRepository.findById(new ObjectId(feedbackId)).orElse(null);
			if (feedbackCollection != null) {
				feedbackCollection.setUpdatedTime(new Date());
				feedbackCollection.setIsVisible(isVisible);
				feedbackCollection = feedbackRepository.save(feedbackCollection);
				if (!DPDoctorUtils.anyStringEmpty(feedbackCollection.getDoctorId())) {
					DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdAndLocationId(feedbackCollection.getDoctorId(),
									feedbackCollection.getLocationId());

					if (isVisible)
						doctorClinicProfileCollection
								.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() + 1);
					else
						doctorClinicProfileCollection
								.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() - 1);
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);

				} else if (DPDoctorUtils.anyStringEmpty(feedbackCollection.getLocationId())) {
					LocationCollection locationCollection = locationRepository
							.findById(feedbackCollection.getLocationId()).orElse(null);

					if (isVisible)
						locationCollection.setNoOfClinicReview(locationCollection.getNoOfClinicReview() + 1);
					else
						locationCollection.setNoOfClinicReview(locationCollection.getNoOfClinicReview() - 1);
					locationRepository.save(locationCollection);

				}
				response = new Feedback();
				BeanUtil.map(feedbackCollection, response);
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while editing feedback");
		}
		return response;
	}

	@Override
	@Transactional
	public List<Feedback> getFeedback(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, String type) {
		List<Feedback> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("type", "$type"), Fields.field("appType", "$appType"),
					Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
					Fields.field("doctorId", "$doctorId"), Fields.field("resourceId", "$resourceId"),
					Fields.field("userId", "$userId"), Fields.field("explanation", "$explanation"),
					Fields.field("deviceType", "$deviceType"), Fields.field("deviceInfo", "$deviceInfo"),
					Fields.field("isVisible", "$isVisible"), Fields.field("isRecommended", "$isRecommended"),
					Fields.field("uniqueFeedbackId", "$uniqueFeedbackId"),
					Fields.field("emailAddress", "$emailAddress"), Fields.field("isUserAnonymous", "$isUserAnonymous"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime")));

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));

			Criteria patientCriteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));

			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			}
			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);

			if (size > 0)
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.lookup("user_cl", "userId", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
						projectList, Aggregation.skip((long) (page) * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), FeedbackCollection.class,
						Feedback.class).getMappedResults();
			else
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.lookup("user_cl", "userId", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(patientCriteria), projectList,
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), FeedbackCollection.class,
						Feedback.class).getMappedResults();

			for (Feedback feedbackCollection : response) {
				if (feedbackCollection.getPatient() != null) {
					feedbackCollection.getPatient()
							.setImageUrl(getFinalImageURL(feedbackCollection.getPatient().getImageUrl()));
					feedbackCollection.getPatient()
							.setThumbnailUrl(getFinalImageURL(feedbackCollection.getPatient().getThumbnailUrl()));
					// feedbackCollection.setPatient(user);
				}
				// response.add(feedback);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting feedback");
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
	public Location registerClinic(ClinicContactUs request) {
		Location response;
		ClinicContactUsCollection clinicContactUsCollection;
		try {
			UserCollection userCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
				userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			} else {
				userCollection = userRepository.findByUserNameAndEmailAddress(request.getEmailAddress(),
						request.getEmailAddress());
			}

			DoctorCollection doctorCollection = null;

			if (userCollection != null) {
				doctorCollection = doctorRepository.findByUserId(userCollection.getId());
			}
			if (doctorCollection != null) {
				HospitalCollection hospitalCollection = null;
				if (request.getHospitalId() != null) {
					hospitalCollection = hospitalRepository.findById(new ObjectId(request.getHospitalId()))
							.orElse(null);

				} else {
					hospitalCollection = new HospitalCollection();
					hospitalCollection.setCreatedTime(new Date());
					hospitalCollection
							.setHospitalUId(UniqueIdInitial.HOSPITAL.getInitial() + DPDoctorUtils.generateRandomId());
					hospitalCollection.setUpdatedTime(new Date());
					hospitalCollection = hospitalRepository.save(hospitalCollection);
				}

				LocationCollection locationCollection = new LocationCollection();
				locationCollection.setCity(request.getCity());
				locationCollection.setState(LocationState.NOTACTIVATED.toString());
				locationCollection.setIsActivate(true);
				locationCollection.setCreatedTime(new Date());
				locationCollection.setClinicNumber(request.getClinicNumber());
				locationCollection.setCountry(request.getCountry());
				locationCollection.setHospitalId(hospitalCollection.getId());
				locationCollection.setLocationName(request.getLocationName());
				locationCollection
						.setLocationUId(UniqueIdInitial.LOCATION.getInitial() + DPDoctorUtils.generateRandomId());
				locationCollection.setState(request.getState());
				locationCollection.setIsClinic(request.getIsClinic());
				locationCollection.setIsLab(request.getIsLab());
				locationCollection.setIsParent(request.getIsParent());
				locationCollection.setIsDentalWorksLab(request.getIsDentalWorksLab());
				locationCollection.setIsDentalImagingLab(request.getIsDentalImagingLab());
				locationCollection.setIsActivate(true);
				locationCollection.setStreetAddress(request.getStreetAddress());
				locationCollection = locationRepository.save(locationCollection);

//				List<RoleCollection> roleCollections = roleRepository
//						.findByRoles(Arrays.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole(),
//								RoleEnum.DOCTOR.getRole()));

				List<RoleCollection> roleCollections = roleRepository.findByRoleInAndLocationIdAndHospitalId(
						Arrays.asList(RoleEnum.HOSPITAL_ADMIN.getRole(), RoleEnum.LOCATION_ADMIN.getRole(),
								RoleEnum.DOCTOR.getRole(), RoleEnum.SUPER_ADMIN.getRole()),
						null, null);

				Collection<ObjectId> roleIds = CollectionUtils.collect(roleCollections,
						new BeanToPropertyValueTransformer("id"));
				List<UserRoleCollection> userRoleCollections = new ArrayList<>();
				for (ObjectId roleId : roleIds) {
					UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleId,
							locationCollection.getId(), locationCollection.getHospitalId());
					userRoleCollection.setCreatedTime(new Date());
					userRoleCollections.add(userRoleCollection);
				}
				userRoleRepository.saveAll(userRoleCollections);

				// Add cllinic in subscription
				SubscriptionCollection subscriptionCollection = subscriptionRepository
						.findByDoctorId(userCollection.getId());

				// save user location.
				DoctorClinicProfileCollection doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setDoctorId(userCollection.getId());
				doctorClinicProfileCollection.setLocationId(locationCollection.getId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
				doctorClinicProfileCollection.setIsActivate(true);
				if (subscriptionCollection != null) {
					doctorClinicProfileCollection.setPackageType(subscriptionCollection.getPackageName().toString());
				}
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);

				// Add cllinic in subscription
//				SubscriptionDetailCollection subscriptionDetailCollection = subscriptionDetailRepository
//						.findByDoctorId(userCollection.getId());
//				if (subscriptionDetailCollection != null) {
//					Set<ObjectId> locationIdSet = subscriptionDetailCollection.getLocationIds();
//					locationIdSet.add(userCollection.getId());
//					subscriptionDetailCollection.setLocationIds(locationIdSet);
//					subscriptionDetailRepository.save(subscriptionDetailCollection);
//				}

				if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
					clinicContactUsCollection = clinicContactUsRepository.findById(new ObjectId(request.getId()))
							.orElse(null);
					clinicContactUsCollection.setContactState(DoctorContactStateType.SIGNED_UP);
					clinicContactUsRepository.save(clinicContactUsCollection);
				} else {
					clinicContactUsCollection = new ClinicContactUsCollection();
					BeanUtil.map(request, clinicContactUsCollection);
					clinicContactUsCollection.setContactState(DoctorContactStateType.SIGNED_UP);
					clinicContactUsRepository.save(clinicContactUsCollection);

				}
				String body = mailBodyGenerator.generateActivationEmailBody(
						userCollection.getTitle() + " " + userCollection.getFirstName(), null,
						"addDoctorToClinicVerifyTemplate.vm",
						userCollection.getTitle() + " " + userCollection.getFirstName(),
						locationCollection.getLocationName());
				// mailService.sendEmail(userCollection.getEmailAddress(),
				// addDoctorToClinicVerifySub, body, null);

				transnationalService.checkDoctor(userCollection.getId(), locationCollection.getId());
				response = new Location();
				BeanUtil.map(locationCollection, response);

			} else
				throw new BusinessException(ServiceError.Unknown, "Doctor Not Found");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public ESPatientDocument getESPatientDocument(RegisteredPatientDetails patient) {
		ESPatientDocument esPatientDocument = null;
		try {
			esPatientDocument = new ESPatientDocument();
			if (patient.getAddress() != null) {
				BeanUtil.map(patient.getAddress(), esPatientDocument);
			}
			if (patient.getPatient() != null) {
				BeanUtil.map(patient.getPatient(), esPatientDocument);
			}
			BeanUtil.map(patient, esPatientDocument);
			if (patient.getBackendPatientId() != null)
				esPatientDocument.setId(patient.getBackendPatientId());
			if (patient.getReferredBy() != null)
				esPatientDocument.setReferredBy(patient.getReferredBy().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return esPatientDocument;
	}

	@Override
	public RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request) {
		RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
		PatientCollection patientCollection = null;
		List<Group> groups = null;
		try {

			if (!DPDoctorUtils.anyStringEmpty(request.getPID())) {
				Integer count = patientRepository.findPatientByPID(new ObjectId(request.getDoctorId()),
						new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()), request.getPID(),
						new ObjectId(request.getUserId()));
				if (count != null && count > 0) {
					logger.warn("Patient with this PID is already present. Please add patient different PID");
					throw new BusinessException(ServiceError.InvalidInput,
							"Patient with this PID is already present. Please add patient different PID");
				}
			}
			if (request.getDob() != null && request.getDob().getAge() != null
					&& request.getDob().getAge().getYears() < 0) {
				logger.warn(DOB);
				throw new BusinessException(ServiceError.InvalidInput, DOB);
			} else if (request.getDob() == null && request.getAge() != null) {
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR) - request.getAge();
				request.setDob(new DOB(currentDay, currentMonth, currentYear));
			}
			ObjectId userObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getUserId()))
				userObjectId = new ObjectId(request.getUserId());
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			// save Patient Info
			if (DPDoctorUtils.anyStringEmpty(doctorObjectId, hospitalObjectId, locationObjectId)) {
				UserCollection userCollection = userRepository.findById(userObjectId).orElse(null);
				if (userCollection == null) {
					logger.error("Incorrect User Id");
					throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
				}

				if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName()))
					userCollection.setFirstName(request.getLocalPatientName());

				userCollection.setIsActive(true);
				userCollection.setUpdatedTime(new Date());
				userCollection.setIsDentalChainPatient(true);
				BeanUtil.map(userCollection, registeredPatientDetails);
				patientCollection = patientRepository.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userObjectId,
						doctorObjectId, locationObjectId, hospitalObjectId);
				if (patientCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
						patientCollection.setLocalPatientName(request.getLocalPatientName());
						patientCollection.setFirstName(request.getLocalPatientName());
					}
					if (!DPDoctorUtils.anyStringEmpty(request.getBloodGroup()))
						patientCollection.setBloodGroup(request.getBloodGroup());
					if (!DPDoctorUtils.anyStringEmpty(request.getGender()))
						patientCollection.setGender(request.getGender());
					if (!DPDoctorUtils.anyStringEmpty(request.getEmailAddress()))
						patientCollection.setEmailAddress(request.getEmailAddress());
					if (request.getDob() != null)
						patientCollection.setDob(request.getDob());
					if (request.getAddress() != null)
						patientCollection.setAddress(request.getAddress());

					if (request.getAddress() != null)
						patientCollection.setAddress(request.getAddress());

					if (!DPDoctorUtils.anyStringEmpty(request.getPID())) {
						patientCollection.setPID(request.getPID());
					}

					if (!DPDoctorUtils.anyStringEmpty(request.getLanguage())) {
						patientCollection.setLanguage(request.getLanguage());
					}

					if (!DPDoctorUtils.anyStringEmpty(request.getFatherName())) {
						patientCollection.setFatherName(request.getFatherName());
					}

					if (!DPDoctorUtils.anyStringEmpty(request.getMotherName())) {
						patientCollection.setMotherName(request.getMotherName());
					}

					patientCollection.setIsChild(request.getIsChild());
					// patientCollection.setRegistrationDate(request.getRegistrationDate());
				} else {
					logger.error("Incorrect User Id, DoctorId, LocationId, HospitalId");
					throw new BusinessException(ServiceError.InvalidInput,
							"Incorrect User Id, DoctorId, LocationId, HospitalId");
				}
				if (request.getImage() != null) {
					String path = "profile-images";
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					patientCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setImageUrl(null);
					patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
					userCollection.setThumbnailUrl(null);
				}
				// patientCollection.setRegistrationDate(request.getRegistrationDate());
				patientCollection.setUpdatedTime(new Date());
				System.out.println("request.getCreatedBy" + request.getCreatedBy());
				userCollection.setCreatedBy(request.getCreatedBy());
				userCollection = userRepository.save(userCollection);
				patientCollection = patientRepository.save(patientCollection);

				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				patient.setPatientId(userCollection.getId().toString());
				registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
				registeredPatientDetails.setPatient(patient);
				registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
				registeredPatientDetails.setDob(patientCollection.getDob());
				registeredPatientDetails.setUserId(userCollection.getId().toString());
				registeredPatientDetails.setGender(patientCollection.getGender());
				registeredPatientDetails.setPID(patientCollection.getPID());
				registeredPatientDetails.setPNUM(patientCollection.getPNUM());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
					registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
					registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
					registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
				registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
				registeredPatientDetails.setAddress(patientCollection.getAddress());
				registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
				registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
				registeredPatientDetails.setIsChild(patientCollection.getIsChild());
			} else {
				List<PatientCollection> patientCollections = patientv3Repository
						.findByUserIdAndLocationIdAndHospitalId(userObjectId, locationObjectId, hospitalObjectId);
				if (patientCollections != null && !patientCollections.isEmpty())
					patientCollection = patientCollections.get(0);

//				patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(userObjectId,
//						locationObjectId, hospitalObjectId);
				String PID = request.getPID(), PNUM = request.getPNUM();
				if (patientCollection != null) {
					ObjectId patientId = patientCollection.getId();
					ObjectId patientDoctorId = patientCollection.getDoctorId();
					if (DPDoctorUtils.anyStringEmpty(request.getPID()))
						PID = patientCollection.getPID();
					if (DPDoctorUtils.anyStringEmpty(request.getPNUM()))
						PNUM = patientCollection.getPNUM();

					// request.setRegistrationDate(patientCollection.getRegistrationDate());
					BeanUtil.map(request, patientCollection);
					patientCollection.setId(patientId);
					patientCollection.setUpdatedTime(new Date());
					patientCollection.setDoctorId(patientDoctorId);
					if (request.getRegistrationDate() != null)
						patientCollection.setRegistrationDate(request.getRegistrationDate());
					else
						patientCollection.setRegistrationDate(new Date().getTime());

				} else {
					patientCollection = new PatientCollection();
					patientCollection.setCreatedTime(new Date());
					BeanUtil.map(request, patientCollection);
					if (request.getRegistrationDate() != null)
						patientCollection.setRegistrationDate(request.getRegistrationDate());
					else
						patientCollection.setRegistrationDate(new Date().getTime());
				}

				patientCollection.setRelations(request.getRelations());
				patientCollection.setNotes(request.getNotes());

				Map<String, String> generatedId = patientIdGenerator(request.getLocationId(), request.getHospitalId(),
						patientCollection.getRegistrationDate(), PID, PNUM);

				patientCollection.setPID(generatedId.get("PID"));
				patientCollection.setPNUM(generatedId.get("PNUM"));

				if (!DPDoctorUtils.anyStringEmpty(request.getProfession())) {
					patientCollection.setProfession(request.getProfession());
				}

				ReferencesCollection referencesCollection = null;
				if (request.getReferredBy() != null) {
					if (request.getReferredBy().getId() != null) {
						referencesCollection = referrenceRepository
								.findById(new ObjectId(request.getReferredBy().getId())).orElse(null);
					}
					if (referencesCollection == null) {
						referencesCollection = new ReferencesCollection();
						BeanUtil.map(request.getReferredBy(), referencesCollection);
						BeanUtil.map(request, referencesCollection);
						referencesCollection.setId(null);
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
							referencesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
							referencesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
							referencesCollection.setLocationId(new ObjectId(request.getLocationId()));
						referencesCollection = referrenceRepository.save(referencesCollection);
						transnationalService.addResource(referencesCollection.getId(), Resource.REFERENCE, false);
						ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
						BeanUtil.map(referencesCollection, esReferenceDocument);
						esRegRistrationService.addEditReference(esReferenceDocument);
					}
					patientCollection.setReferredBy(referencesCollection.getId());
				}

				// assign groups
				if (request.getGroups() != null) {

					List<String> groupIds = new ArrayList<String>();
					List<PatientGroupCollection> patientGroupCollections = patientGroupRepository
							.findByPatientIdAndDiscardedIsFalse(patientCollection.getUserId());
					if (patientGroupCollections != null && !patientGroupCollections.isEmpty()) {
						for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
							if (request.getGroups() != null && !request.getGroups().isEmpty()) {
								groupIds.add(patientGroupCollection.getGroupId().toString());
								if (!request.getGroups().contains(patientGroupCollection.getGroupId().toString())) {
									patientGroupRepository.delete(patientGroupCollection);
								}
							} else {
								patientGroupRepository.delete(patientGroupCollection);
							}
						}
					}

					if (request.getGroups() != null && !request.getGroups().isEmpty()) {
						for (String group : request.getGroups()) {
							if (!groupIds.contains(group)) {
								PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
								patientGroupCollection.setGroupId(new ObjectId(group));
								patientGroupCollection.setPatientId(patientCollection.getUserId());
								patientGroupRepository.save(patientGroupCollection);
							}
						}
					}
				}
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getUserId())).orElse(null);
				if (userCollection == null) {
					logger.error("Incorrect User Id");
					throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
				}
				/* registeredPatientDetails = new RegisteredPatientDetails(); */
				BeanUtil.map(userCollection, registeredPatientDetails);
				if (request.getImage() != null) {
					String path = "profile-images";
					// save image
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					patientCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setImageUrl(null);
					registeredPatientDetails.setImageUrl(imageURLResponse.getImageUrl());
					patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
					userCollection.setThumbnailUrl(null);
					registeredPatientDetails.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
				}

				if (!userCollection.isSignedUp()) {
					userCollection.setFirstName(request.getLocalPatientName());
					userCollection.setUpdatedTime(new Date());
					userCollection = userRepository.save(userCollection);
				}
				registeredPatientDetails.setCreatedBy(request.getCreatedBy());
				registeredPatientDetails.setUserId(userCollection.getId().toString());
				patientCollection.setFirstName(userCollection.getFirstName());

				// if(RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(request.getRole())){
				List<ObjectId> consultantDoctorIds = patientCollection.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(new ObjectId(request.getDoctorId())))
					consultantDoctorIds.add(new ObjectId(request.getDoctorId()));
				patientCollection.setConsultantDoctorIds(consultantDoctorIds);
				// }

				patientCollection = patientRepository.save(patientCollection);
				registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
				registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
				patient.setPatientId(userCollection.getId().toString());

				Integer prescriptionCount = 0, clinicalNotesCount = 0, recordsCount = 0;
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(),
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
				} else {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherLocations(
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherLocations(
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					recordsCount = recordsRepository.getRecordsForOtherLocations(userCollection.getId(),
							patientCollection.getHospitalId(), patientCollection.getLocationId());
				}

				if ((prescriptionCount != null && prescriptionCount > 0)
						|| (clinicalNotesCount != null && clinicalNotesCount > 0)
						|| (recordsCount != null && recordsCount > 0))
					patient.setIsDataAvailableWithOtherDoctor(true);

				patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(),
						patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(),
						userCollection.getId().toString()));

				registeredPatientDetails.setPatient(patient);
				registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
				registeredPatientDetails.setDob(patientCollection.getDob());
				registeredPatientDetails.setGender(patientCollection.getGender());
				registeredPatientDetails.setPID(patientCollection.getPID());
				registeredPatientDetails.setPNUM(patientCollection.getPNUM());
				registeredPatientDetails.setConsultantDoctorIds(patient.getConsultantDoctorIds());
				registeredPatientDetails.setIsChild(patientCollection.getIsChild());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
					registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
					registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
					registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
				registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
				if (referencesCollection != null) {
					Reference reference = new Reference();
					BeanUtil.map(referencesCollection, reference);
					registeredPatientDetails.setReferredBy(reference);
				}
				registeredPatientDetails.setAddress(patientCollection.getAddress());
				if (request.getGroups() != null) {
					List<ObjectId> groupObjectIds = new ArrayList<ObjectId>();
					for (String groupId : request.getGroups())
						groupObjectIds.add(new ObjectId(groupId));
					groups = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupObjectIds))),
							GroupCollection.class, Group.class).getMappedResults();
					registeredPatientDetails.setGroups(groups);
				}
			}
//			if (request.getRecordType() != null && !DPDoctorUtils.anyStringEmpty(request.getRecordId())) {
//				if (request.getRecordType().equals(ComponentType.DOCTOR_LAB_REPORTS)) {
//					DoctorLabReportCollection doctorLabReportCollection = doctorLabReportRepository
//							.findById(new ObjectId(request.getRecordId())).orElse(null);
//					doctorLabReportCollection.setPatientId(new ObjectId(registeredPatientDetails.getUserId()));
//					doctorLabReportRepository.save(doctorLabReportCollection);
//				}
//			}
//			pushNotificationServices.notifyUser(request.getDoctorId(), "New patient created.",
//					ComponentType.PATIENT_REFRESH.getType(), null, null);

			if (request.getLocationId() != null) {
				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getLocationId())).orElse(null);
				if (locationCollection.getIsPatientWelcomeMessageOn() != null) {
					if (locationCollection.getIsPatientWelcomeMessageOn().equals(Boolean.TRUE)) {
						sendWelcomeMessageToPatient(patientCollection, locationCollection, request.getMobileNumber());
					}
				}

			}

			if (request.getIsChild() != null && request.getIsChild() == true) {
				createImmunisationChart(registeredPatientDetails);
				createBabyAchievementChart(registeredPatientDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	@Async
	@Transactional
	private void createImmunisationChart(RegisteredPatientDetails request) {
		List<VaccineCollection> vaccineCollections = new ArrayList<>();

		Calendar calendar = new GregorianCalendar();
		if (request.getDob() != null) {
			calendar.set(request.getDob().getYears(), request.getDob().getMonths() - 1, request.getDob().getDays(), 0,
					0);
		}

		vaccineCollections = vaccineRepository.findByPatientId(new ObjectId(request.getUserId()));
		if (vaccineCollections == null || vaccineCollections.isEmpty()) {
			vaccineCollections = new ArrayList<>();
			List<MasterBabyImmunizationCollection> babyImmunizationCollections = masterBabyImmunizationRepository
					.findByIsChartVaccine(true);
			for (MasterBabyImmunizationCollection masterBabyImmunizationCollection : babyImmunizationCollections) {
				VaccineCollection vaccineCollection = new VaccineCollection();
				vaccineCollection.setPatientId(new ObjectId(request.getUserId()));
				// vaccineCollection.setLocationId(new ObjectId(request.getLocationId()));
				// vaccineCollection.setHospitalId(new ObjectId(request.getHospitalId()));
				// vaccineCollection.setDoctorId(new ObjectId(request.getDoctorId()));
				vaccineCollection.setVaccineId(masterBabyImmunizationCollection.getId());
				vaccineCollection.setLongName(masterBabyImmunizationCollection.getLongName());
				vaccineCollection.setName(masterBabyImmunizationCollection.getName());
				vaccineCollection.setDuration(masterBabyImmunizationCollection.getDuration());
				vaccineCollection.setPeriodTime(masterBabyImmunizationCollection.getPeriodTime());
				DateTime dueDate = new DateTime(calendar);
				dueDate = dueDate.plusWeeks(masterBabyImmunizationCollection.getPeriodTime());
				vaccineCollection.setDueDate(dueDate.toDate());
				vaccineCollection.setCreatedTime(new Date());
				vaccineCollections.add(vaccineCollection);
			}
		} else {
			for (VaccineCollection vaccineCollection : vaccineCollections) {
				if (vaccineCollection.getPeriodTime() != null) {
					DateTime dueDate = new DateTime(calendar);
					dueDate = dueDate.plusWeeks(vaccineCollection.getPeriodTime());
					vaccineCollection.setDueDate(dueDate.toDate());
				}
			}
		}
		vaccineRepository.saveAll(vaccineCollections);
	}

	@Async
	@Transactional
	private void createBabyAchievementChart(RegisteredPatientDetails request) {
		List<BirthAchievementCollection> birthAchievementCollections = null;
		birthAchievementCollections = birthAchievementRepository.findByPatientId(new ObjectId(request.getUserId()));
		if (birthAchievementCollections == null || birthAchievementCollections.isEmpty()) {
			birthAchievementCollections = new ArrayList<>();
			List<MasterBabyAchievementCollection> babyAchievementCollections = masterBabyAchievementRepository
					.findAll();
			for (MasterBabyAchievementCollection masterBabyAchievementCollection : babyAchievementCollections) {
				BirthAchievementCollection birthAchievementCollection = new BirthAchievementCollection();
				birthAchievementCollection.setPatientId(new ObjectId(request.getUserId()));
				birthAchievementCollection.setAchievement(masterBabyAchievementCollection.getAchievement());
				birthAchievementCollection.setNote(masterBabyAchievementCollection.getNote());

				birthAchievementCollections.add(birthAchievementCollection);
			}
		} /*
			 * else { for (VaccineCollection vaccineCollection : vaccineCollections) { if
			 * (vaccineCollection.getPeriodTime() != null) { DateTime dueDate = new
			 * DateTime(calendar); dueDate =
			 * dueDate.plusWeeks(vaccineCollection.getPeriodTime());
			 * vaccineCollection.setDueDate(dueDate.toDate()); } } }
			 */

		birthAchievementRepository.saveAll(birthAchievementCollections);
	}

	private void sendWelcomeMessageToPatient(PatientCollection patientCollection, LocationCollection locationCollection,
			String mobileNumber) {
		try {

			if (patientCollection != null) {
				String message = "Dear " + patientCollection.getLocalPatientName() + ",Thank you for visiting "
						+ locationCollection.getLocationName() + "Call " + locationCollection.getClinicNumber()
						+ " for queries.";
				;
				message = StringEscapeUtils.unescapeJava(message);
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setDoctorId(patientCollection.getDoctorId());
				smsTrackDetail.setLocationId(patientCollection.getLocationId());
				smsTrackDetail.setHospitalId(patientCollection.getHospitalId());
				smsTrackDetail.setType("APP_LINK_THROUGH_PRESCRIPTION");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(patientCollection.getUserId());
				SMS sms = new SMS();
				smsDetail.setUserName(patientCollection.getLocalPatientName());
//				message = message.replace("{patientName}", patientCollection.getLocalPatientName());
//				message = message.replace("{clinicName}", locationCollection.getLocationName());
//				message = message.replace("{clinicNumber}", locationCollection.getClinicNumber());
				sms.setSmsText(message);

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(mobileNumber);
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Map<String, String> patientIdGenerator(String locationId, String hospitalId, Long registrationDate,
			String PID, String PNUM) {
		Map<String, String> generatedId = new HashMap<String, String>();
		try {

			if (DPDoctorUtils.anyStringEmpty(PID, PNUM)) {
				ObjectId locationObjectId = null, hospitalObjectId = null;
				if (!DPDoctorUtils.anyStringEmpty(locationId))
					locationObjectId = new ObjectId(locationId);
				if (!DPDoctorUtils.anyStringEmpty(hospitalId))
					hospitalObjectId = new ObjectId(hospitalId);

				LocationCollection location = locationRepository.findById(locationObjectId).orElse(null);
				if (location == null) {
					logger.warn("Invalid Location Id");
					throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
				}

				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				if (registrationDate != null) {
					localCalendar.setTime(new Date(registrationDate));
				} else {
					localCalendar.setTime(new Date());
				}

				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				Long startTimeinMillis = start.getMillis();
				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				Long endTimeinMillis = end.getMillis();
				Integer patientSize = patientRepository.findTodaysRegisteredPatient(locationObjectId, hospitalObjectId,
						startTimeinMillis, endTimeinMillis);

				if (patientSize == null)
					patientSize = 0;

				String patientInitial = location.getPatientInitial();

				if (DPDoctorUtils.anyStringEmpty(PID)) {
					PID = patientInitial + DPDoctorUtils.getPrefixedNumber(currentDay)
							+ DPDoctorUtils.getPrefixedNumber(currentMonth)
							+ DPDoctorUtils.getPrefixedNumber(currentYear % 100)
							+ DPDoctorUtils.getPrefixedNumber(patientSize + 1);
				}

				if (DPDoctorUtils.anyStringEmpty(PNUM)) {
					int patientCounter = location.getPatientCounter();
					List<PatientCollection> patientCollections = patientRepository
							.findByLocationIdAndHospitalIdAndPNUMNotNull(locationObjectId, hospitalObjectId,
									PageRequest.of(0, 1, Direction.DESC, "createdTime"));
					if (patientCollections != null && !patientCollections.isEmpty()) {
						PatientCollection patientCollection = patientCollections.get(0);
						String lastRegisterdPatientPNUM = patientCollection.getPNUM().replaceAll("[a-zA-Z\\s\\W_]", "");
						Integer lastRegisterdPatientPNUMCount = 0;
						if (!DPDoctorUtils.anyStringEmpty(lastRegisterdPatientPNUM))
							lastRegisterdPatientPNUMCount = Integer.parseInt(lastRegisterdPatientPNUM);
						if (lastRegisterdPatientPNUMCount < patientCounter) {
							patientSize = patientCounter;
						} else {
							patientSize = lastRegisterdPatientPNUMCount + 1;
						}
					}
					PNUM = patientInitial + patientSize;
				}
			}
			generatedId.put("PID", PID);
			generatedId.put("PNUM", PNUM);
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generatedId;
	}

	public RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request) {
		RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
		List<Group> groups = null;
		try {
			// get role of specified type
			RoleCollection roleCollection = roleRepository
					.findByRoleAndLocationIdIsNullAndHospitalIdIsNull(RoleEnum.PATIENT.getRole());
			if (roleCollection == null) {
				logger.warn(role);
				throw new BusinessException(ServiceError.NoRecord, role);
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getPID())) {
				Integer count = patientRepository.findPatientByPID(new ObjectId(request.getDoctorId()),
						new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()), request.getPID());
				if (count != null && count > 0) {
					logger.warn("Patient with this PID is already present. Please add patient different PID");
					throw new BusinessException(ServiceError.InvalidInput,
							"Patient with this PID is already present. Please add patient different PID");
				}
			}

			request.setFirstName(request.getLocalPatientName());

			Date createdTime = new Date();

			CheckPatientSignUpResponse checkPatientSignUpResponse = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getMobileNumber()))
				checkPatientSignUpResponse = checkIfPatientIsSignedUp(request.getMobileNumber());
			// save user

			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			if (request.getDob() != null && request.getDob().getAge() != null
					&& request.getDob().getAge().getYears() < 0) {
				logger.warn(DOB);
				throw new BusinessException(ServiceError.InvalidInput, DOB);
			} else if (request.getDob() == null && request.getAge() != null) {
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR) - request.getAge();
				request.setDob(new DOB(currentDay, currentMonth, currentYear));
			}
			User user = new User();
			BeanUtil.map(request, user);
			user.setFirstName(request.getLocalPatientName());
			String uniqueUserName = generateUniqueUserNameService.generate(user);
			userCollection.setUserName(uniqueUserName);
			// userCollection.setPassword(generateRandomAlphanumericString(10));
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
			userCollection.setIsActive(true);
			userCollection.setIsDentalChainPatient(true);
			userCollection.setCreatedTime(createdTime);
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			if (checkPatientSignUpResponse != null) {
				userCollection.setSignedUp(checkPatientSignUpResponse.isSignedUp());
				userCollection.setPassword(checkPatientSignUpResponse.getPassword());
				userCollection.setSalt(checkPatientSignUpResponse.getSalt());
			}
			userCollection.setFirstName(request.getLocalPatientName());
			System.out.println("request.getCreatedBy" + request.getCreatedBy());

			userCollection.setCreatedBy(request.getCreatedBy());
			userCollection = userRepository.save(userCollection);

			// assign roles
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(),
					roleCollection.getId(), null, null);
			userRoleCollection.setCreatedTime(new Date());

			userRoleRepository.save(userRoleCollection);

			if (checkPatientSignUpResponse != null) {
				PatientCollection patientCollection = new PatientCollection();
				patientCollection.setCreatedTime(new Date());
				patientCollection.setFirstName(request.getLocalPatientName());
				patientCollection.setLocalPatientName(request.getLocalPatientName());
				patientCollection.setUserId(userCollection.getId());
				patientCollection.setRegistrationDate(request.getRegistrationDate());
				patientRepository.save(patientCollection);
			}
			// save Patient Info
			PatientCollection patientCollection = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setFirstName(request.getLocalPatientName());
			patientCollection.setUserId(userCollection.getId());
			patientCollection.setIsChild(request.getIsChild());
			if (request.getRegistrationDate() != null)
				patientCollection.setRegistrationDate(request.getRegistrationDate());
			else
				patientCollection.setRegistrationDate(new Date().getTime());

			patientCollection.setCreatedTime(createdTime);
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId())
					&& !DPDoctorUtils.anyStringEmpty(request.getHospitalId())) {
				Map<String, String> generatedId = patientIdGenerator(request.getLocationId(), request.getHospitalId(),
						patientCollection.getRegistrationDate(), request.getPID(), request.getPNUM());
				patientCollection.setPID(generatedId.get("PID"));
				patientCollection.setPNUM(generatedId.get("PNUM"));
			}
			// if(RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(request.getRole())){
			List<ObjectId> consultantDoctorIds = new ArrayList<ObjectId>();
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				consultantDoctorIds.add(new ObjectId(request.getDoctorId()));
			patientCollection.setConsultantDoctorIds(consultantDoctorIds);
			// }
			if (!DPDoctorUtils.anyStringEmpty(request.getProfession())) {
				patientCollection.setProfession(request.getProfession());
			}
			patientCollection.setNotes(request.getNotes());
			if (request.getImage() != null) {
				String path = "profile-images";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				patientCollection.setImageUrl(imageURLResponse.getImageUrl());
				patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
			}

			ReferencesCollection referencesCollection = null;
			if (request.getReferredBy() != null) {
				if (request.getReferredBy().getId() != null) {
					referencesCollection = referrenceRepository.findById(new ObjectId(request.getReferredBy().getId()))
							.orElse(null);
				}
				if (referencesCollection == null) {
					referencesCollection = new ReferencesCollection();
					BeanUtil.map(request.getReferredBy(), referencesCollection);
					if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
						referencesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
					if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
						referencesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
						referencesCollection.setLocationId(new ObjectId(request.getLocationId()));
					referencesCollection = referrenceRepository.save(referencesCollection);
					transnationalService.addResource(referencesCollection.getId(), Resource.REFERENCE, false);
					ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
					BeanUtil.map(referencesCollection, esReferenceDocument);
					esRegRistrationService.addEditReference(esReferenceDocument);
				}

				patientCollection.setReferredBy(referencesCollection.getId());
			}
			patientCollection = patientRepository.save(patientCollection);

			// assign groups
			if (request.getGroups() != null) {
				for (String group : request.getGroups()) {
					PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
					patientGroupCollection.setGroupId(new ObjectId(group));
					patientGroupCollection.setPatientId(patientCollection.getUserId());
					patientGroupCollection.setCreatedTime(new Date());
					patientGroupCollection = patientGroupRepository.save(patientGroupCollection);
				}
			}

			// send SMS logic
			BeanUtil.map(userCollection, registeredPatientDetails);
			registeredPatientDetails.setUserId(userCollection.getId().toString());
			Patient patient = new Patient();
			BeanUtil.map(patientCollection, patient);
			patient.setPatientId(userCollection.getId().toString());

			Integer prescriptionCount = 0, clinicalNotesCount = 0, recordsCount = 0;
			if (!DPDoctorUtils.anyStringEmpty(patient.getDoctorId())) {
				prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(
						patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
						patientCollection.getLocationId());
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(
						patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
						patientCollection.getLocationId());
				recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(),
						userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
			} else {
				prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherLocations(userCollection.getId(),
						patientCollection.getHospitalId(), patientCollection.getLocationId());
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherLocations(
						userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
				recordsCount = recordsRepository.getRecordsForOtherLocations(userCollection.getId(),
						patientCollection.getHospitalId(), patientCollection.getLocationId());
			}

			if ((prescriptionCount != null && prescriptionCount > 0)
					|| (clinicalNotesCount != null && clinicalNotesCount > 0)
					|| (recordsCount != null && recordsCount > 0))
				patient.setIsDataAvailableWithOtherDoctor(true);

//			patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(),
//					patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(),
//					userCollection.getId().toString()));

			registeredPatientDetails.setPatient(patient);
			registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
			registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
			registeredPatientDetails.setDob(patientCollection.getDob());
			registeredPatientDetails.setGender(patientCollection.getGender());
			registeredPatientDetails.setPID(patientCollection.getPID());
			registeredPatientDetails.setPNUM(patientCollection.getPNUM());
			registeredPatientDetails.setConsultantDoctorIds(patient.getConsultantDoctorIds());
			if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
				registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
			if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
				registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
			if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
				registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
			registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
			registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
			registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
			if (referencesCollection != null) {
				Reference reference = new Reference();
				BeanUtil.map(referencesCollection, reference);
				registeredPatientDetails.setReferredBy(reference);
			}
			registeredPatientDetails.setAddress(patientCollection.getAddress());

			if (request.getGroups() != null) {
				List<ObjectId> groupObjectIds = new ArrayList<ObjectId>();
				for (String groupId : request.getGroups())
					groupObjectIds.add(new ObjectId(groupId));
				groups = mongoTemplate
						.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupObjectIds))),
								GroupCollection.class, Group.class)
						.getMappedResults();
			}
			registeredPatientDetails.setGroups(groups);

			if (request.getLocationId() != null) {
				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getLocationId())).orElse(null);

				if (locationCollection.getIsPatientWelcomeMessageOn() != null) {
					if (locationCollection.getIsPatientWelcomeMessageOn().equals(Boolean.TRUE)) {
						sendWelcomeMessageToPatient(patientCollection, locationCollection, request.getMobileNumber());
					}
				}

				pushNotificationServices.notifyUser(request.getDoctorId(), "New patient created.",
						ComponentType.PATIENT_REFRESH.getType(), null, null);
				pushNotificationServices.notifyUser(patientCollection.getUserId().toString(),
						"Welcome to " + locationCollection.getLocationName()
								+ ", let us know about your visit. We will be happy to serve you again.",
						ComponentType.PATIENT.getType(), patientCollection.getUserId().toString(), null);
			}

			if (request.getRecordType() != null && !DPDoctorUtils.anyStringEmpty(request.getRecordId())) {
				if (request.getRecordType().equals(ComponentType.DOCTOR_LAB_REPORTS)) {
					DoctorLabReportCollection doctorLabReportCollection = doctorLabReportRepository
							.findById(new ObjectId(request.getRecordId())).orElse(null);
					doctorLabReportCollection.setPatientId(new ObjectId(registeredPatientDetails.getUserId()));
					doctorLabReportRepository.save(doctorLabReportCollection);
				}
			}

			pushNotificationServices.notifyUser(request.getDoctorId(), "New patient created.",
					ComponentType.PATIENT_REFRESH.getType(), null, null);

			if (request.getIsChild() == true) {
				createImmunisationChart(registeredPatientDetails);
				createBabyAchievementChart(registeredPatientDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	private CheckPatientSignUpResponse checkIfPatientIsSignedUp(String mobileNumber) {
		CheckPatientSignUpResponse response = null;
		try {
			List<UserCollection> userCollections = userRepositoryForAppoint.findByMobileNumberAndUserState(mobileNumber,
					UserState.USERSTATECOMPLETE.getState());
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (userCollection.getEmailAddress() != null) {
						if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
							if (userCollection.isSignedUp()) {
								response = new CheckPatientSignUpResponse(userCollection.getPassword(),
										userCollection.getSalt(), userCollection.isSignedUp());
							}
							break;
						}
					} else {
						if (userCollection.isSignedUp()) {
							response = new CheckPatientSignUpResponse(userCollection.getPassword(),
									userCollection.getSalt(), userCollection.isSignedUp());
						}
						break;
					}
				}
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
	public void checkPatientCount(String mobileNumber) {

		int count = 0;

		List<UserCollection> userCollections = null;
		Criteria criteria = new Criteria();
		if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
			criteria.and("mobileNumber").is(mobileNumber);

		criteria.and("userState").is(UserState.USERSTATECOMPLETE.getState());
		userCollections = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
				UserCollection.class, UserCollection.class).getMappedResults();

		if (userCollections != null && !userCollections.isEmpty()) {
			for (UserCollection userCollection : userCollections) {
				if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress()))
					count++;
			}
			if (count >= Integer.parseInt(patientCount)) {
				logger.warn(checkPatientCount);
				throw new BusinessException(ServiceError.Unknown, checkPatientCount);
			}
		}
	}

	@Override
	public List<RegisteredPatientDetails> getPatientsByPhoneNumber(Boolean isDentalChainPatient, String mobileNumber) {
		List<RegisteredPatientDetails> users = null;
		try {
			List<UserCollection> userCollections = null;
			Criteria criteria = new Criteria();
			if (isDentalChainPatient != null)
				criteria.and("isDentalChainPatient").is(isDentalChainPatient);
			if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
				criteria.and("mobileNumber").is(mobileNumber);

			criteria.and("userState").is(UserState.USERSTATECOMPLETE.getState());
			userCollections = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					UserCollection.class, UserCollection.class).getMappedResults();

			if (userCollections != null) {
				users = new ArrayList<RegisteredPatientDetails>();
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						RegisteredPatientDetails user = new RegisteredPatientDetails();
						Patient patient = new Patient();
						PatientCollection patientCollection = patientRepository
								.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userCollection.getId(), null, null,
										null);

						if (patientCollection != null) {
							BeanUtil.map(patientCollection, patient);
							BeanUtil.map(patientCollection, user);
							BeanUtil.map(userCollection, user);
							if (patientCollection.getLocationId() != null) {
								user.setLocationId(patientCollection.getLocationId().toString());
							}
							if (patientCollection.getHospitalId() != null) {
								user.setHospitalId(patientCollection.getHospitalId().toString());
							}
							user.setImageUrl(patientCollection.getImageUrl());
							user.setThumbnailUrl(patientCollection.getThumbnailUrl());
						} else {
							BeanUtil.map(userCollection, user);
						}
						patient.setPatientId(userCollection.getId().toString());
						user.setPatient(patient);
						users.add(user);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return users;
	}

	@Override
	public List<UserAddress> getUserAddress(String userId, String mobileNumber, Boolean discarded) {
		List<UserAddress> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria.and("userIds").is(new ObjectId(userId));
			if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
				criteria.and("mobileNumber").is(mobileNumber);

			if (!discarded)
				criteria.and("discarded").is(discarded);
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					UserAddressCollection.class, UserAddress.class).getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (UserAddress userAddress : response) {
					Address address = userAddress.getAddress();
					String formattedAddress = (!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
							? address.getStreetAddress() + ", "
							: "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLandmarkDetails())
									? address.getLandmarkDetails() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ? address.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode() : "");

					if (formattedAddress.charAt(formattedAddress.length() - 2) == ',') {
						formattedAddress = formattedAddress.substring(0, formattedAddress.length() - 2);
					}
					userAddress.setFormattedAddress(formattedAddress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting user address");
		}
		return response;
	}

	@Override
	public Boolean updatePatientContactState(String patientId, String contactState) {
		Boolean response = false;
		UserCollection userCollection = null;
		try {
			userCollection = userRepository.findById(new ObjectId(patientId)).orElse(null);
			if (userCollection != null) {
				userCollection.setContactState(ContactState.valueOf(contactState));
				userCollection.setUpdatedTime(new Date());
				userRepository.save(userCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public ClinicTreatmentRatelist updateClinicTreatmentRatelist(ClinicTreatmentRatelist request) {
		ClinicTreatmentRatelist response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (locationCollection != null)
				BeanUtil.map(request, locationCollection);
			locationCollection.setRatelistId(new ObjectId(request.getRatelistId()));
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicTreatmentRatelist();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

}
